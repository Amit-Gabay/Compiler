package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_EXP_FUNC extends AST_EXP
{
	public AST_VAR var;
	public String ID;
	public AST_LIST<AST_EXP> expList;

	public int method_id;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_FUNC(AST_VAR var, String ID, AST_LIST<AST_EXP> expList, int line)
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
		this.var = var;
		this.ID = ID;
		this.expList = expList;
	}
	
	/********************************************/
	/* The printing message for a FUNC AST node */
	/********************************************/
	public void PrintMe()
	{
		/********************************/
		/* AST NODE TYPE = AST FUNC EXP */
		/********************************/

		/***************************************/
		/* RECURSIVELY PRINT var + expList ... */
		/***************************************/
		if (var != null) var.PrintMe();
		if (expList != null) expList.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("EXP\nFUNC(%s)",ID));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (var     != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
		if (expList != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,expList.SerialNumber);
	}

	public TYPE SemantMe() throws SemanticException
	{
		TYPE_LIST found_arg_list;
		TYPE_FUNCTION found_method = null;
		TYPE found_function = null;
		boolean is_found = false;

		/* If it's a method */
		if (var != null)
		{
			if (expList == null)
			{
				if (TYPE_FUNCTION.is_recursive_call(ID, null) == true)
				{
					return SYMBOL_TABLE.getInstance().current_function.returnType;
				}
			}
			else
			{
				if (TYPE_FUNCTION.is_recursive_call(ID, (TYPE_LIST) expList.SemantMe()) == true)
				{
					return SYMBOL_TABLE.getInstance().current_function.returnType;
				}
			}
			TYPE var_type = var.SemantMe();

			/* Make sure that var is a class */
			if (var_type.typeEnum != TypeEnum.TYPE_CLASS)
			{
				throw new SemanticException(line);
			}

			/* Find the method of the given class */
			TYPE_CLASS curr_class;
			if (var_type instanceof TYPE_VAR)
			{
				curr_class = (TYPE_CLASS) ((TYPE_VAR) var_type).var_type;
			}
			else
			{
				curr_class = (TYPE_CLASS) var_type;
			}

			while(curr_class != null && !is_found)
			{
				TYPE_LIST curr_data_member = curr_class.data_members;
				while(curr_data_member != null)
				{
					if (curr_data_member.dataMemberName.equals(ID))
					{
						if (curr_data_member.head.typeEnum == TypeEnum.TYPE_FUNCTION)
						{
							found_method = (TYPE_FUNCTION) curr_data_member.head;
							is_found = true;
							break;
						}
						throw new SemanticException(line);
					}

					curr_data_member = curr_data_member.tail;
				}

				curr_class = curr_class.father;
			}

			if (!is_found)
			{
				throw new SemanticException(line);
			}

			found_arg_list = found_method.params;
		}

		/* Else, it's a function */
		else
		{
			if (expList == null)
			{
				if (TYPE_FUNCTION.is_recursive_call(ID, null) == true)
				{
					return SYMBOL_TABLE.getInstance().current_function.returnType;
				}
			}
			else
			{
				if (TYPE_FUNCTION.is_recursive_call(ID, (TYPE_LIST) expList.SemantMe()) == true)
				{
					return SYMBOL_TABLE.getInstance().current_function.returnType;
				}
			}
			found_function = SYMBOL_TABLE.getInstance().find(ID);
			if (found_function == null)
			{
				throw new SemanticException(line);
			}

			/* Make sure that the found ID in the symbols table is a function */
			if (found_function.typeEnum != TypeEnum.TYPE_FUNCTION)
			{
				throw new SemanticException(line);
			}

			found_arg_list = ((TYPE_FUNCTION) found_function).params;
		}

		/* Compare the given function arguments with the found function arguments */
		TYPE_LIST given_arg_list;
		if (expList != null)
		{
			given_arg_list = (TYPE_LIST) expList.SemantMe();
		}
		else
		{
			given_arg_list = null;
		}
		while(given_arg_list != null && found_arg_list != null)
		{
			if (given_arg_list.head.typeEnum != found_arg_list.head.typeEnum)
			{
				if (!(given_arg_list.head.typeEnum == TypeEnum.TYPE_NIL && (found_arg_list.head.typeEnum == TypeEnum.TYPE_CLASS || found_arg_list.head.typeEnum == TypeEnum.TYPE_ARRAY)))
				{
					throw new SemanticException(line);
				}
			}

			else if (given_arg_list.head.typeEnum == TypeEnum.TYPE_ARRAY)
			{
				TYPE_ARRAY given_array = (TYPE_ARRAY) given_arg_list.head;
				TYPE_ARRAY found_array = (TYPE_ARRAY) ((TYPE_VAR) found_arg_list.head).var_type;
				// left (this) := right (given)
				if(!found_array.is_replacable(given_array)) throw new SemanticException(line);
			}

			else if (given_arg_list.head.typeEnum == TypeEnum.TYPE_CLASS)
			{
				TYPE_CLASS given_class = (TYPE_CLASS) given_arg_list.head;
				TYPE_CLASS found_class = (TYPE_CLASS) ((TYPE_VAR) found_arg_list.head).var_type;
				if (!found_class.is_replacable(given_class)) throw new SemanticException(line);
			}

			given_arg_list = given_arg_list.tail;
			found_arg_list = found_arg_list.tail;
		}

		if (given_arg_list != null || found_arg_list != null)
		{
			throw new SemanticException(line);
		}

		/* If it's a method */
		if (var != null)
		{
			this.method_id = found_method.func_index;
			return found_method.returnType;
		}
		else
		{
			return ((TYPE_FUNCTION) found_function).returnType;
		}
	}

	public TEMP IRme()
	{
		TEMP result = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP_LIST params_tmplist;
		TEMP_LIST tmplist_head = null;
		TEMP class_temp = null;

		/* If it's a method */
		if (var != null)
		{
			class_temp = var.IRme();
		}

		AST_LIST<AST_EXP> params = expList;
		if (params != null)
		{
			TEMP first_param_temp = params.data.IRme();
			params_tmplist = new TEMP_LIST(first_param_temp);
			tmplist_head = params_tmplist;
			params = params.next;

			while (params != null)
			{
				TEMP param_temp = params.data.IRme();
				params_tmplist.next = new TEMP_LIST(param_temp);
				tmplist_head.length++;

				params_tmplist = params_tmplist.next;
				params = params.next;
			}
		}

		String func_label = "_"+ID;

		/* If it's a method */
		if (var != null)
		{
			IR.getInstance().Add_IRcommand(new IRcommand_Method_Call(result, class_temp, method_id, tmplist_head));
		}
		else
		{
			if (ID.equals("PrintInt"))
			{
				IR.getInstance().Add_IRcommand(new IRcommand_PrintInt(tmplist_head.data));
			}
			else if (ID.equals("PrintString"))
			{
				IR.getInstance().Add_IRcommand(new IRcommand_PrintString(tmplist_head.data));
			}
			else
			{
				IR.getInstance().Add_IRcommand(new IRcommand_Func_Call(result, func_label, tmplist_head));
			}
		}

		return result;
	}
}
