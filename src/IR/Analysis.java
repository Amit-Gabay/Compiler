package IR;
import java.io.*;
import java.util.*;
import java.util.HashSet;
import java.util.Set;
import TEMP.*;

public class Analysis{

    public static void AnalysisMe(Function_Start_End_List func_boundaries) throws Exception{
        IRcommandList start = func_boundaries.start;
        IRcommandList end = func_boundaries.end;
        int size = func_boundaries.size;

        // make the CFG GRAPH

        CFG_Node[] array = new CFG_Node[size];
        IRcommandList tmp = start;
        IRcommandList tmp_new = start;
        IRcommandList tmp2 = null;
        boolean flag = false;
        int j = 0;
        int i = 0;
        while(tmp != end.tail){ // we think that tmp != end.tail is the same as i != size
            if(tmp.head != null && !(tmp.head instanceof IRcommand_Label)){
                array[i] = new CFG_Node(i , tmp.head);
                i++;
            }
            tmp = tmp.tail;
        }
        i = 0;
        while(tmp_new != end.tail){
            if(tmp_new.head != null && !(tmp_new.head instanceof IRcommand_Label)){
                if(!(tmp_new.head instanceof IRcommand_Jump_Label))  // if its not JUMP = if its a "normal" IRcommand or a conditional branch
                {
                    if (i + 1 < size) {
                        array[i].neighbours = new NodeList(array[i + 1]);
                    }
                }
                if(tmp_new.head instanceof IRcommand_Jump_Label || tmp_new.head instanceof IRcommand_Jump_If_Eq_To_Zero)
                {
                    tmp2 = tmp_new;
                    j = i;
                    flag = false;
                    while(tmp2!=end.tail){
                        if((flag) && (!(tmp2.head instanceof IRcommand_Label))){
                            if(array[i].neighbours != null){
                                array[i].neighbours.next = new NodeList(array[j]);
                            }
                            else{
                                array[i].neighbours = new NodeList(array[j]);
                            }
                            break;
                        }

                        if(tmp2.head instanceof IRcommand_Label){
                            if((tmp_new.head instanceof IRcommand_Jump_Label && ((IRcommand_Jump_Label) tmp_new.head).label_name.equals(((IRcommand_Label)tmp2.head).label_name)) || (tmp_new.head instanceof IRcommand_Jump_If_Eq_To_Zero && ((IRcommand_Jump_If_Eq_To_Zero) tmp_new.head).label_name.equals(((IRcommand_Label)tmp2.head).label_name))){
                                flag = true;
                            }
                        }
                        else{
                            j++;
                        }
                        tmp2 = tmp2.tail;
                    }
                }
                i++;
            }
            tmp_new = tmp_new.tail;
        }


        //creating the IN and OUT for each node
        List<Set<Integer>> In = new ArrayList<>();
        List<Set<Integer>> Out = new ArrayList<>();
        for(int w = 0 ; w < size; w++){
            In.add(new HashSet<>());
            Out.add(new HashSet<>());
        }
        // giving In and Out their values:
        NodeList successors = null;
        flag = true;
        while (flag) {
            flag = false;
            for (int h = size - 1; h >= 0; h--) {
                Set<Integer> old_val = new HashSet<>();
                old_val.addAll(Out.get(h));
                successors = array[h].neighbours;
                while (successors != null) {
                    Out.get(h).addAll(In.get(successors.data.node_num));
                    successors = successors.next;
                }
                Set<Integer> new_val = new HashSet<>();
                new_val.addAll(Out.get(h));
                if (!new_val.equals(old_val)){
                    flag = true;
                }
                Set<Integer> old_val2 = new HashSet<>();
                old_val2.addAll(In.get(h));
                In.get(h).addAll(Out.get(h));
                if (array[h].command.destination != -1) {
                    In.get(h).remove(array[h].command.destination);
                }
                In.get(h).addAll(array[h].command.DependsOn);
                Set<Integer> new_val2 = new HashSet<>();
                new_val2.addAll(In.get(h));
                if (!new_val2.equals(old_val2)){
                    flag = true;
                }
            }
        }
        // build interference graph

        Set<Integer> all_nodes = new HashSet<>();
        for (int b = 0 ; b < size; b++){
            all_nodes.addAll(Out.get(b));
        }

        Map<Integer, Set<Integer>> graph = new HashMap<Integer, Set<Integer>>();
        Map<Integer, Set<Integer>> graph_temp = new HashMap<Integer, Set<Integer>>();

        for (Integer cr : all_nodes){
            graph.put(cr, new HashSet<Integer>());
            graph_temp.put(cr, new HashSet<Integer>());
        }

        for (Set<Integer> s : Out){
            int sz = s.size();
            if (sz <= 1){
                continue;
            }
            for (Integer first : s){
                for (Integer second : s){
                    if (!first.equals(second)){
                        graph.get(first).add(second);
                        graph.get(second).add(first);
                        graph_temp.get(first).add(second);
                        graph_temp.get(second).add(first);
                    }
                }
            }
        }

        // graph coloring - chaitin's algorithm

        int k = 10;
        Stack<Integer> stack = new Stack<Integer>();
        Integer cur = exists_less_than_k(graph, k);
        while (cur != null){
            Set<Integer> cur_set = graph.get(cur);
            graph.remove(cur);
            for (Integer p : cur_set){
                graph.get(p).remove(cur);
            }
            stack.push(cur);
            cur = exists_less_than_k(graph, k);
        }
        if (!graph.isEmpty()){
            throw new Exception("Couldn't convert to 10 temporaries");
        }
        Map<Integer, Integer> res = new HashMap<Integer, Integer>();
        for (Integer q : stack) {
            res.put(q, -1);
        }

        while (!stack.empty()){
            Integer tp = stack.pop();
            Set<Integer> st = graph_temp.get(tp);
            Integer mx = -1;
            for (Integer a : st){
                if (res.get(a) > mx){
                    mx = res.get(a);
                }
            }
            res.replace(tp, mx+1);
        }

        IRcommandList ir = start;
        while (ir != end.tail){
            IRcommand irc = ir.head;
            if (irc != null && !(irc instanceof IRcommand_Label) && irc.all_temps.size() > 0){
                for (TEMP tm : irc.all_temps){
                    if (!res.containsKey(tm.serial)){
                        tm.real = 0;
                    }
                    else{
                        tm.real = res.get(tm.serial);
                    }
                }
            }
            ir = ir.tail;
        }
    }

    public static Integer exists_less_than_k(Map<Integer, Set<Integer>> graph, int k){
        for (Integer tmp : graph.keySet()){
            Set<Integer> s = graph.get(tmp);
            if (s.size() < k){
                return tmp;
            }
        }
        return null;
    }

    public static Function_Start_End_List initialize_list(){
        IRcommandList start = null;
        IRcommandList end = null;

        IRcommandList curr = IR.getInstance().tail;

        Function_Start_End_List result = new Function_Start_End_List(null,null , 0);
        Function_Start_End_List tmp = result;

        int size = 0;
        while(curr != null){
            size++;
            if ((curr.head != null) && (curr.head instanceof IRcommand_Label)){
                if (((IRcommand_Label) curr.head).label_type == labelEnum.FUNCSTART){
                    start = curr.tail; // start will  hold the command AFTER THE LABLE
                    size = 1;
                }
                size--;
            }
            if (curr.tail != null && curr.tail.head != null && curr.tail.head instanceof IRcommand_Label && ((IRcommand_Label) curr.tail.head).label_type == labelEnum.FUNCEND){
                end = curr;
            }

            if(start != null && end!=null)
            {
                tmp.next = new Function_Start_End_List(start,end,size);
                start = null;
                end = null;
                size = 0;
                tmp = tmp.next;
            }
            curr = curr.tail;
        }

        return result.next;
    }


}
