package IR;

import TEMP.*;
import MIPS.*;

public class IRcommand_PrintString extends IRcommand
{
    public TEMP str_register;

    public IRcommand_PrintString(TEMP str_register)
    {
        this.str_register = str_register;
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().print_string(str_register);
    }
}
