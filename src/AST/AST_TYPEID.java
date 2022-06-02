package AST;

import SYMBOL_TABLE.KindEnum;
import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_TYPEID extends AST_Node
{
	public AST_TYPE type;
	public String ID;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_TYPEID(AST_TYPE type, String ID, int line)
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
		this.ID = ID;
	}

	/*********************************************************/
	/* The printing message for an assign statement AST node */
	/*********************************************************/
	public void PrintMe()
	{

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("TYPE ID(%s)", ID));
	}

	public TYPE SemantMe() throws SemanticException
	{
		TYPE type_semant = type.SemantMe();
		TYPE_VAR param_type = new TYPE_VAR(type_semant, ID);

		return param_type;
	}

	public TEMP IRme()
	{
		return null;
	}
}
