package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_VAR_FIELD extends AST_VAR
{
	public AST_VAR var;
	public String fieldName;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_VAR_FIELD(AST_VAR var,String fieldName, int line)
	{
		this.line = line;
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.var = var;
		this.fieldName = fieldName;
	}

	/*************************************************/
	/* The printing message for a field var AST node */
	/*************************************************/
	public void PrintMe()
	{
		/**********************************************/
		/* RECURSIVELY PRINT VAR, then FIELD NAME ... */
		/**********************************************/
		if (var != null) var.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("VAR\nFIELD(%s)",fieldName));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (var != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
	}

	public TYPE SemantMe() throws SemanticException
	{
		if (var == null)
		{
			throw new SemanticException(line);
		}
		TYPE var_semant = var.SemantMe();
		TYPE_CLASS class_var;
		if (var_semant instanceof TYPE_VAR){
			class_var = (TYPE_CLASS) ((TYPE_VAR) var_semant).var_type;
		}
		else {
			class_var = (TYPE_CLASS) var_semant;
		}

		/* Make sure that var is a class */
		if (class_var.typeEnum != TypeEnum.TYPE_CLASS)
		{
			throw new SemanticException(line);
		}

		TYPE_CLASS curr_class = class_var;

		while(curr_class != null)
		{
			TYPE_LIST curr_data_member = curr_class.data_members;
			while(curr_data_member != null)
			{
				if ((curr_data_member.dataMemberName.equals(fieldName)))
				{
					if (curr_data_member.head.typeEnum != TypeEnum.TYPE_FUNCTION)
					{
						TYPE_VAR field_var = (TYPE_VAR) curr_data_member.head;
						/* Add the AST annotations */
						this.var_kind = KindEnum.FIELD;
						this.var_index = field_var.var_index;
						this.var_label = null;

						return field_var;
					}
					throw new SemanticException(line);
				}

				curr_data_member = curr_data_member.tail;
			}

			curr_class = curr_class.father;
		}
		throw new SemanticException(line);
	}

	public TEMP IRme()
	{
		/* If we got here, it has to be a class field access */
		TEMP result_register = TEMP_FACTORY.getInstance().getFreshTEMP();

		TEMP class_register = var.IRme();
		int field_index = this.var_index;

		IR.getInstance().Add_IRcommand(new IRcommand_Field_Access(result_register, class_register, field_index));

		return result_register;
	}
}
