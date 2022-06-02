package IR;

import TEMP.*;
import MIPS.*;

public class IRcommand_Allocate_String extends IRcommand
{
    public String str_label;
    public String str_value;

    public IRcommand_Allocate_String(String str_label, String str_value)
    {
        this.str_label = str_label;
        this.str_value = str_value;
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().data_allocate_string(null, str_value, str_label);
    }
}
