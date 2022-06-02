package IR;

import TEMP.*;
import MIPS.*;

public class IRcommand_Local_Var_Set extends IRcommand
{
    public int local_var_id;
    public TEMP value_register = null;
    /* Additional fields for cases which the assigned value is CONSTANT (int / string / nil) */
    public int constant_int = 0;
    public String constant_str_label = null;

    public IRcommand_Local_Var_Set(int local_var_id, TEMP value_register)
    {
        this.local_var_id = local_var_id;
        this.value_register = value_register;
        if (value_register != null)
        {
            this.DependsOn.add(value_register.serial);
            this.all_temps.add(value_register);
        }
    }

    public IRcommand_Local_Var_Set(int local_var_id, int constant_int)
    {
        this.local_var_id = local_var_id;
        this.constant_int = constant_int;
    }

    public IRcommand_Local_Var_Set(int local_Var_id, String constant_str_label)
    {
        this.local_var_id = local_Var_id;
        this.constant_str_label = constant_str_label;
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().local_var_set(local_var_id, value_register, constant_int, constant_str_label);
    }
}
