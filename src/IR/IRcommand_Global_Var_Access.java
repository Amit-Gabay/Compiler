package IR;

import TEMP.*;
import MIPS.*;

public class IRcommand_Global_Var_Access extends IRcommand
{
    public TEMP dst_register;
    public String var_label;

    public IRcommand_Global_Var_Access(TEMP dst_register, String var_label)
    {
        this.dst_register = dst_register;
        this.var_label = var_label;
        this.destination = dst_register.serial;
        this.all_temps.add(dst_register);
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().global_var_access(dst_register, var_label);
    }
}
