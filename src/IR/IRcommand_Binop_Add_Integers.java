/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;
import MIPS.*;

public class IRcommand_Binop_Add_Integers extends IRcommand
{
	public TEMP left_operand;
	public TEMP right_operand;
	public TEMP dst_register;
	
	public IRcommand_Binop_Add_Integers(TEMP dst_register, TEMP left_operand, TEMP right_operand)
	{
		this.dst_register = dst_register;
		this.left_operand = left_operand;
		this.right_operand = right_operand;
		this.destination = dst_register.serial;
		this.DependsOn.add(left_operand.serial);
		this.DependsOn.add(right_operand.serial);
		this.all_temps.add(left_operand);
		this.all_temps.add(right_operand);
		this.all_temps.add(dst_register);
	}

	public void MIPSme()
	{
		MIPSGenerator.getInstance().add(dst_register, left_operand, right_operand);
		MIPSGenerator.getInstance().fix_overflow(dst_register);
	}
}
