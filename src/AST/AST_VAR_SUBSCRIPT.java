package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_VAR_SUBSCRIPT extends AST_VAR
{
	public AST_VAR var;
	public AST_EXP subscript;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_VAR_SUBSCRIPT(AST_VAR var,AST_EXP subscript, int line)
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
		this.subscript = subscript;
	}

	/*****************************************************/
	/* The printing message for a subscript var AST node */
	/*****************************************************/
	public void PrintMe()
	{
		/****************************************/
		/* RECURSIVELY PRINT VAR + SUBSRIPT ... */
		/****************************************/
		if (var != null) var.PrintMe();
		if (subscript != null) subscript.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"VAR\nSUBSCRIPT\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (var       != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
		if (subscript != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,subscript.SerialNumber);
	}

	public TYPE SemantMe() throws SemanticException
	{
		/* Check subscript validity: */
		TYPE subscript_type = subscript.SemantMe();
		/* Make sure that the subscript is INT */
		if (subscript_type.typeEnum != TypeEnum.TYPE_INT)
		{
			throw new SemanticException(line);
		}

		/* If the subscript is CONSTANT, make sure that it's not negative */
		if (subscript instanceof AST_EXP_INT)
		{
			if (((AST_EXP_INT) subscript).value < 0)
			{
				throw new SemanticException(line);
			}
		}

		/* Check var validity: */
		TYPE var_type = var.SemantMe();
		if (var_type.typeEnum != TypeEnum.TYPE_ARRAY)
		{
			throw new SemanticException(line);
		}

		if (var_type instanceof TYPE_ARRAY)
		{
			return ((TYPE_ARRAY) var_type).arrayMembersType;
		}

		return ((TYPE_ARRAY)((TYPE_VAR) var_type).var_type).arrayMembersType;
	}

	public TEMP IRme()
	{
		/* If we got here, it has to be an array access */
		TEMP result_register = TEMP_FACTORY.getInstance().getFreshTEMP();

		TEMP array_register = var.IRme();
		TEMP subscript_register = subscript.IRme();

		IR.getInstance().Add_IRcommand(new IRcommand_Array_Access(result_register, array_register, subscript_register));

		return result_register;
	}
}
