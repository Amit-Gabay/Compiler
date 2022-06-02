package IR;

import MIPS.*;

public class IRcommand_Exit extends IRcommand
{
    public IRcommand_Exit()
    {}

    public void MIPSme()
    {
        MIPSGenerator.getInstance().exit();
    }
}
