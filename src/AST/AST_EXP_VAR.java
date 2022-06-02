package AST;

import TYPES.*;
import TEMP.*;

public class AST_EXP_VAR extends AST_EXP
{
	public AST_VAR var;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_VAR(AST_VAR var, int line)
	{
		this.line = line;
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.var = var;
	}
	
	/***********************************************/
	/* The default message for an exp var AST node */
	/***********************************************/
	public void PrintMe()
	{
		/*****************************/
		/* RECURSIVELY PRINT var ... */
		/*****************************/
		if (var != null) var.PrintMe();
		
		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"EXP\nVAR\n");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if(var != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
			
	}

	public TYPE SemantMe() throws SemanticException
	{
		TYPE var_type = null;
		if (var != null)
		{
			var_type = var.SemantMe();
		}
		if (var_type != null)
		{
			if (var_type instanceof TYPE_VAR)
			{
				return ((TYPE_VAR) var_type).var_type;
			}
			return var_type;
		}
		throw new SemanticException(line);
	}

	public TEMP IRme()
	{
		return var.IRme();
	}
}
