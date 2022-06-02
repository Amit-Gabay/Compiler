package IR;

import TEMP.*;
import MIPS.*;

public class IRcommand_Declare_Global_Var extends IRcommand
{
    public String var_label;
    public int default_int_val = 0;
    public String default_str_label = null;

    public IRcommand_Declare_Global_Var(String var_label, int default_int_val)
    {
        this.var_label = var_label;
        this.default_int_val = default_int_val;
    }

    public IRcommand_Declare_Global_Var(String var_label, String default_str_label)
    {
        this.var_label = var_label;
        this.default_str_label = default_str_label;
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().declare_global_var(var_label, default_int_val, default_str_label);
    }
}
