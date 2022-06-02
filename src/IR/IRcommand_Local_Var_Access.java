package IR;

import TEMP.*;
import MIPS.*;

public class IRcommand_Local_Var_Access extends IRcommand
{
    public TEMP dst_resgister;
    public int local_var_id;

    public IRcommand_Local_Var_Access(TEMP dst_register, int local_var_id)
    {
        this.dst_resgister = dst_register;
        this.local_var_id = local_var_id;
        this.destination = dst_register.serial;
        this.all_temps.add(dst_register);
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().local_var_access(dst_resgister, local_var_id);
    }
}
