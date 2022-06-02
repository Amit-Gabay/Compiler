package AST;

import TYPES.*;
import TEMP.*;

public class AST_DEC_CLASSDEC extends AST_DEC
{
	public AST_CLASSDEC classDec;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_CLASSDEC(AST_CLASSDEC classDec, int line)
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
		this.classDec = classDec;
	}
	
	/****************************************************/
	/* The printing message for a classDec dec AST node */
	/****************************************************/
	public void PrintMe()
	{
		/*****************************************/
		/* AST NODE TYPE = AST NODE CLASSDEC DEC */
		/*****************************************/

		/*********************************/
		/* RECURSIVELY PRINT classDec... */
		/*********************************/
		if (classDec != null) classDec.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"DEC\nCLASSDEC\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (classDec  != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,classDec.SerialNumber);
	}

	public TYPE SemantMe() throws SemanticException
	{
		return classDec.SemantMe();
	}

	public TEMP IRme()
	{
		return classDec.IRme();
	}
}
