package AST;

import IR.*;
import TYPES.*;
import TEMP.*;

public class AST_EXP_INT extends AST_EXP
{
	public int value;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_INT(int value, int line)
	{
		this.line = line;
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.value = value;
	}

	/************************************************/
	/* The printing message for an INT EXP AST node */
	/************************************************/
	public void PrintMe()
	{

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("EXP\nINT(%d)",value));
	}

	public TYPE SemantMe() throws SemanticException
	{
		return TYPE_INT.getInstance();
	}

	public TEMP IRme()
	{
		TEMP result_register = TEMP_FACTORY.getInstance().getFreshTEMP();

		IR.getInstance().Add_IRcommand(new IRcommand_Exp_Int(result_register, this.value));

		return result_register;
	}
}
