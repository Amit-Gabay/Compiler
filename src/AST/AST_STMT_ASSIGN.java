package AST;

import TYPES.*;
import IR.*;
import TEMP.*;
import SYMBOL_TABLE.*;

public class AST_STMT_ASSIGN extends AST_STMT
{
	/***************/
	/*  var := exp */
	/***************/
	public AST_VAR var;
	public AST_EXP exp;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_ASSIGN(AST_VAR var,AST_EXP exp, int line)
	{
		this.line = line;
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.var = var;
		this.exp = exp;
	}

	/*********************************************************/
	/* The printing message for an assign statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/***********************************/
		/* RECURSIVELY PRINT VAR + EXP ... */
		/***********************************/
		if (var != null) var.PrintMe();
		if (exp != null) exp.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"STMT\nASSIGN\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (var != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
		if (exp != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp.SerialNumber);
	}

	public TYPE SemantMe() throws SemanticException
	{
		TYPE var_type = var.SemantMe();
		TYPE exp_type = exp.SemantMe();

		/* Make sure that the types of both sides of the assign are equal */

		/* If they're both classes, compare their names */
		if (var_type.typeEnum == TypeEnum.TYPE_CLASS && exp_type.typeEnum == TypeEnum.TYPE_CLASS)
		{
			TYPE_CLASS exp_class = (TYPE_CLASS) exp_type;
			TYPE_CLASS var_class;
			if (var_type instanceof TYPE_VAR)
			{
				var_class = (TYPE_CLASS) ((TYPE_VAR) var_type).var_type;
			}
			else
			{
				var_class = (TYPE_CLASS) var_type;
			}
			if (!var_class.is_replacable(exp_class)) throw new SemanticException(line);
		}
		/* If they're both arrays, compare their names */
		else if (var_type.typeEnum == TypeEnum.TYPE_ARRAY && exp_type.typeEnum == TypeEnum.TYPE_ARRAY)
		{
			TYPE_ARRAY exp_array = (TYPE_ARRAY) exp_type;
			TYPE_ARRAY var_array;
			if (var_type instanceof TYPE_VAR)
			{
				 var_array = (TYPE_ARRAY) ((TYPE_VAR) var_type).var_type;
			}
			else
			{
				var_array = (TYPE_ARRAY) var_type;
			}
			if (!var_array.is_replacable(exp_array)) throw new SemanticException(line);
		}
		/* Else they're not classes or arrays, then make sure that their types are equal */
		else if (var_type.typeEnum == TypeEnum.TYPE_FUNCTION || exp_type.typeEnum == TypeEnum.TYPE_FUNCTION)
		{
			throw new SemanticException(line);
		}
		else if(var_type.typeEnum != exp_type.typeEnum)
		{
			if (exp_type.typeEnum == TypeEnum.TYPE_NIL)
			{
				if (var_type.typeEnum != TypeEnum.TYPE_CLASS && var_type.typeEnum != TypeEnum.TYPE_ARRAY)
				{
					throw new SemanticException(line);
				}
			}
			else
			{
				throw new SemanticException(line);
			}
		}
		return null;
	}

	public TEMP IRme()
	{
		TEMP exp_register;
		boolean is_constant = false;
		if (exp instanceof AST_EXP_INT || exp instanceof AST_EXP_STRING || exp instanceof AST_EXP_NIL)
		{
			is_constant = true;
		}

		if (var instanceof AST_VAR_SUBSCRIPT)
		{
			AST_VAR_SUBSCRIPT subscript_var = (AST_VAR_SUBSCRIPT) var;
			/* Evaluate the left hand of the assignment */
			TEMP array_register = subscript_var.var.IRme();
			TEMP index_register = subscript_var.subscript.IRme();
			/* Evaluate the right hand of the assignment */
			if (is_constant)
			{
				if (exp instanceof AST_EXP_INT)
				{
					int constant_int = ((AST_EXP_INT) exp).value;
					IR.getInstance().Add_IRcommand(new IRcommand_Array_Set(array_register, index_register, constant_int));
				}
				else if (exp instanceof AST_EXP_STRING)
				{
					String constant_str_label = ((AST_EXP_STRING) exp).string_label;
					String constant_str_value = ((AST_EXP_STRING) exp).string;
					IR.getInstance().Add_IRcommand(new IRcommand_Allocate_String(constant_str_label, constant_str_value));
					IR.getInstance().Add_IRcommand(new IRcommand_Array_Set(array_register, index_register, constant_str_label));
				}
				else /* exp instanceof AST_EXP_NIL */
				{
					IR.getInstance().Add_IRcommand(new IRcommand_Array_Set(array_register, index_register, 0));
				}
			}
			else
			{
				exp_register = exp.IRme();
				IR.getInstance().Add_IRcommand(new IRcommand_Array_Set(array_register, index_register, exp_register));
			}
		}
		else if (var instanceof AST_VAR_SIMPLE)
		{
			AST_VAR_SIMPLE simple_var = (AST_VAR_SIMPLE) var;
			/* Evaluate the left hand of the assignment */
			KindEnum var_kind = var.var_kind;
			int var_id = var.var_index;
			String var_label = var.var_label;
			/* Evaluate the right hand of the assignment */
			if (is_constant)
			{
				if (exp instanceof AST_EXP_INT)
				{
					int constant_int = ((AST_EXP_INT) exp).value;

					if (var_kind == KindEnum.GLOBAL)
					{
						IR.getInstance().Add_IRcommand(new IRcommand_Global_Var_Set(var_label, constant_int));
					}

					else if (var_kind == KindEnum.LOCAL)
					{
						IR.getInstance().Add_IRcommand(new IRcommand_Local_Var_Set(var_id, constant_int));
					}

					else if (var_kind == KindEnum.FIELD)
					{
						IR.getInstance().Add_IRcommand(new IRcommand_Field_Set(var_id, constant_int));
					}

					else /* var_kind == KindEnum.PARAM */
					{
						IR.getInstance().Add_IRcommand(new IRcommand_Param_Set(var_id, constant_int));
					}
				}
				else if (exp instanceof AST_EXP_STRING)
				{
					String constant_str_label = ((AST_EXP_STRING) exp).string_label;
					String constant_str_value = ((AST_EXP_STRING) exp).string;
					IR.getInstance().Add_IRcommand(new IRcommand_Allocate_String(constant_str_label, constant_str_value));

					if (var_kind == KindEnum.GLOBAL)
					{
						IR.getInstance().Add_IRcommand(new IRcommand_Global_Var_Set(var_label, constant_str_label));
					}

					else if (var_kind == KindEnum.LOCAL)
					{
						IR.getInstance().Add_IRcommand(new IRcommand_Local_Var_Set(var_id, constant_str_label));
					}

					else if (var_kind == KindEnum.FIELD)
					{
						IR.getInstance().Add_IRcommand(new IRcommand_Field_Set(var_id, constant_str_label));
					}

					else /* var_kind == KindEnum.PARAM */
					{
						IR.getInstance().Add_IRcommand(new IRcommand_Param_Set(var_id, constant_str_label));
					}
				}
				else /* exp instanceof AST_EXP_NIL */
				{
					if (var_kind == KindEnum.GLOBAL)
					{
						IR.getInstance().Add_IRcommand(new IRcommand_Global_Var_Set(var_label, 0));
					}

					else if (var_kind == KindEnum.LOCAL)
					{
						IR.getInstance().Add_IRcommand(new IRcommand_Local_Var_Set(var_id, 0));
					}

					else if (var_kind == KindEnum.FIELD)
					{
						IR.getInstance().Add_IRcommand(new IRcommand_Field_Set(var_id, 0));
					}

					else /* var_kind == KindEnum.PARAM */
					{
						IR.getInstance().Add_IRcommand(new IRcommand_Param_Set(var_id, 0));
					}
				}
			}
			else
			{
				exp_register = exp.IRme();

				if (var_kind == KindEnum.GLOBAL)
				{
					IR.getInstance().Add_IRcommand(new IRcommand_Global_Var_Set(var_label, exp_register));
				}

				else if (var_kind == KindEnum.LOCAL)
				{
					IR.getInstance().Add_IRcommand(new IRcommand_Local_Var_Set(var_id, exp_register));
				}

				else if (var_kind == KindEnum.FIELD)
				{
					IR.getInstance().Add_IRcommand(new IRcommand_Field_Set(var_id, exp_register));
				}

				else /* var_kind == KindEnum.PARAM */
				{
					IR.getInstance().Add_IRcommand(new IRcommand_Param_Set(var_id, exp_register));
				}
			}
		}
		else /* var instanceof AST_VAR_FIELD */
		{
			AST_VAR_FIELD var_field = (AST_VAR_FIELD) var;
			/* Evaluate the left hand of the assignment */
			TEMP class_register = var_field.var.IRme();
			int field_index = var_field.var_index;
			/* Evaluate the right hand of the assignment */
			if (is_constant)
			{
				if (exp instanceof AST_EXP_INT)
				{
					int constant_int = ((AST_EXP_INT) exp).value;
					IR.getInstance().Add_IRcommand(new IRcommand_Field_Set(class_register, field_index, constant_int));
				}
				else if (exp instanceof AST_EXP_STRING)
				{
					String constant_str_label = ((AST_EXP_STRING) exp).string_label;
					String constant_str_value = ((AST_EXP_STRING) exp).string;
					IR.getInstance().Add_IRcommand(new IRcommand_Allocate_String(constant_str_label, constant_str_value));
					IR.getInstance().Add_IRcommand(new IRcommand_Field_Set(class_register, field_index, constant_str_label));
				}
				else /* exp instanceof AST_EXP_NIL */
				{
					IR.getInstance().Add_IRcommand(new IRcommand_Field_Set(class_register, field_index, 0));
				}
			}
			else
			{
				exp_register = exp.IRme();
				IR.getInstance().Add_IRcommand(new IRcommand_Field_Set(class_register, field_index, exp_register));
			}
		}

		return null;
	}
}
