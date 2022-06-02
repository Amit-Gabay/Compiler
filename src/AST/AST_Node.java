package AST;

import TYPES.*;
import TEMP.*;

public abstract class AST_Node
{
	/*******************************************/
	/* The serial number is for debug purposes */
	/* In particular, it can help in creating  */
	/* a graphviz dot format of the AST ...    */
	/*******************************************/
	public int SerialNumber;
	public int line; /* Code line number - for error handling */
	/***********************************************/
	/* The default message for an unknown AST node */
	/***********************************************/
	public void PrintMe()
	{
		System.out.print("AST NODE UNKNOWN\n");
	}
	public TYPE SemantMe() throws SemanticException
	{
		return null;
	}
	public TEMP IRme()
	{
		return null;
	}
}
