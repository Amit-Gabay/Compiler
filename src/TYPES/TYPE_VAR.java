package TYPES;

import SYMBOL_TABLE.KindEnum;

public class TYPE_VAR extends TYPE
{
    /* Keep the type of the variable (int, string, etc...) */
    public TYPE var_type;

    /* Keep the name of the variable (x, y, etc...) */
    public String var_name;

    /* AST Annotations */
    public KindEnum var_kind;
    public int var_index;
    public String var_label;

    /* AST Annotation for the field default value */
    public int default_int_value;
    public String default_string_label;

    public TYPE_VAR(TYPE var_type, String var_name)
    {
        this.typeEnum = var_type.typeEnum;
        this.name = var_type.name;
        this.var_type = var_type;
        this.var_name = var_name;
        this.default_int_value = 0;
        this.default_string_label = null;
    }
}
