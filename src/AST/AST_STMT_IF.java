package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_STMT_IF extends AST_STMT
{
	public AST_EXP exp;
	public AST_LIST<AST_STMT> lst;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_IF(AST_EXP exp, AST_LIST<AST_STMT> lst, int line)
	{
		this.line = line;
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.exp = exp;
		this.lst = lst;
	}

	/***********************************************************/
	/* The printing message for an function statement AST node */
	/***********************************************************/
	public void PrintMe()
	{
		/***************************************/
		/* RECURSIVELY PRINT VAR + EXPLIST ... */
		/***************************************/
		if (exp != null) exp.PrintMe();
		if (lst != null) lst.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"STMT\nIF\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (exp != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp.SerialNumber);
		if (lst != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,lst.SerialNumber);
	}

	public TYPE SemantMe() throws SemanticException
	{
		/* Make sure that the if's condition is an int */
		if (exp.SemantMe().typeEnum != TypeEnum.TYPE_INT)
		{
			throw new SemanticException(line);
		}

		/*************************/
		/* [1] Begin Class Scope */
		/*************************/
		SYMBOL_TABLE.getInstance().beginScope("if");

		/***************************/
		/* [2] Semant Data Members */
		/***************************/
		lst.SemantMe();

		/*****************/
		/* [3] End Scope */
		/*****************/
		SYMBOL_TABLE.getInstance().endScope();

		/*********************************************************/
		/* [4] Return value is irrelevant for class declarations */
		/*********************************************************/
		return null;
	}

	public TEMP IRme()
	{
		/*
		 * 0) Call recursively IRme() on exp       (V)
		 * 1) BEQ exp, 0 to _if_end_num            (V)
		 * 2) Call recursively IRme() on stmtList  (V)
		 * 3) _if_end_num label                    (V)
		 */

		String end_label = IRcommand.getFreshLabel("if_end");

		TEMP condition_register = exp.IRme();

		IR.getInstance().Add_IRcommand(new IRcommand_Jump_If_Eq_To_Zero(condition_register, end_label));
		lst.IRme();
		IR.getInstance().Add_IRcommand(new IRcommand_Label(end_label , labelEnum.OTHER));

		return null;
	}
}
