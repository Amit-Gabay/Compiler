package IR;

import TEMP.*;
import MIPS.*;

public class IRcommand_Return extends IRcommand
{
    public TEMP exp_register;
    public String epilogue_label;

    public IRcommand_Return(TEMP exp_register, String epilogue_label)
    {
        this.exp_register = exp_register;
        this.epilogue_label = epilogue_label;
        if (exp_register != null){
            this.DependsOn.add(exp_register.serial);
            this.all_temps.add(exp_register);
        }
    }

    public void MIPSme()
    {
        if (exp_register != null){
            MIPSGenerator.getInstance().return_exp(exp_register);
        }
        MIPSGenerator.getInstance().jump(epilogue_label);
    }
}
