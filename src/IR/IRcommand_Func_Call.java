package IR;

import TEMP.*;
import MIPS.*;

public class IRcommand_Func_Call extends IRcommand
{
    public TEMP_LIST params_tmplist;
    public TEMP result;
    public String func_label;

    public IRcommand_Func_Call(TEMP result, String func_label, TEMP_LIST params_tmplist)
    {
        /* If result == null, then there's no need to store the function call result */
        this.result = result;
        this.func_label = func_label;
        this.params_tmplist = params_tmplist;
        if (result != null){
            this.destination = result.serial;
            this.all_temps.add(result);
        }
        TEMP_LIST cur = params_tmplist;
        while (cur != null) {
            this.DependsOn.add(cur.data.serial);
            this.all_temps.add(cur.data);
            cur = cur.next;
        }
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().before_call(params_tmplist);
        MIPSGenerator.getInstance().call_function(func_label);
        MIPSGenerator.getInstance().after_call(result, params_tmplist);
    }
}
