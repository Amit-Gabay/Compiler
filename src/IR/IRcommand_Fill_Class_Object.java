package IR;

import TYPES.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Fill_Class_Object extends IRcommand
{
    public TEMP class_object_register;
    public String class_name;
    public TYPE_LIST class_members;

    public IRcommand_Fill_Class_Object(TEMP class_object_register, String class_name, TYPE_LIST class_members)
    {
        this.class_object_register = class_object_register;
        this.class_name = class_name;
        this.class_members = class_members;
        this.destination = class_object_register.serial;
        this.all_temps.add(class_object_register);
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().fill_class_object(class_object_register, class_name, class_members);
    }
}
