package AST;

import TEMP.*;
import TYPES.*;

public class AST_EXP_PAREN extends AST_EXP
{
	public AST_EXP expression;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_PAREN(AST_EXP expression, int line)
	{
		this.line = line;
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.expression = expression;
	}
	
	/*************************************************/
	/* The printing message for a paren exp AST node */
	/*************************************************/
	public void PrintMe()
	{
		/*****************************/
		/* RECURSIVELY PRINT exp ... */
		/*****************************/
		if (expression != null) expression.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"EXP\nPAREN\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (expression != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,expression.SerialNumber);
	}

	public TYPE SemantMe() throws SemanticException
	{
		TYPE exp_type = null;
		if (expression != null)
		{
			exp_type = expression.SemantMe();
		}
		if (exp_type != null)
		{
			return exp_type;
		}
		throw new SemanticException(line);
	}

	public TEMP IRme()
	{
		TEMP exp_register = expression.IRme();

		return exp_register;
	}
}
