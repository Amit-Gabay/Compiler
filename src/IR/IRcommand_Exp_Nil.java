package IR;

import TEMP.*;
import MIPS.*;

public class IRcommand_Exp_Nil extends IRcommand
{
    public TEMP dst_register;

    public IRcommand_Exp_Nil(TEMP dst_register)
    {
        this.dst_register = dst_register;
        this.destination = dst_register.serial;
        this.all_temps.add(dst_register);
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().assign_nil(dst_register);
    }
}
