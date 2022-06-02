package IR;

import MIPS.*;
import TEMP.*;

public class IRcommand_Heap_Allocate extends IRcommand
{
    public TEMP dst_register;
    public int alloc_size;
    public TEMP alloc_size_register;
    public String class_name;

    public IRcommand_Heap_Allocate(TEMP dst_register, int alloc_size, String class_name)
    {
        this.dst_register = dst_register;
        this.alloc_size = alloc_size;
        this.class_name = class_name;
        this.alloc_size_register = null;
        this.destination = dst_register.serial;
        this.all_temps.add(dst_register);
    }

    public IRcommand_Heap_Allocate(TEMP dst_register, TEMP alloc_size_register)
    {
        this.dst_register = dst_register;
        this.alloc_size = 0;
        this.alloc_size_register = alloc_size_register;
        this.destination = dst_register.serial;
        this.DependsOn.add(alloc_size_register.serial);
        this.all_temps.add(dst_register);
        this.all_temps.add(alloc_size_register);
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().heap_allocate(dst_register, alloc_size, alloc_size_register, class_name);
    }
}
