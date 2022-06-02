package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_PROGRAM extends AST_Node
{
	public AST_LIST<AST_DEC> decList;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_PROGRAM(AST_LIST<AST_DEC> decList, int line)
	{
		this.line = line;
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.decList = decList;
	}

	/*********************************************************/
	/* The printing message for an assign statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/*********************************/
		/* RECURSIVELY PRINT DECLIST ... */
		/*********************************/
		if (decList != null) decList.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"PROGRAM\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (decList != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,decList.SerialNumber);
	}

	public TYPE SemantMe() throws SemanticException
	{
		/* Enter the global scope, call SemantMe() on the whole program, and exit the global scope */
		SYMBOL_TABLE.getInstance();
		decList.SemantMe();
		SYMBOL_TABLE.getInstance().endScope();
		return null;
	}

	public TEMP IRme()
	{
		decList.IRme();

		IR.getInstance().Add_IRcommand(new IRcommand_Exit());

		return null;
	}
}
