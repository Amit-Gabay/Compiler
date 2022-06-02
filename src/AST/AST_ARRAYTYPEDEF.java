package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;

public class AST_ARRAYTYPEDEF extends AST_DEC
{
	public String ID;
	public AST_TYPE type;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_ARRAYTYPEDEF(String ID, AST_TYPE type, int line)
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
		this.ID = ID;
		this.type = type;
	}
	
	/****************************************************/
	/* The printing message for a ARRAYTYPEDEF AST node */
	/****************************************************/
	public void PrintMe()
	{	
		/************************************/
		/* AST NODE TYPE = AST ARRAYTYPEDEF */
		/************************************/

		/*****************************/
		/* RECURSIVELY PRINT type... */
		/*****************************/
		if (type != null) type.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("ARRAY TYPEDEF(%s)",ID));

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (type != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,type.SerialNumber);
	}

	public TYPE SemantMe() throws SemanticException
	{
		/* Make sure that array declaration is in the global scope */
		String curr_scope = SYMBOL_TABLE.getInstance().findCurrentScope();
		if (!(curr_scope.equals("global")))
		{
			throw new SemanticException(line);
		}

		/* Make sure that the array name is not found in previous declarations in the global scope */
		if (SYMBOL_TABLE.getInstance().findInCurrentScope(ID) != null)
		{
			throw new SemanticException(line);
		}

		/* Call type.SemantMe() */
		TYPE_ARRAY type_semant = new TYPE_ARRAY(ID, type.SemantMe());
		SYMBOL_TABLE.getInstance().enter(ID, type_semant, true, SYMBOL_TABLE.getInstance().current_scope);

		return type_semant;
	}

	public TEMP IRme()
	{
		return null;
	}
}
