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

// no need for this class

public class IRcommand_PrintInt extends IRcommand
{
	TEMP int_register;
	
	public IRcommand_PrintInt(TEMP int_register)
	{
		this.int_register = int_register;
	}
	

	public void MIPSme()
	{
		MIPSGenerator.getInstance().print_int(int_register);
	}
}
