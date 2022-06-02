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
import TEMP.TEMP;

import java.util.HashSet;
import java.util.Set;

public abstract class IRcommand
{
	/*****************/
	/* Label Factory */
	/*****************/
	public Set<Integer> DependsOn = new HashSet<Integer>();
	public int destination = -1;
	public Set<TEMP> all_temps = new HashSet<TEMP>();

	protected static int label_counter=0;
	public    static String getFreshLabel(String msg)
	{
		return String.format("Label_%d_%s",label_counter++,msg);
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public abstract void MIPSme();
}
