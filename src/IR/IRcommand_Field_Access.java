package IR;

import TEMP.*;
import MIPS.*;

public class IRcommand_Field_Access extends IRcommand
{
    public TEMP result_register;
    public TEMP class_register;
    public int field_index;

    public IRcommand_Field_Access(TEMP result_register, TEMP class_register, int field_index)
    {
        this.result_register = result_register;
        this.class_register = class_register;
        this.field_index = field_index;

        this.destination = result_register.serial;
        this.DependsOn.add(class_register.serial);
        this.all_temps.add(result_register);
        this.all_temps.add(class_register);
    }

    public IRcommand_Field_Access(TEMP result_register, int field_index)
    {
        this.result_register = result_register;
        this.class_register = null; /* The class object pointer is in $s6 */
        this.field_index = field_index;

        this.destination = result_register.serial;
        this.all_temps.add(result_register);
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().check_invalid_dereference(class_register);
        MIPSGenerator.getInstance().field_access(result_register, class_register, field_index);
    }
}
