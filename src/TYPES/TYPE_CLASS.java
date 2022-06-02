package TYPES;

import java.util.Objects;

public class TYPE_CLASS extends TYPE
{
	/*********************************************************************/
	/* If this class does not extend a father class this should be null  */
	/*********************************************************************/
	public TYPE_CLASS father;

	/**************************************************/
	/* Gather up all data members in one place        */
	/* Note that data members coming from the AST are */
	/* packed together with the class methods         */
	/**************************************************/
	public TYPE_LIST data_members;

	/* Fields indexing counter */
	public int fields_counter;
	/* Methods indexing counter */
	public int methods_counter;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_CLASS(TYPE_CLASS father, String name, TYPE_LIST data_members)
	{
		this.name = name;
		this.typeEnum = TypeEnum.TYPE_CLASS;
		this.father = father;
		this.data_members = data_members;
		this.fields_counter = 1;
		this.methods_counter = 1;
	}

	public boolean isClass()
	{
		return true;
	}

	// left (this) := right (given);
	public boolean is_replacable(TYPE_CLASS given)
	{
		if (this.equals(given)) return true;

		TYPE_CLASS curr_class = given;
		while (curr_class != null)
		{
			if (this.equals(curr_class)) return true;

			curr_class = curr_class.father;
		}
		return false;
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof TYPE_CLASS)) return false;
		TYPE_CLASS given = (TYPE_CLASS) o;

		if (this.name.equals(given.name))
		{
			return true;
		}
		return false;
	}
}
