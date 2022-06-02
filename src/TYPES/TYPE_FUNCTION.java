package TYPES;

import SYMBOL_TABLE.*;

import java.util.Objects;

public class TYPE_FUNCTION extends TYPE
{
	/***********************************/
	/* The return type of the function */
	/***********************************/
	public TYPE returnType;

	/*************************/
	/* types of input params */
	/*************************/
	public TYPE_LIST params;

	/* Local variables indexing counter */
	public int local_counter;

	/* AST Annotations */
	public KindEnum func_kind;
	public int func_index;
	public String func_label;

	public String epilogue_label;

	public String class_name;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_FUNCTION(TYPE returnType, String name, TYPE_LIST params, String class_name)
	{
		this.name = name;
		this.typeEnum = TypeEnum.TYPE_FUNCTION;
		this.returnType = returnType;
		this.params = params;
		this.local_counter = 1;
		this.class_name = class_name;
	}

	static public boolean is_recursive_call(String ID, TYPE_LIST args)
	{
		TYPE_FUNCTION outer_func = SYMBOL_TABLE.getInstance().current_function;
		if (outer_func == null) return false;
		if (!outer_func.name.equals(ID)) return false;
		TYPE_LIST outer_params = outer_func.params;
		TYPE_LIST inner_params = args;
		while (outer_params != null && inner_params != null)
		{
			if (outer_params.head.typeEnum != inner_params.head.typeEnum) return false;
			if (outer_params.head.typeEnum == TypeEnum.TYPE_ARRAY)
			{
				TYPE_ARRAY outer_arr = (TYPE_ARRAY) ((TYPE_VAR) outer_params.head).var_type;
				TYPE_ARRAY inner_arr = (TYPE_ARRAY) inner_params.head;
				if (!outer_arr.is_replacable(inner_arr)) return false;
			}
			if (outer_params.head.typeEnum == TypeEnum.TYPE_CLASS)
			{
				TYPE_CLASS outer_class = (TYPE_CLASS) ((TYPE_VAR) outer_params.head).var_type;
				TYPE_CLASS inner_class = (TYPE_CLASS) inner_params.head;
				if (!outer_class.is_replacable(inner_class)) return false;
			}

			outer_params = outer_params.tail;
			inner_params = inner_params.tail;
		}
		if (outer_params != null || inner_params != null) return false;

		return true;
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof TYPE_FUNCTION)) return false;

		TYPE_FUNCTION given = (TYPE_FUNCTION) o;

		/* Compare names */
		if (!this.name.equals(given.name)) return false;

		/* Compare return types */
		if (this.returnType.typeEnum != given.returnType.typeEnum) return false;
		if (this.returnType.typeEnum == TypeEnum.TYPE_CLASS)
		{
			TYPE_CLASS this_return = (TYPE_CLASS) this.returnType;
			TYPE_CLASS given_return = (TYPE_CLASS) given.returnType;
			if (!this_return.equals(given_return)) return false;
		}
		if (this.returnType.typeEnum == TypeEnum.TYPE_ARRAY)
		{
			TYPE_ARRAY this_return = (TYPE_ARRAY) this.returnType;
			TYPE_ARRAY given_return = (TYPE_ARRAY) given.returnType;
			if (!this_return.equals(given_return)) return false;
		}

		/* Compare parameters */
		TYPE_LIST given_param = given.params;
		TYPE_LIST this_param = this.params;

		while (given_param != null && this_param != null)
		{
			if (given_param.head.typeEnum != this_param.head.typeEnum) return false;
			if (given_param.head.typeEnum == TypeEnum.TYPE_FUNCTION) return false;
			if (given_param.head.typeEnum == TypeEnum.TYPE_CLASS)
			{
				TYPE_CLASS given_class = (TYPE_CLASS) given_param.head;
				TYPE_CLASS this_class = (TYPE_CLASS) this_param.head;
				if (!this_class.equals(given_class)) return false;
			}
			if (given_param.head.typeEnum == TypeEnum.TYPE_ARRAY)
			{
				TYPE_ARRAY given_array = (TYPE_ARRAY) given_param.head;
				TYPE_ARRAY this_array = (TYPE_ARRAY) this_param.head;
				if (!this_array.equals(given_array)) return false;
			}

			given_param = given_param.tail;
			this_param = this_param.tail;
		}
		if (given_param != null || this_param != null) return false;

		return true;
	}
}