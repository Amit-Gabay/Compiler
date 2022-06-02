package AST;

import SYMBOL_TABLE.KindEnum;
import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_STMT_ASSIGN_NEW extends AST_STMT
{
	public AST_VAR var;
	public AST_NEWEXP newExpression;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_ASSIGN_NEW(AST_VAR var, AST_NEWEXP newExpression, int line)
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
		this.newExpression = newExpression;
	}

	/*****************************************************************/
	/* The printing message for an new expression statement AST node */
	/*****************************************************************/
	public void PrintMe()
	{
		/********************************/
		/* RECURSIVELY PRINT NEWEXP ... */
		/********************************/
		if (var != null) var.PrintMe();
		if (newExpression != null) newExpression.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"STMT\nASSIGN NEW\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (var != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
		if (newExpression != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,newExpression.SerialNumber);
	}

	public TYPE SemantMe() throws SemanticException
	{
		TYPE var_type = var.SemantMe();
		TYPE newexp_type = newExpression.SemantMe();

		/* Make sure that the types of both sides of the assign are equal */

		/* If they're both classes, compare them */
		if (var_type.typeEnum == TypeEnum.TYPE_CLASS && newexp_type.typeEnum == TypeEnum.TYPE_CLASS)
		{
			TYPE_CLASS newexp_class = (TYPE_CLASS) newexp_type;
			TYPE_CLASS var_class;
			if (var_type instanceof TYPE_VAR) {
				var_class = (TYPE_CLASS) ((TYPE_VAR) var_type).var_type;
			}
			else {
				var_class = (TYPE_CLASS) var_type;
			}
			if (!var_class.is_replacable(newexp_class)) throw new SemanticException(line);
		}
		/* If they're both arrays, compare them */
		else if (var_type.typeEnum == TypeEnum.TYPE_ARRAY && newexp_type.typeEnum == TypeEnum.TYPE_ARRAY)
		{
			TYPE_ARRAY var_array;
			if (var_type instanceof TYPE_VAR){
				var_array = (TYPE_ARRAY) ((TYPE_VAR) var_type).var_type;
			}
			else {
				var_array = (TYPE_ARRAY) var_type;
			}
			TYPE_ARRAY newexp_array = (TYPE_ARRAY) newexp_type;
			/* If their arrayMembersType NOT equal, return error */
			if (!var_array.is_replacable_new(newexp_array)) throw new SemanticException(line);
		}
		/* Else, it's an error */
		else
		{
			throw new SemanticException(line);
		}
		return null;
	}

	public TEMP IRme()
	{
		TEMP allocation_register;

		if (var instanceof AST_VAR_SUBSCRIPT)
		{
			AST_VAR_SUBSCRIPT subscript_var = (AST_VAR_SUBSCRIPT) var;
			/* Evaluate the left hand of the assignment */
			TEMP array_register = subscript_var.var.IRme();
			TEMP index_register = subscript_var.subscript.IRme();
			/* Evaluate the right hand of the assignment */
			allocation_register = newExpression.IRme();
			IR.getInstance().Add_IRcommand(new IRcommand_Array_Set(array_register, index_register, allocation_register));
		}
		else if (var instanceof AST_VAR_SIMPLE)
		{
			/* Evaluate the left hand of the assignment */
			KindEnum var_kind = var.var_kind;
			int var_id = var.var_index;
			String var_label = var.var_label;
			/* Evaluate the right hand of the assignment */
			allocation_register = newExpression.IRme();

			if (var_kind == KindEnum.GLOBAL)
			{
				IR.getInstance().Add_IRcommand(new IRcommand_Global_Var_Set(var_label, allocation_register));
			}

			else if (var_kind == KindEnum.LOCAL)
			{
				IR.getInstance().Add_IRcommand(new IRcommand_Local_Var_Set(var_id, allocation_register));
			}

			else /* var_kind == KindEnum.PARAM */
			{
				IR.getInstance().Add_IRcommand(new IRcommand_Param_Set(var_id, allocation_register));
			}

		}
		else /* var instanceof AST_VAR_FIELD */
		{
			AST_VAR_FIELD var_field = (AST_VAR_FIELD) var;
			/* Evaluate the left hand of the assignment */
			TEMP class_register = var_field.var.IRme();
			int field_index = var_field.var_index;
			/* Evaluate the right hand of the assignment */
			allocation_register = newExpression.IRme();
			IR.getInstance().Add_IRcommand(new IRcommand_Field_Set(class_register, field_index, allocation_register));
		}

		return null;
	}
}
