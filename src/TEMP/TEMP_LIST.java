package TEMP;

public class TEMP_LIST
{
    public TEMP data;
    public TEMP_LIST next;
    public int length;

    public TEMP_LIST(TEMP first)
    {
        this.data = first;
        this.next = null;
        this.length = 1;
    }
}
