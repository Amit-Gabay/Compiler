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

public class IRcommand_Binop_EQ_Strings extends IRcommand
{
    public TEMP left_str;
    public TEMP right_str;
    public TEMP dst_register;

    public IRcommand_Binop_EQ_Strings(TEMP dst_register, TEMP left_str, TEMP right_str)
    {
        this.dst_register = dst_register;
        this.left_str = left_str;
        this.right_str = right_str;
        this.destination = dst_register.serial;
        this.DependsOn.add(left_str.serial);
        this.DependsOn.add(right_str.serial);
        this.all_temps.add(left_str);
        this.all_temps.add(right_str);
        this.all_temps.add(dst_register);
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().str_cmp(dst_register, left_str, right_str);
    }
}
