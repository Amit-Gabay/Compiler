package IR;

import MIPS.MIPSGenerator;
import TEMP.*;

public class IRcommand_Param_Set extends IRcommand
{
    public int param_var_id;
    public TEMP value_register = null;
    /* Additional fields for cases which the assigned value is CONSTANT (int / string / nil) */
    public int constant_int = 0;
    public String constant_str_label = null;

    public IRcommand_Param_Set(int param_var_id, TEMP value_register)
    {
        this.param_var_id = param_var_id;
        this.value_register = value_register;
        this.DependsOn.add(value_register.serial);
        this.all_temps.add(value_register);
    }

    public IRcommand_Param_Set(int param_var_id, int constant_int)
    {
        this.param_var_id = param_var_id;
        this.constant_int = constant_int;
    }

    public IRcommand_Param_Set(int param_var_id, String constant_str_label)
    {
        this.param_var_id = param_var_id;
        this.constant_str_label = constant_str_label;
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().param_set(param_var_id, value_register, constant_int, constant_str_label);
    }
}
