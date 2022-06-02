package IR;

import MIPS.MIPSGenerator;
import TEMP.TEMP;
import TEMP.TEMP_LIST;

public class IRcommand_Method_Call extends IRcommand
{
    public TEMP class_temp;
    public TEMP_LIST params_tmplist;
    public TEMP result;
    public int method_id;

    public IRcommand_Method_Call(TEMP result, TEMP class_temp, int method_id, TEMP_LIST params_tmplist)
    {
        /* If result == null, then there's no need to store the method call result */
        this.result = result;
        this.class_temp = class_temp;
        this.method_id = method_id;
        this.params_tmplist = params_tmplist;
        if (result != null) {
            this.destination = result.serial;
            this.all_temps.add(result);
        }
        TEMP_LIST cur = params_tmplist;
        while (cur != null) {
            this.DependsOn.add(cur.data.serial);
            this.all_temps.add(cur.data);
            cur = cur.next;
        }
        this.DependsOn.add(class_temp.serial);
        this.all_temps.add(class_temp);
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().before_call(params_tmplist);
        MIPSGenerator.getInstance().call_method(class_temp, method_id);
        MIPSGenerator.getInstance().after_call(result, params_tmplist);
    }
}
