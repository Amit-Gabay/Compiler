package AST;

import SYMBOL_TABLE.SYMBOL_TABLE;
import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_STMT_VARDEC extends AST_STMT
{
	public AST_VARDEC varDec;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_STMT_VARDEC(AST_VARDEC varDec, int line)
	{
		this.line = line;
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.varDec = varDec;
	}
	
	/***************************************************/
	/* The printing message for a varDec stmt AST node */
	/***************************************************/
	public void PrintMe()
	{
		/*******************************/
		/* RECURSIVELY PRINT varDec... */
		/*******************************/
		if (varDec != null) varDec.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"STMT\nVARDEC\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (varDec  != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,varDec.SerialNumber);
	}

	public TYPE SemantMe() throws SemanticException
	{
		return varDec.SemantMe();
	}

	public TEMP IRme()
	{
		return varDec.IRme();
	}
}
