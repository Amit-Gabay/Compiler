package IR;

import TEMP.*;
import MIPS.*;

public class IRcommand_Prologue extends IRcommand
{
    public int local_var_num;

    public IRcommand_Prologue(int local_var_num)
    {
        this.local_var_num = local_var_num;
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().prologue(this.local_var_num);
    }
}
