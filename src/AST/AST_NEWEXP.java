package AST;

import SYMBOL_TABLE.KindEnum;
import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_NEWEXP extends AST_Node
{
	public AST_TYPE type;
	public AST_EXP exp;

	public int fields_num;

	public String class_name;

	public TYPE_LIST class_members;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_NEWEXP(AST_TYPE type, AST_EXP exp, int line)
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
		this.exp = exp;
	}

	/*********************************************************/
	/* The printing message for an assign statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/************************************/
		/* RECURSIVELY PRINT TYPE + EXP ... */
		/************************************/
		if (type != null) type.PrintMe();
		if (exp != null) exp.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"NEWEXP\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (type != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,type.SerialNumber);
		if (exp  != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp.SerialNumber);
	}

	public TYPE SemantMe() throws SemanticException
	{
		TYPE type_semant = type.SemantMe();

		/* If the new expression is an array allocation */
		if (exp != null)
		{
			/* If the expression is a constant, make sure that it's greater than zero */
			if(exp instanceof AST_EXP_INT)
			{
				if (((AST_EXP_INT) exp).value <= 0)
				{
					throw new SemanticException(line);
				}
			}
			
			exp.SemantMe();

			return new TYPE_ARRAY(null, type_semant);
		}

		/* If the new expression is a class allocation */
		else
		{
			/* Make sure that we're creating a new instance of type class */
			if (type_semant.typeEnum != TypeEnum.TYPE_CLASS)
			{
				throw new SemanticException(line);
			}

			/* Annotations for the MIPSme */
			TYPE_CLASS class_type = (TYPE_CLASS) type_semant;
			this.fields_num = class_type.fields_counter - 1;

			this.class_members = class_type.data_members;
			this.class_name = class_type.name;

			return type_semant;
		}
	}

	public TEMP IRme()
	{
		TEMP dst_register = TEMP_FACTORY.getInstance().getFreshTEMP();

		/* If it's a class */
		if (exp == null)
		{
			/* Class object CANNOT be initialized in the global scope */
			IR.getInstance().Add_IRcommand(new IRcommand_Heap_Allocate(dst_register, this.fields_num, class_name));
			IR.getInstance().Add_IRcommand(new IRcommand_Fill_Class_Object(dst_register, this.class_name, this.class_members));
		}

		/* Else, it's an array */
		else
		{
			TEMP size_register = exp.IRme();

			/* Arrays CANNOT be initialized in the global scope */
			IR.getInstance().Add_IRcommand(new IRcommand_Heap_Allocate(dst_register, size_register));
		}

		return dst_register;
	}
}
