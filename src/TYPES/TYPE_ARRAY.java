package TYPES;

import AST.SemanticException;

public class TYPE_ARRAY extends TYPE {
    public TYPE arrayMembersType;

    public TYPE_ARRAY(String name, TYPE arrayMembersType) {
        this.name = name;
        this.typeEnum = TypeEnum.TYPE_ARRAY;
        this.arrayMembersType = arrayMembersType;
    }

    public boolean isArray()
    {
        return true;
    }

    // left (this) := right (given);
    public boolean is_replacable(TYPE_ARRAY given)
    {
        if (this.arrayMembersType.typeEnum == given.arrayMembersType.typeEnum)
        {
            /* Make sure that the array type names are equal */
            if (!this.name.equals(given.name)) return false;
            return true;
        }
        return false;
    }

    /* left (this) := new type[int]; */
    public boolean is_replacable_new(TYPE_ARRAY given)
    {
        if (this.arrayMembersType.typeEnum != given.arrayMembersType.typeEnum) return false;
        if (this.arrayMembersType.typeEnum == TypeEnum.TYPE_CLASS)
        {
            TYPE_CLASS this_class = (TYPE_CLASS) this.arrayMembersType;
            TYPE_CLASS given_class = (TYPE_CLASS) given.arrayMembersType;
            return (this_class.is_replacable(given_class));
        }
        if (this.arrayMembersType.typeEnum == TypeEnum.TYPE_ARRAY)
        {
            TYPE_ARRAY this_array = (TYPE_ARRAY) this.arrayMembersType;
            TYPE_ARRAY given_array = (TYPE_ARRAY) given.arrayMembersType;
            return (this_array.is_replacable(given_array));
        }
        return true;
    }

    @Override
    public boolean equals(Object given)
    {
        if (!(given instanceof TYPE_ARRAY)) return false;
        TYPE_ARRAY given_type = (TYPE_ARRAY) given;
        if (this.typeEnum == given_type.typeEnum) /* Make sure that they are TYPE_ARRAY */
        {
            if (this.arrayMembersType.typeEnum == given_type.arrayMembersType.typeEnum)
            {
                /* If they are both arrays of arrays: */
                if (this.arrayMembersType.typeEnum == TypeEnum.TYPE_ARRAY)
                {
                    return (this.arrayMembersType.name.equals(given_type.arrayMembersType.name));
                }
                /* If they are both arrays of classes: */
                else if (this.arrayMembersType.typeEnum == TypeEnum.TYPE_CLASS)
                {
                    TYPE_CLASS this_class = (TYPE_CLASS) this.arrayMembersType;
                    TYPE_CLASS given_class = (TYPE_CLASS) given_type.arrayMembersType;
                    return (this_class.equals(given_class));
                }
                else
                {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
