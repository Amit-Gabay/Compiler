package AST;

import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_EXP_STRING extends AST_EXP
{
	public String string;
	public int str_len;
	public String string_label;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_STRING(String string, int line)
	{
		this.line = line;
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.string = string;
		this.str_len = string.length();
		this.string_label = IRcommand.getFreshLabel("str");
	}
	
	/**************************************************/
	/* The printing message for a string exp AST node */
	/**************************************************/
	public void PrintMe()
	{
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("EXP\nSTRING(%s)",string));
	}

	public TYPE SemantMe() throws SemanticException
	{
		return TYPE_STRING.getInstance();
	}

	public TEMP IRme()
	{
		TEMP dst_register = TEMP_FACTORY.getInstance().getFreshTEMP();

		IR.getInstance().Add_IRcommand(new IRcommand_Exp_String(dst_register, string, string_label));

		return dst_register;
	}
}
