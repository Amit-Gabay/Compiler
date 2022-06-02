package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import IR.*;
import TEMP.*;

public class AST_VARDEC extends AST_Node
{
	public AST_TYPE type;
	public String ID;
	public AST_EXP expression;
	public AST_NEWEXP newExpression;

	/* The AST annotations */
	public KindEnum var_kind;
	public int var_index;
	public String var_label;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_VARDEC(AST_TYPE type, String ID, AST_EXP expression, AST_NEWEXP newExpression, int line)
	{
		this.line = line;
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/
		this.type = type;
		this.ID = ID;
		this.expression = expression;
		this.newExpression = newExpression;
	}
	
	/**************************************************/
	/* The printing message for a DEC VARDEC AST node */
	/**************************************************/
	public void PrintMe()
	{
		/**********************************************************/
		/* RECURSIVELY PRINT type + expression + newExpression... */
		/**********************************************************/
		if (type != null) type.PrintMe();
		if (expression != null) expression.PrintMe();
		if (newExpression != null) newExpression.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("VARDEC(%s)",ID));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (type  != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,type.SerialNumber);
		if (expression != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,expression.SerialNumber);
		if (newExpression != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,newExpression.SerialNumber);
	}

	public TYPE SemantMe() throws SemanticException
	{
		boolean is_instancable;
		boolean is_equals_class_name = false;
		TYPE newExp_type = null;
		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		/* Check if the var type is same as the class type which we are in (If we are in class scope) */
		if (SYMBOL_TABLE.getInstance().current_class != null && type.typeID != null)
		{
			is_equals_class_name = SYMBOL_TABLE.getInstance().current_class.name.equals(type.typeID);
		}

		is_instancable = SYMBOL_TABLE.getInstance().isInstancable(type.getName());

		/* Check if the variable type is a valid type (Declared and instance-able type) */
		if (!is_equals_class_name && !is_instancable)
		{
			throw new SemanticException(line);
		}

		TYPE type_semant = type.SemantMe();		

		/* Make sure that the variable type is NOT void */
		if (type_semant.typeEnum == TypeEnum.TYPE_VOID)
		{
			throw new SemanticException(line);
		}

		/* If there is an expression, make sure that its type matches the var type */
		TYPE exp_type = null;
		if (expression != null)
		{
			exp_type = expression.SemantMe();
			if (exp_type.typeEnum != type_semant.typeEnum)
			{
				if (exp_type.typeEnum == TypeEnum.TYPE_NIL)
				{
					if (type_semant.typeEnum != TypeEnum.TYPE_CLASS && type_semant.typeEnum != TypeEnum.TYPE_ARRAY)
					{
						throw new SemanticException(line);
					}
				}
				else
				{
					throw new SemanticException(line);
				}
			}

			else if (exp_type.typeEnum == TypeEnum.TYPE_CLASS)
			{
				TYPE_CLASS exp_class = (TYPE_CLASS) exp_type;
				TYPE_CLASS var_class = (TYPE_CLASS) type_semant;
				if (!var_class.is_replacable(exp_class)) throw new SemanticException(line);
			}

			else if (exp_type.typeEnum == TypeEnum.TYPE_ARRAY)
			{
				TYPE_ARRAY exp_array = (TYPE_ARRAY) exp_type;
				TYPE_ARRAY var_array = (TYPE_ARRAY) type_semant;
				if (!var_array.is_replacable(exp_array)) throw new SemanticException(line);
			}
		}

		else if (newExpression != null)
		{
			newExp_type = newExpression.SemantMe();
			if (newExp_type.typeEnum != type_semant.typeEnum)
			{
				throw new SemanticException(line);
			}
			else if (newExp_type.typeEnum == TypeEnum.TYPE_CLASS)
			{
				TYPE_CLASS newexp_class = (TYPE_CLASS) newExp_type;
				TYPE_CLASS var_class = (TYPE_CLASS) type_semant;
				if (!var_class.is_replacable(newexp_class)) throw new SemanticException(line);
			}

			else if (newExp_type.typeEnum == TypeEnum.TYPE_ARRAY)
			{
				TYPE_ARRAY newexp_array = (TYPE_ARRAY) newExp_type;
				TYPE_ARRAY var_array = (TYPE_ARRAY) type_semant;
				if (!var_array.is_replacable_new(newexp_array))
				{
					throw new SemanticException(line);
				}
			}
		}

		/**************************************/
		/* [2] Check That Name does NOT exist */
		/**************************************/
		TYPE found_duplicate = SYMBOL_TABLE.getInstance().findInCurrentScope(ID);
		if (found_duplicate != null)
		{
			if (!SYMBOL_TABLE.getInstance().isInstancable(ID))
			{
				throw new SemanticException(line);
			}
		}

		/***************************************************/
		/* [3] Enter the variable Type to the Symbol Table */
		/***************************************************/
		KindEnum var_kind = KindEnum.GLOBAL;
		int var_index = 0;
		String var_label = null;

		/* If we are in a class scope (and not in a method scope) */
		if (SYMBOL_TABLE.getInstance().current_class != null && SYMBOL_TABLE.getInstance().current_function == null)
		{
			var_kind = KindEnum.FIELD;
			var_index = SYMBOL_TABLE.getInstance().current_class.fields_counter;
			SYMBOL_TABLE.getInstance().current_class.fields_counter++;
		}
		/* Else, if we are in a function / method scope */
		else if (SYMBOL_TABLE.getInstance().current_function != null)
		{
			var_kind = KindEnum.LOCAL;
			var_index = SYMBOL_TABLE.getInstance().current_function.local_counter;
			SYMBOL_TABLE.getInstance().current_function.local_counter++;
		}

		else
		{
			var_label = "_"+ID;
		}

		TYPE_VAR var_type = new TYPE_VAR(type_semant, ID);

		var_type.var_kind = var_kind;
		var_type.var_index = var_index;
		var_type.var_label = var_label;
		/* Add the AST annotations */
		this.var_kind = var_kind;
		this.var_index = var_index;
		this.var_label = var_label;

		/* If it's a class field declaration with default value assigment */
		if (expression != null && SYMBOL_TABLE.getInstance().current_class != null && SYMBOL_TABLE.getInstance().current_function == null)
		{
			/* If it's an int assigment, it has to constant int */
			if (exp_type.typeEnum == TypeEnum.TYPE_INT)
			{
				AST_EXP_INT constant_int = (AST_EXP_INT) expression;
				var_type.default_int_value = constant_int.value;
			}

			/* If it's a string assigment, it has to constant string */
			else if (exp_type.typeEnum == TypeEnum.TYPE_STRING)
			{
				AST_EXP_STRING constant_string = (AST_EXP_STRING) expression;
				var_type.default_string_label = constant_string.string_label;
			}

			else if (exp_type.typeEnum == TypeEnum.TYPE_NIL)
			{
				var_type.default_int_value = 0;
			}
		}

		SYMBOL_TABLE.getInstance().enter(ID, var_type, false, SYMBOL_TABLE.getInstance().current_scope);

		/*********************************************************/
		/* [4] Return value is irrelevant for class declarations */
		/*********************************************************/
		return var_type;
	}

	public TEMP IRme()
	{
		/* If got here it's a local / global variable (parameters are typeID) */
		TEMP assignment_register = null;

		/* Declare the variable */

		/* If it's a global variable */
		if (var_kind == KindEnum.GLOBAL)
		{
			/* If it's a global variable, then newExpression = null, and the expression has to be CONSTANT */
			if (expression == null)
			{
				IR.getInstance().Add_IRcommand(new IRcommand_Declare_Global_Var(var_label, 0));
			}
			else if (expression instanceof AST_EXP_INT)
			{
				int int_default_value = ((AST_EXP_INT) expression).value;
				IR.getInstance().Add_IRcommand(new IRcommand_Declare_Global_Var(var_label, int_default_value));
			}
			else if (expression instanceof AST_EXP_STRING)
			{
				String str_default_label = ((AST_EXP_STRING) expression).string_label;
				String str_default_value = ((AST_EXP_STRING) expression).string;
				IR.getInstance().Add_IRcommand(new IRcommand_Allocate_String(str_default_label, str_default_value));
				IR.getInstance().Add_IRcommand(new IRcommand_Declare_Global_Var(var_label, str_default_label));
			}
			else /* expression instanceof AST_EXP_NIL */
			{
				IR.getInstance().Add_IRcommand(new IRcommand_Declare_Global_Var(var_label, 0));
			}
		}

		/* Else, if it's a local variable */
		else if (var_kind == KindEnum.LOCAL)
		{
			if (expression == null && newExpression == null)
			{
				IR.getInstance().Add_IRcommand(new IRcommand_Local_Var_Set(var_index, 0));
			}

			else
			{
				/* Evaluate the default assignment (If there is) */
				if (newExpression != null)
				{
					assignment_register = newExpression.IRme();
				}

				else if (expression != null)
				{
					assignment_register = expression.IRme();
				}

				IR.getInstance().Add_IRcommand(new IRcommand_Local_Var_Set(var_index, assignment_register));
			}
		}

		else if (var_kind == KindEnum.FIELD)
		{
			if (expression != null && expression instanceof AST_EXP_STRING)
			{
				String str_default_label = ((AST_EXP_STRING) expression).string_label;
				String str_default_value = ((AST_EXP_STRING) expression).string;
				IR.getInstance().Add_IRcommand(new IRcommand_Allocate_String(str_default_label, str_default_value));
			}
		}

		return null;
	}
}
