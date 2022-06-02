package AST;

import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_EXP_NIL extends AST_EXP
{
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_NIL(int line)
	{
		this.line = line;
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
	}
	
	/*******************************************/
	/* The printing message for a NIL AST node */
	/*******************************************/
	public void PrintMe()
	{
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"EXP\nNIL\n");
	}

	public TYPE SemantMe() throws SemanticException
	{
		return TYPE_NIL.getInstance();
	}

	public TEMP IRme()
	{
		TEMP result_register = TEMP_FACTORY.getInstance().getFreshTEMP();

		IR.getInstance().Add_IRcommand(new IRcommand_Exp_Nil(result_register));

		return result_register;
	}
}
