package AST;

import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_EXP_BINOP extends AST_EXP
{
	int OP;
	public AST_EXP left;
	public AST_EXP right;

	public TYPE operands_type;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_BINOP(AST_EXP left, AST_EXP right, int OP, int line)
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
		this.left = left;
		this.right = right;
		this.OP = OP;
	}
	
	/*************************************************/
	/* The printing message for a binop exp AST node */
	/*************************************************/
	public void PrintMe()
	{
		String sOP="";
		
		/*********************************/
		/* CONVERT OP to a printable sOP */
		/*********************************/
		if (OP == 0) {sOP = "+";}
		else if (OP == 1) {sOP = "-";}
		else if (OP == 2) {sOP = "*";}
		else if (OP == 3) {sOP = "/";}
		else if (OP == 4) {sOP = "<";}
		else if (OP == 5) {sOP = ">";}
		else if (OP == 6) {sOP = "=";}
		
		/*************************************/
		/* AST NODE TYPE = AST BINOP EXP */
		/*************************************/

		/**************************************/
		/* RECURSIVELY PRINT left + right ... */
		/**************************************/
		if (left != null) left.PrintMe();
		if (right != null) right.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("EXP\nBINOP(%s)",sOP));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (left  != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,left.SerialNumber);
		if (right != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,right.SerialNumber);
	}
	public TYPE SemantMe() throws SemanticException
	{
		TYPE left_type = null;
		TYPE right_type = null;

		if (left  != null) left_type = left.SemantMe();
		if (right != null) right_type = right.SemantMe();

		/* There are no operators on functions */
		if (left_type.typeEnum == TypeEnum.TYPE_FUNCTION || right_type.typeEnum == TypeEnum.TYPE_FUNCTION)
		{
			throw new SemanticException(line);
		}

		/* EQ (Compare) */
		/* Allow equality between same types */
		if (OP == 6 /* EQ */ && left_type.typeEnum == right_type.typeEnum)
		{
			if (left_type.typeEnum == TypeEnum.TYPE_ARRAY)
			{
				TYPE_ARRAY left_array = (TYPE_ARRAY) left_type;
				TYPE_ARRAY right_array = (TYPE_ARRAY) right_type;
				this.operands_type = TYPE_INT.getInstance();
				if (!left_array.equals(right_array)) throw new SemanticException(line);
			}
			else if (left_type.typeEnum == TypeEnum.TYPE_CLASS)
			{
				TYPE_CLASS left_class = (TYPE_CLASS) left_type;
				TYPE_CLASS right_class = (TYPE_CLASS) right_type;
				this.operands_type = TYPE_INT.getInstance();
				if ((!left_class.is_replacable(right_class)) && (!right_class.is_replacable(left_class))) throw new SemanticException(line);
			}
			else if (left_type.typeEnum == TypeEnum.TYPE_INT)
			{
				this.operands_type = TYPE_INT.getInstance();
			}
			else if (left_type.typeEnum == TypeEnum.TYPE_STRING)
			{
				this.operands_type = TYPE_STRING.getInstance();
			}
			return TYPE_INT.getInstance();
		}

		/* Allow to compare array / class with NIL */
		else if ((right_type.typeEnum == TypeEnum.TYPE_NIL) && (left_type.typeEnum == TypeEnum.TYPE_CLASS || left_type.typeEnum == TypeEnum.TYPE_ARRAY) && OP == 6)
		{
			this.operands_type = TYPE_INT.getInstance();
			return TYPE_INT.getInstance();
		}

		else if ((left_type.typeEnum == TypeEnum.TYPE_NIL) && (right_type.typeEnum == TypeEnum.TYPE_CLASS || right_type.typeEnum == TypeEnum.TYPE_ARRAY) && OP == 6)
		{
			this.operands_type = TYPE_INT.getInstance();
			return TYPE_INT.getInstance();
		}

		/* INT operators */
		/* Allow integer operators except of zero DIVISION */
		else if ((left_type.typeEnum == TypeEnum.TYPE_INT) && (right_type.typeEnum == TypeEnum.TYPE_INT))
		{
			if (OP == 3 /* Division */ && right instanceof AST_EXP_INT && ((AST_EXP_INT) right).value == 0)
			{
				throw new SemanticException(line);
			}
			this.operands_type = TYPE_INT.getInstance();
			return TYPE_INT.getInstance();
		}

		/* STRING operators */
		/* Allow strings PLUS operator */
		else if ((left_type.typeEnum == TypeEnum.TYPE_STRING) && (right_type.typeEnum == TypeEnum.TYPE_STRING) && OP == 0 /* Plus */)
		{
			this.operands_type = TYPE_STRING.getInstance();
			return TYPE_STRING.getInstance();
		}

		throw new SemanticException(line);
	}

	public TEMP IRme()
	{
		TEMP t1 = null;
		TEMP t2 = null;
		TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();

		if (left  != null) t1 = left.IRme();
		if (right != null) t2 = right.IRme();

		if (OP == 0 /*Add*/)
		{
			if (this.operands_type.typeEnum == TypeEnum.TYPE_INT)
			{
				IR.getInstance().Add_IRcommand(new IRcommand_Binop_Add_Integers(dst,t1,t2));
			}
			else if (this.operands_type.typeEnum == TypeEnum.TYPE_STRING)/* TypeEnum.TYPE_STRING */
			{
				IR.getInstance().Add_IRcommand(new IRcommand_Binop_Add_Strings(dst,t1,t2));
			}
		}
		if (OP == 1 /*Subtract*/)
		{
			IR.getInstance().Add_IRcommand(new IRcommand_Binop_Sub_Integers(dst,t1,t2));
		}
		if (OP == 2 /*Multiply*/)
		{
			IR.getInstance().Add_IRcommand(new IRcommand_Binop_Mul_Integers(dst,t1,t2));
		}
		if (OP == 3 /*Division*/)
		{
			IR.getInstance().Add_IRcommand(new IRcommand_Binop_Div_Integers(dst,t1,t2));
		}
		if (OP == 4 /*LT*/)
		{
			IR.getInstance().Add_IRcommand(new IRcommand_Binop_LT_Integers(dst,t1,t2));
		}
		if (OP == 5 /*GT*/)
		{
			IR.getInstance().Add_IRcommand(new IRcommand_Binop_LT_Integers(dst,t2,t1));
		}
		if (OP == 6 /*EQ*/)
		{
			if (this.operands_type.typeEnum == TypeEnum.TYPE_INT)
			{
				// includes arrays and objects
				IR.getInstance().Add_IRcommand(new IRcommand_Binop_EQ_Integers(dst,t1,t2));
			}
			else if (this.operands_type.typeEnum == TypeEnum.TYPE_STRING)
			{
				IR.getInstance().Add_IRcommand(new IRcommand_Binop_EQ_Strings(dst,t1,t2));
			}
		}
		return dst;
	}
}

