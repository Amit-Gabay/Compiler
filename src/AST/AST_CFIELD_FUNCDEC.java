package AST;

import TYPES.*;
import TEMP.*;

public class AST_CFIELD_FUNCDEC extends AST_CFIELD
{
	public AST_FUNCDEC funcDec;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_CFIELD_FUNCDEC(AST_FUNCDEC funcDec, int line)
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
		this.funcDec = funcDec;
	}

	/*******************************************************/
	/* The printing message for an cField funcdec AST node */
	/*******************************************************/
	public void PrintMe()
	{
		/******************************************/
		/* AST NODE TYPE = AST NEW CFIELD FUNCDEC */
		/******************************************/

		/*********************************/
		/* RECURSIVELY PRINT funcDec ... */
		/*********************************/
		if (funcDec != null) funcDec.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"CFIELD\nFUNCDEC\n");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (funcDec  != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,funcDec.SerialNumber);
	}

	public TYPE SemantMe() throws SemanticException
	{
		return funcDec.SemantMe();
	}

	public TEMP IRme()
	{
		return funcDec.IRme();
	}
}
