package IR;

import TEMP.*;
import MIPS.*;

public class IRcommand_Exp_Int extends IRcommand
{
    public TEMP dst_register;
    public int value;

    public IRcommand_Exp_Int(TEMP dst_register, int value)
    {
        this.dst_register = dst_register;
        this.value = value;
        this.destination = dst_register.serial;
        this.all_temps.add(dst_register);
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().assign_int(dst_register, value);
    }
}
