package IR;

public class CFG_Node {
    int node_num;
    IRcommand command;
    NodeList neighbours;

    public CFG_Node(int node_num , IRcommand command){
        this.node_num = node_num;
        this.command = command;
        this.neighbours = null;
    }

}
