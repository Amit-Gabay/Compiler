package IR;

import MIPS.*;
import TEMP.*;

public class IRcommand_Exp_String extends IRcommand
{
    public TEMP dst_register;
    public String str_value;
    public String str_label;

    public IRcommand_Exp_String(TEMP dst_register, String str_value, String str_label)
    {
        this.dst_register = dst_register;
        this.str_value = str_value;
        this.str_label = str_label;
        this.destination = dst_register.serial;
        this.all_temps.add(dst_register);
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().data_allocate_string(dst_register, str_value, str_label);
    }
}
