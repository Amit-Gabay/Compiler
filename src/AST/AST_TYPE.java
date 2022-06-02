package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import IR.*;
import TEMP.*;

public class AST_TYPE extends AST_Node
{
	/*
	if type==0: TYPE_INT
	if type==1: TYPE_STRING
	if type==2: TYPE_VOID
	*/
	public int type;
	public String typeID;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_TYPE(int type, int line)
	{
		this.line = line;
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.type = type;
	}
	
	public AST_TYPE(String typeID, int line)
	{
		this.line = line;
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.typeID = typeID;
	}

	/*********************************************************/
	/* The printing message for an assign statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		String typeString;

		/******************************************/
		/* CONVERT type to a printable typeString */
		/******************************************/
		if (typeID != null)
		{
			typeString = typeID;
		}
		else if (type == 0)
		{
			typeString = "TYPE_INT";
		}
		else if (type == 1)
		{
			typeString = "TYPE_STRING";
		}
		else
		{
			typeString = "TYPE_VOID";
		}

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("TYPE(%s)", typeString));
	}

	public TYPE SemantMe() throws SemanticException
	{
		/* If it's a complex type: */
		if (typeID != null)
		{
			/* If we are in a class scope */
			if (SYMBOL_TABLE.getInstance().current_class != null)
			{
				/* If the type name is same as the current class name */
				if(SYMBOL_TABLE.getInstance().current_class.name.equals(typeID))
				{
					return SYMBOL_TABLE.getInstance().current_class;
				}
			}		

			/* Make sure that the complex type is an instance-able type */
			if (!SYMBOL_TABLE.getInstance().isInstancable(typeID))
			{
				throw new SemanticException(line);
			}

			/* Find the type found from the declaration */
			TYPE typeFromTable = SYMBOL_TABLE.getInstance().find(typeID);
			if (typeFromTable != null)
			{
				return typeFromTable;
			}
			/* type declaration was NOT found: */
			throw new SemanticException(line);
		}
		else if (type == 0)
		{
			return  TYPE_INT.getInstance();
		}
		else if (type == 1)
		{
			return  TYPE_STRING.getInstance();
		}
		else
		{
			return  TYPE_VOID.getInstance();
		}
	}

	public String getName() 
	{
		if (typeID != null)
		{
			return typeID;
		}
		else if (type == 0) 
		{
			return "int";
		}
		else if (type == 1) 
		{
			return "string";
		}
		else
		{
			return "void";
		}
	}

	public TEMP IRme()
	{
		return null;
	}
}
