package IR;

import MIPS.MIPSGenerator;

public class IRcommand_Epilogue extends IRcommand
{
    public String epilogue_label;

    public IRcommand_Epilogue(String epilogue_label)
    {
        this.epilogue_label = epilogue_label;
    }

    public void MIPSme()
    {
        MIPSGenerator.getInstance().epilogue(this.epilogue_label);
    }
}
