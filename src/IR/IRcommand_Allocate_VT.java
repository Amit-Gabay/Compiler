package IR;

import TEMP.*;
import MIPS.*;

import java.util.Arrays;

public class IRcommand_Allocate_VT extends IRcommand
{
    public String class_name;
    public String method_labels[];

    public IRcommand_Allocate_VT(String class_name, String method_labels[])
    {
        this.class_name = class_name;
        this.method_labels = method_labels;
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().allocate_vt(class_name, method_labels);
    }
}
