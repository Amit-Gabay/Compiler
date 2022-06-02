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

public class IRcommand_Label extends IRcommand
{
	String label_name;
	labelEnum label_type;
	
	public IRcommand_Label(String label_name, labelEnum label_type)
	{
		this.label_name = label_name;
		this.label_type = label_type;
	}

	public void MIPSme()
	{
		MIPSGenerator.getInstance().label(label_name);
	}
}
