package AST;

import IR.*;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;

public class AST_CLASSDEC extends AST_Node
{
	public String firstID;
	public String secondID;
	public AST_LIST<AST_CFIELD> cFieldList;

	public int methods_num;
	public String method_labels[];

	/* Fields default values array */
	public Object field_values[];
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_CLASSDEC(String firstID, String secondID, AST_LIST<AST_CFIELD> cFieldList, int line)
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
		this.firstID = firstID;
		this.secondID = secondID;
		this.cFieldList = cFieldList;
	}
	
	/****************************************************/
	/* The printing message for a DEC CLASSDEC AST node */
	/****************************************************/
	public void PrintMe()
	{	
		/************************************/
		/* AST NODE TYPE = AST DEC CLASSDEC */
		/************************************/

		/***********************************/
		/* RECURSIVELY PRINT cFieldList... */
		/***********************************/
		if (cFieldList != null) cFieldList.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("CLASSDEC(%s,%s)",firstID,secondID));

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (cFieldList != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,cFieldList.SerialNumber);
	}

	public TYPE SemantMe() throws SemanticException
	{
		/* Make sure that class declaration is in the global scope */
		String curr_scope = SYMBOL_TABLE.getInstance().findCurrentScope();
		if (!(curr_scope.equals("global")))
		{
			throw new SemanticException(line);
		}

		/* Make sure that the class name is NOT found in previous declarations in the global scope */
		if (SYMBOL_TABLE.getInstance().find(firstID) != null)
		{
			throw new SemanticException(line);
		}

		/*************************/
		/* [1] Begin Class Scope */
		/*************************/
		SYMBOL_TABLE.getInstance().beginScope("class");
		TYPE_CLASS class_type = new TYPE_CLASS(null, firstID, null);
		SYMBOL_TABLE.getInstance().current_class = class_type;

		/***************************/
		/* [2] Semant Data Members */
		/***************************/

		TYPE_CLASS father_type = null;
		/* If there is NO parent class */
		if (secondID == null)
		{
			class_type.methods_counter = 1;
			class_type.data_members = (TYPE_LIST) cFieldList.SemantMe();
		}
		/* Else, there is a parent class */
		else
		{
			/* Make sure that the given parent class is a valid class */
			boolean is_class = SYMBOL_TABLE.getInstance().isTypeClass(secondID);
			if (!is_class)
			{
				throw new SemanticException(line);
			}

			father_type = (TYPE_CLASS) SYMBOL_TABLE.getInstance().find(secondID);
			SYMBOL_TABLE.getInstance().current_parent_class = father_type;
			class_type.father = father_type;

			class_type.methods_counter = class_type.father.methods_counter;
			class_type.fields_counter = class_type.father.fields_counter;

			TYPE_LIST curr_class_data_members = (TYPE_LIST) cFieldList.SemantMe();
			class_type.data_members = father_type.data_members.concat(curr_class_data_members);
		}

		/* Determine the method labels for the Virtual Table */
		this.methods_num = class_type.methods_counter - 1;
		this.method_labels = new String[this.methods_num];

		TYPE_LIST curr_member = class_type.data_members;
		while (curr_member != null)
		{
			/* If the data member is a function - insert its label to the array */
			if (curr_member.head.typeEnum == TypeEnum.TYPE_FUNCTION)
			{
				int method_index = ((TYPE_FUNCTION) curr_member.head).func_index - 1;
				method_labels[method_index] = "_"+curr_member.dataMemberName+"_"+((TYPE_FUNCTION) (curr_member.head)).class_name;
			}

			curr_member = curr_member.tail;
		}

		/*****************/
		/* [3] End Scope */
		/*****************/
		SYMBOL_TABLE.getInstance().endScope();
		SYMBOL_TABLE.getInstance().current_class = null;
		SYMBOL_TABLE.getInstance().current_parent_class = null;


		/************************************************/
		/* [4] Enter the Class Type to the Symbol Table */
		/************************************************/
		SYMBOL_TABLE.getInstance().enter(firstID, class_type, true, SYMBOL_TABLE.getInstance().current_scope);

		/*******************************************************/
		/* [5] Return value is relevant for class declarations */
		/*******************************************************/
		return class_type;
	}

	public TEMP IRme()
	{
		/* Allocate the virtual table */
		IR.getInstance().Add_IRcommand(new IRcommand_Allocate_VT(firstID, method_labels));

		cFieldList.IRme();

		return null;
	}
}
