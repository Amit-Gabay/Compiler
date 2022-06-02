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

public class IRcommand_Jump_If_Eq_To_Zero extends IRcommand
{
	TEMP condition_register;
	String label_name;
	
	public IRcommand_Jump_If_Eq_To_Zero(TEMP condition_register, String label_name)
	{
		this.condition_register = condition_register;
		this.label_name = label_name;
		this.DependsOn.add(condition_register.serial);
		this.all_temps.add(condition_register);
	}

	public void MIPSme()
	{
		MIPSGenerator.getInstance().beqz(condition_register, label_name);
	}
}
