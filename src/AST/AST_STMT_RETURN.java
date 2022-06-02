package AST;

import SYMBOL_TABLE.*;
import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_STMT_RETURN extends AST_STMT
{
	public AST_EXP exp;

	public String epilogue_label;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_RETURN(AST_EXP exp, int line)
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
	}

	/*********************************************************/
	/* The printing message for an return statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/*****************************/
		/* RECURSIVELY PRINT EXP ... */
		/*****************************/
		if (exp != null) exp.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"STMT\nRETURN\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (exp != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp.SerialNumber);
	}

	public TYPE SemantMe() throws SemanticException
	{
		this.epilogue_label = SYMBOL_TABLE.getInstance().current_function.epilogue_label;
		/* Make sure that the returned value matches the functions return value */
		TYPE_FUNCTION current_function = SYMBOL_TABLE.getInstance().current_function;
		if (exp == null)
		{
			if (current_function.returnType.typeEnum == TypeEnum.TYPE_VOID)
			{
				return null;
			}
			throw new SemanticException(line);
		}
		TYPE exp_type = exp.SemantMe();
		if (exp_type.typeEnum != current_function.returnType.typeEnum)
		{
			if (!(exp_type.typeEnum == TypeEnum.TYPE_NIL && (current_function.returnType.typeEnum == TypeEnum.TYPE_CLASS || current_function.returnType.typeEnum == TypeEnum.TYPE_ARRAY)))
			{
				throw new SemanticException(line);
			}
		}

		else if (exp_type.typeEnum == TypeEnum.TYPE_CLASS)
		{
			TYPE_CLASS exp_class = (TYPE_CLASS) exp_type;
			TYPE_CLASS return_class = (TYPE_CLASS) current_function.returnType;
			if (!return_class.is_replacable(exp_class)) throw new SemanticException(line);
		}

		else if (exp_type.typeEnum == TypeEnum.TYPE_ARRAY)
		{
			TYPE_ARRAY exp_array = (TYPE_ARRAY) exp_type;
			TYPE_ARRAY return_array = (TYPE_ARRAY) current_function.returnType;
			if (!return_array.is_replacable(exp_array)) throw new SemanticException(line);
		}

		return null;
	}

	public TEMP IRme()
	{
		TEMP exp_register = null;
		if (exp != null) {
			exp_register = exp.IRme();
		}

		IR.getInstance().Add_IRcommand(new IRcommand_Return(exp_register, this.epilogue_label));

		return exp_register;
	}
}
