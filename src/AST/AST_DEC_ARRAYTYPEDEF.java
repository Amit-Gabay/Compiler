package AST;

import TYPES.*;
import TEMP.*;

public class AST_DEC_ARRAYTYPEDEF extends AST_DEC
{
	public AST_ARRAYTYPEDEF arrayDec;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_ARRAYTYPEDEF(AST_ARRAYTYPEDEF arrayDec, int line)
	{
		this.line = line;
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.arrayDec = arrayDec;
	}
	
	/****************************************************/
	/* The printing message for a arrayDec dec AST node */
	/****************************************************/
	public void PrintMe()
	{
		/*****************************************/
		/* AST NODE TYPE = AST NODE ARRAYDEC DEC */
		/*****************************************/

		/*********************************/
		/* RECURSIVELY PRINT arrayDec... */
		/*********************************/
		if (arrayDec != null) arrayDec.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"DEC\nARRAY TYPEDEF\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (arrayDec  != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,arrayDec.SerialNumber);
	}

	public TYPE SemantMe() throws SemanticException
	{
		return arrayDec.SemantMe();
	}

	public TEMP IRme()
	{
		return arrayDec.IRme();
	}
}
