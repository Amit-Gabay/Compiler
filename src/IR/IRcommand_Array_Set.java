package IR;

import TEMP.*;
import MIPS.*;

public class IRcommand_Array_Set extends IRcommand
{
    public TEMP array_register;
    public TEMP index_register;
    public TEMP value_register = null;
    /* Additional fields for cases which the assigned value is CONSTANT (int / string / nil) */
    public int constant_int = 0;
    public String constant_str_label = null;

    public IRcommand_Array_Set(TEMP array_register, TEMP index_register, TEMP value_register)
    {
        this.array_register = array_register;
        this.index_register = index_register;
        this.value_register = value_register;

        this.DependsOn.add(array_register.serial);
        this.DependsOn.add(index_register.serial);
        this.DependsOn.add(value_register.serial);
        this.all_temps.add(array_register);
        this.all_temps.add(index_register);
        this.all_temps.add(value_register);
    }

    public IRcommand_Array_Set(TEMP array_register, TEMP index_register, int constant_int)
    {
        this.array_register = array_register;
        this.index_register = index_register;
        this.constant_int = constant_int;

        this.DependsOn.add(array_register.serial);
        this.DependsOn.add(index_register.serial);
        this.all_temps.add(array_register);
        this.all_temps.add(index_register);
    }

    public IRcommand_Array_Set(TEMP array_register, TEMP index_register, String constant_str_label)
    {
        this.array_register = array_register;
        this.index_register = index_register;
        this.constant_str_label = constant_str_label;

        this.DependsOn.add(array_register.serial);
        this.DependsOn.add(index_register.serial);
        this.all_temps.add(array_register);
        this.all_temps.add(index_register);
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().check_access_violation(array_register, index_register);
        MIPSGenerator.getInstance().array_set(array_register, index_register, value_register, constant_int, constant_str_label);
    }
}
