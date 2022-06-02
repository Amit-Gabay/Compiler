package IR;

import TEMP.*;
import MIPS.*;

public class IRcommand_Param_Access extends IRcommand
{
    public TEMP dst_register;
    public int param_var_id;

    public IRcommand_Param_Access(TEMP dst_register, int param_var_id)
    {
        this.dst_register = dst_register;
        this.param_var_id = param_var_id;
        this.destination = dst_register.serial;
        this.all_temps.add(dst_register);
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().param_access(dst_register, param_var_id);
    }
}
