package IR;

import MIPS.MIPSGenerator;
import TEMP.*;

public class IRcommand_Field_Set extends IRcommand
{
    public TEMP class_register;
    public int field_index;
    public TEMP value_register = null;
    /* Additional fields for cases which the assigned value is CONSTANT (int / string / nil) */
    public int constant_int = 0;
    public String constant_str_label = null;

    public IRcommand_Field_Set(TEMP class_register, int field_index, TEMP value_register)
    {
        this.class_register = class_register;
        this.field_index = field_index;
        this.value_register = value_register;

        this.DependsOn.add(class_register.serial);
        this.DependsOn.add(value_register.serial);
        this.all_temps.add(class_register);
        this.all_temps.add(value_register);
    }

    public IRcommand_Field_Set(int field_index, TEMP value_register)
    {
        this.class_register = null;
        this.field_index = field_index;
        this.value_register = value_register;

        this.DependsOn.add(value_register.serial);
        this.all_temps.add(value_register);
    }

    public IRcommand_Field_Set(TEMP class_register, int field_index, int constant_int)
    {
        this.class_register = class_register;
        this.field_index = field_index;
        this.constant_int = constant_int;

        this.DependsOn.add(class_register.serial);
        this.all_temps.add(class_register);
    }

    public IRcommand_Field_Set(int field_index, int constant_int)
    {
        this.class_register = null;
        this.field_index = field_index;
        this.constant_int = constant_int;
    }

    public IRcommand_Field_Set(TEMP class_register, int field_index, String constant_str_label)
    {
        this.class_register = class_register;
        this.field_index = field_index;
        this.constant_str_label = constant_str_label;

        this.DependsOn.add(class_register.serial);
        this.all_temps.add(class_register);
    }

    public IRcommand_Field_Set(int field_index, String constant_str_label)
    {
        this.class_register = null;
        this.field_index = field_index;
        this.constant_str_label = constant_str_label;
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().check_invalid_dereference(class_register);
        MIPSGenerator.getInstance().field_set(class_register, field_index, value_register, constant_int, constant_str_label);
    }
}
