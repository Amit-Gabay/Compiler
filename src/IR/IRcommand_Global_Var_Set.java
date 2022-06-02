package IR;

import TEMP.*;
import MIPS.*;

public class IRcommand_Global_Var_Set extends IRcommand
{
    public String var_label;
    public TEMP value_register = null;
    /* Additional fields for cases which the assigned value is CONSTANT (int / string / nil) */
    public int constant_int = 0;
    public String constant_str_label = null;

    public IRcommand_Global_Var_Set(String var_label, TEMP value_register)
    {
        this.var_label = var_label;
        this.value_register = value_register;
        this.DependsOn.add(value_register.serial);
        this.all_temps.add(value_register);
    }

    public IRcommand_Global_Var_Set(String var_label, int constant_int)
    {
        this.var_label = var_label;
        this.constant_int = constant_int;
    }

    public IRcommand_Global_Var_Set(String var_label, String constant_str_label)
    {
        this.var_label = var_label;
        this.constant_str_label = constant_str_label;
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().global_var_set(var_label, value_register, constant_int, constant_str_label);
    }
}
