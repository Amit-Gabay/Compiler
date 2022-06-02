package IR;

public class Function_Start_End_List {
    public IRcommandList start;
    public IRcommandList end;
    public int size;
    public Function_Start_End_List next;

    public Function_Start_End_List(IRcommandList start,IRcommandList end, int size){
        this.start = start;
        this.end = end;
        this.size = size;
        this.next = null;
    }

}
