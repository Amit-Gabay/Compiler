package AST;

import SYMBOL_TABLE.SYMBOL_TABLE;
import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_LIST <T extends AST_Node> extends AST_Node
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	private String list_type;
	public T data;
	public AST_LIST<T> next;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_LIST(T data, AST_LIST<T> next, int line)
	{
		this.line = line;
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (data instanceof AST_STMT)
		{
			list_type = "STMT";
		}
		else if (data instanceof AST_EXP)
		{
			list_type = "EXP";
		}
		else if (data instanceof AST_DEC)
		{
			list_type = "DEC";
		}
		else if (data instanceof AST_TYPEID)
		{
			list_type = "TYPEID";
		}
		else /* data instanceof AST_CFIELD */
		{
			list_type = "CFIELD";
		}

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.data = data;
		this.next = next;
	}

	/******************************************************/
	/* The printing message for a statement list AST node */
	/******************************************************/
	public void PrintMe()
	{
		/*************************************/
		/* RECURSIVELY PRINT DATA + NEXT ... */
		/*************************************/
		if (data != null) data.PrintMe();
		if (next != null) next.PrintMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("%s\nLIST", list_type));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (data != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, data.SerialNumber);
		if (next != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, next.SerialNumber);
	}

	public TYPE SemantMe() throws SemanticException
	{
		String dataMemberName = null;

		/* Call SemantMe() on the data of the list node */
		TYPE data_type = data.SemantMe();

		/* If data is function / variable declaration --> Add the dataMemberName to the TYPE_LIST */
		if (data instanceof AST_FUNCDEC)
		{
			dataMemberName = ((AST_FUNCDEC) data).ID;
		}
		else if (data instanceof AST_VARDEC)
		{
			dataMemberName = ((AST_VARDEC) data).ID;
		}
		else if (data instanceof AST_CFIELD_FUNCDEC)
		{
			dataMemberName = ((AST_CFIELD_FUNCDEC) data).funcDec.ID;
		}
		else if (data instanceof AST_CFIELD_VARDEC)
		{
			dataMemberName = ((AST_CFIELD_VARDEC) data).varDec.ID;
		}
		else if (data instanceof AST_TYPEID)
		{
			dataMemberName = ((AST_TYPEID) data).ID;
		}

		TYPE_LIST type_node = null;
		if (next == null)
		{
			type_node = new TYPE_LIST(data_type, null, dataMemberName);
		}
		else
		{
			type_node = new TYPE_LIST(data_type, (TYPE_LIST) next.SemantMe(), dataMemberName);
		}
		return type_node;
	}

	public TEMP IRme()
	{
		data.IRme();

		if (next != null)
		{
			next.IRme();
		}

		return null;
	}
}
