package AST;

import TYPES.*;
import SYMBOL_TABLE.*;

public abstract class AST_VAR extends AST_Node
{
    /* The AST annotations */
    public KindEnum var_kind;
    public int var_index;
    public String var_label;

    public TYPE SemantMe() throws SemanticException
    {
        return null;
    }
}
