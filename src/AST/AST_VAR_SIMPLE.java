package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_VAR_SIMPLE extends AST_VAR
{
	/************************/
	/* simple variable name */
	/************************/
	public String name;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_VAR_SIMPLE(String name, int line)
	{
		this.line = line;
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.name = name;
	}

	/**************************************************/
	/* The printing message for a simple var AST node */
	/**************************************************/
	public void PrintMe()
	{
		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("VAR\nSIMPLE(%s)",name));
	}

	public TYPE SemantMe() throws SemanticException
	{
		/* Make sure that the variable is already declared */
		TYPE found_type = SYMBOL_TABLE.getInstance().find(name);
		if (found_type != null)
		{
			/* Make sure that the variable is NOT a function */
			if (found_type.typeEnum != TypeEnum.TYPE_FUNCTION)
			{
				TYPE_VAR var_type = (TYPE_VAR) found_type;
				/* Add the AST annotations */
				this.var_kind = var_type.var_kind;
				this.var_index = var_type.var_index;
				this.var_label = var_type.var_label;

				return var_type;
			}
		}
		throw new SemanticException(line);
	}

	public TEMP IRme()
	{
		/* If we got here, it has to be a variable access */
		TEMP result_register = TEMP_FACTORY.getInstance().getFreshTEMP();

		if (this.var_kind == KindEnum.GLOBAL)
		{
			IR.getInstance().Add_IRcommand(new IRcommand_Global_Var_Access(result_register, this.var_label));
		}

		else if (this.var_kind == KindEnum.LOCAL)
		{
			IR.getInstance().Add_IRcommand(new IRcommand_Local_Var_Access(result_register, this.var_index));
		}

		else if (this.var_kind == KindEnum.FIELD)
		{
			/* class_register is not passed: means that the class object pointer is in $s6 */
			IR.getInstance().Add_IRcommand(new IRcommand_Field_Access(result_register, this.var_index));
		}

		else
		{
			IR.getInstance().Add_IRcommand(new IRcommand_Param_Access(result_register, this.var_index));
		}

		return result_register;
	}
}
