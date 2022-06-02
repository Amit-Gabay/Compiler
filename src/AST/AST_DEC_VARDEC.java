package AST;

import TYPES.*;
import TEMP.*;

public class AST_DEC_VARDEC extends AST_DEC
{
	public AST_VARDEC varDec;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_VARDEC(AST_VARDEC varDec, int line)
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
		this.varDec = varDec;
	}
	
	/**************************************************/
	/* The printing message for a varDec dec AST node */
	/**************************************************/
	public void PrintMe()
	{
		/***************************************/
		/* AST NODE TYPE = AST NODE VARDEC DEC */
		/***************************************/

		/*******************************/
		/* RECURSIVELY PRINT varDec... */
		/*******************************/
		if (varDec != null) varDec.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"DEC\nVARDEC\n");
		
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
