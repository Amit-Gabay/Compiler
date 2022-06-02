package AST;

import TYPES.*;

public abstract class AST_STMT extends AST_Node
{
	/*********************************************************/
	/* The default message for an unknown AST statement node */
	/*********************************************************/
	public void PrintMe()
	{
		System.out.print("UNKNOWN STMT NODE");
	}
	public TYPE SemantMe() throws SemanticException
	{
		return null;
	}
}
