package IR;

import MIPS.*;
import TEMP.*;

public class IRcommand_Array_Access extends IRcommand
{
    public TEMP dst_register;
    public TEMP array_register;
    public TEMP subscript_register;

    public IRcommand_Array_Access(TEMP dst_register, TEMP array_register, TEMP subscript_register)
    {
        this.dst_register = dst_register;
        this.array_register = array_register;
        this.subscript_register = subscript_register;
        this.destination = dst_register.serial;
        this.DependsOn.add(array_register.serial);
        this.DependsOn.add(subscript_register.serial);
        this.all_temps.add(dst_register);
        this.all_temps.add(array_register);
        this.all_temps.add(subscript_register);
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().check_access_violation(array_register, subscript_register);
        MIPSGenerator.getInstance().array_access(dst_register, array_register, subscript_register);
    }
}
