package AST;

import SYMBOL_TABLE.*;
import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_FUNCDEC extends AST_Node
{
	public AST_TYPE type;
	public String ID;
	public AST_LIST<AST_TYPEID> typeIDList;
	public AST_LIST<AST_STMT> stmtList;

	public String class_name;
	public String epilogue_label;

	/* Parameters number */
	public int parameters_num;
	public int local_var_num;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_FUNCDEC(AST_TYPE type, String ID, AST_LIST<AST_TYPEID> typeIDList, AST_LIST<AST_STMT> stmtList, int line)
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
		this.typeIDList = typeIDList;
		this.stmtList = stmtList;

//		if (this.class_name != null)
//		{
//			this.epilogue_label = "_"+ID+"_"+this.class_name+"_epilogue";
//		}
//		else
//		{
//			if (ID.equals("main"))
//			{
//				this.epilogue_label = "main_epilogue";
//			}
//			else
//			{
//				this.epilogue_label = "_"+ID+"_epilogue";
//			}
//		}
	}
	
	/***********************************************/
	/* The printing message for a FUNCDEC AST node */
	/***********************************************/
	public void PrintMe()
	{
		/*****************************************************/
		/* RECURSIVELY PRINT type + typeIDList + stmtList... */
		/*****************************************************/
		if (type != null) type.PrintMe();
		if (typeIDList != null) typeIDList.PrintMe();
		if (stmtList != null) stmtList.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("FUNCDEC(%s)",ID));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (type  != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,type.SerialNumber);
		if (typeIDList != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,typeIDList.SerialNumber);
		if (stmtList != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,stmtList.SerialNumber);
	}

	public TYPE SemantMe() throws SemanticException
	{
		boolean is_method = false;
		boolean is_found = false;
		int method_id = 0;

		/* Make sure that function declaration is in the global scope or in a class scope */
		String curr_scope = SYMBOL_TABLE.getInstance().findCurrentScope();
		if (!(curr_scope.equals("global")) && !(curr_scope.equals("class")))
		{
			throw new SemanticException(line);
		}

		/* Make sure that the return type is valid */
		if (type.typeID != null)
		{
			if (!SYMBOL_TABLE.getInstance().isInstancable(type.typeID))
			{
				throw new SemanticException(line);
			}
		}

		/* Check if the function is a method */
		if (curr_scope.equals("class"))
		{
			is_method = true;
		}

		/* If it's a function (In global scope) */
		if (!is_method)
		{
			/* Check if the function is declared in the current scope */
			TYPE dec_type = SYMBOL_TABLE.getInstance().find(ID);
			if (dec_type != null)
			{
				throw new SemanticException(line);
			}

			/* If there are arguments, make sure that their types are valid */
			if (typeIDList != null)
			{
				TYPE_LIST function_params = (TYPE_LIST) typeIDList.SemantMe();
				while (function_params != null)
				{
					if (function_params.head.typeEnum == TypeEnum.TYPE_VOID)
					{
						throw new SemanticException(line);
					}

					if (!SYMBOL_TABLE.getInstance().isInstancable(function_params.head.name))
					{
						throw new SemanticException(line);
					}

					function_params = function_params.tail;
				}
			}
		}

		/* If it's a method (In a class scope) */
		else
		{
			/* Check if the method is declared in the current scope (class scope) */
			TYPE dec_type = SYMBOL_TABLE.getInstance().findInCurrentScope(ID);
			if (dec_type != null)
			{
				throw new SemanticException(line);
			}

			/* If there are arguments, make sure that their types are valid */
			if (typeIDList != null)
			{
				TYPE_LIST method_params = (TYPE_LIST) typeIDList.SemantMe();
				while (method_params != null)
				{
					if (method_params.head.typeEnum == TypeEnum.TYPE_VOID)
					{
						throw new SemanticException(line);
					}

					if (!SYMBOL_TABLE.getInstance().isInstancable(method_params.head.name))
					{
						throw new SemanticException(line);
					}

					method_params = method_params.tail;
				}
			}

			/* Check if the method is declared in parent classes --> Then distinguish between override and overload */
			TYPE_CLASS curr_class = SYMBOL_TABLE.getInstance().current_parent_class;

			method_id = SYMBOL_TABLE.getInstance().current_class.methods_counter;
			SYMBOL_TABLE.getInstance().current_class.methods_counter++;

			while(curr_class != null && !is_found)
			{
				TYPE_LIST curr_data_member = curr_class.data_members;
				while(curr_data_member != null)
				{
					if ((curr_data_member.dataMemberName.equals(ID)))
					{
						if (curr_data_member.head.typeEnum == TypeEnum.TYPE_FUNCTION)
						{
							TYPE_FUNCTION curr_method = (TYPE_FUNCTION) curr_data_member.head;
							TYPE_FUNCTION given_method;
							if (typeIDList != null)
							{
								given_method = new TYPE_FUNCTION(type.SemantMe(), ID, (TYPE_LIST) typeIDList.SemantMe(), SYMBOL_TABLE.getInstance().current_class.name);
							}
							else
							{
								given_method = new TYPE_FUNCTION(type.SemantMe(), ID, null,SYMBOL_TABLE.getInstance().current_class.name);
							}
							/* Check if it's a valid override */
							if (given_method.equals(curr_method))
							{
								/* It's an override */
								SYMBOL_TABLE.getInstance().current_class.methods_counter--;
								method_id = curr_method.func_index;

								is_found = true;
								break;
							}
							else
							{
								/* It's an overload */
								throw new SemanticException(line);
							}
						}
						throw new SemanticException(line);
					}

					curr_data_member = curr_data_member.tail;
				}
				curr_class = curr_class.father;
			}
		}

		/************************/
		/* [1] Begin func Scope */
		/************************/
		SYMBOL_TABLE.getInstance().beginScope("function");

		TYPE_FUNCTION func_type = null;
		if (typeIDList != null)
		{
			if (SYMBOL_TABLE.getInstance().current_class != null) {
				func_type = new TYPE_FUNCTION(type.SemantMe(), ID, (TYPE_LIST) typeIDList.SemantMe(),SYMBOL_TABLE.getInstance().current_class.name);
			}
			else {
				func_type = new TYPE_FUNCTION(type.SemantMe(), ID, (TYPE_LIST) typeIDList.SemantMe(),null);

			}

			TYPE_LIST method_params = (TYPE_LIST) typeIDList.SemantMe();
			int param_counter = 1; /* Counter for the parameter indices */
			while (method_params != null)
			{
				TYPE_VAR param_type = (TYPE_VAR) method_params.head;
				param_type.var_kind = KindEnum.PARAM;
				param_type.var_index = param_counter;
				param_type.var_label = null;
				SYMBOL_TABLE.getInstance().enter(method_params.dataMemberName, param_type, false, SYMBOL_TABLE.getInstance().current_scope);

				param_counter++;
				method_params = method_params.tail;
			}

			parameters_num = param_counter - 1;
		}
		else
		{
			if (SYMBOL_TABLE.getInstance().current_class != null){
				func_type = new TYPE_FUNCTION(type.SemantMe(), ID, null,SYMBOL_TABLE.getInstance().current_class.name);
			}
			else {
				func_type = new TYPE_FUNCTION(type.SemantMe(), ID, null,null);
			}
			parameters_num = 0;
		}

		/***************************************************/
		/* [2] Enter the function Type to the Symbol Table */
		/***************************************************/
		if (SYMBOL_TABLE.getInstance().current_class != null){
			this.epilogue_label = "_"+ID+"_"+SYMBOL_TABLE.getInstance().current_class.name+"_epilogue";
		}
		else
		{
			if (ID.equals("main"))
			{
				this.epilogue_label = "main_epilogue";
			}
			else
			{
				this.epilogue_label = "_"+ID+"_epilogue";
			}
		}
		func_type.epilogue_label = this.epilogue_label;
		SYMBOL_TABLE.getInstance().current_function = func_type;

		/* Check the validity of the statement types, using their SemantMe() function */
		stmtList.SemantMe();

		/*****************/
		/* [3] End Scope */
		/*****************/
		SYMBOL_TABLE.getInstance().endScope();
		SYMBOL_TABLE.getInstance().current_function = null;

		/***************************************************/
		/* [4] Enter the function Type to the Symbol Table */
		/***************************************************/

		/* Add the AST annotations */
		KindEnum func_kind = KindEnum.GLOBAL;
		int func_id = 0;
		String func_label = null;

		/* If it's a method */
		if (is_method)
		{
			this.class_name = SYMBOL_TABLE.getInstance().current_class.name;
			func_kind = KindEnum.METHOD;
			func_id = method_id;
		}
		/* Else, it's a function */
		else
		{
			this.class_name = null;
			func_label = "_"+ID;
		}

		func_type.func_kind = func_kind;
		func_type.func_index = func_id;
		func_type.func_label = func_label;

		SYMBOL_TABLE.getInstance().enter(ID, func_type, false, SYMBOL_TABLE.getInstance().current_scope);

		this.local_var_num = func_type.local_counter - 1;

		/* Return the TYPE_FUNCTION */
		return func_type;
	}

	public TEMP IRme()
	{
		/*
		* 0) Jump to the _function_name_end (V)
		* 1) _function_name_start label     (V)
		* 2) Function prologue              (V)
		* 3) Function body                  (V)
		* 4) Function epilogue              (V)
		* 5) _function_name_end label       (V)
		 */
		String start_label;
		String end_label;
		String epilogue_label;
		/* If it's a method */
		if (this.class_name != null)
		{
			start_label = "_"+ID+"_"+this.class_name;
			end_label = "_"+ID+"_"+this.class_name+"_end";
			epilogue_label = "_"+ID+"_"+this.class_name+"_epilogue";
		}
		else
		{
			if (ID.equals("main"))
			{
				start_label = "main";
				end_label = "main_end";
				epilogue_label = "main_epilogue";
			}
			else
			{
				start_label = "_"+ID;
				end_label = "_"+ID+"_end";
				epilogue_label = "_"+ID+"_epilogue";
			}
		}

		IR.getInstance().Add_IRcommand(new IRcommand_Jump_Label(end_label));
		IR.getInstance().Add_IRcommand(new IRcommand_Label(start_label , labelEnum.FUNCSTART));
		IR.getInstance().Add_IRcommand(new IRcommand_Prologue(this.local_var_num));
		/* BODY - start */
		stmtList.IRme();
		/* BODY - end */
		IR.getInstance().Add_IRcommand(new IRcommand_Epilogue(epilogue_label));
		IR.getInstance().Add_IRcommand(new IRcommand_Label(end_label , labelEnum.FUNCEND));

		return null;
	}
}
