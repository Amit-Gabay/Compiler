package AST;

import TYPES.*;
import TEMP.*;

public class AST_CFIELD_VARDEC extends AST_CFIELD
{
	public AST_VARDEC varDec;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_CFIELD_VARDEC(AST_VARDEC varDec, int line)
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

	/******************************************************/
	/* The printing message for an cField vardec AST node */
	/******************************************************/
	public void PrintMe()
	{
		/*****************************************/
		/* AST NODE TYPE = AST NEW CFIELD VARDEC */
		/*****************************************/

		/********************************/
		/* RECURSIVELY PRINT varDec ... */
		/********************************/
		if (varDec != null) varDec.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"CFIELD\nVARDEC\n");

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
