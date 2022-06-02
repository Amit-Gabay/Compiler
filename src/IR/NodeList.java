package IR;

public class NodeList {
    public CFG_Node data;
    public NodeList next;

    public NodeList(CFG_Node data){
        this.data = data;
        this.next = null;
    }

}
