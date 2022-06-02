package TYPES;

import java.util.Objects;

public class TYPE_LIST extends TYPE
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public TYPE head;
	public String dataMemberName = null;
	public TYPE_LIST tail;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public TYPE_LIST(TYPE head, TYPE_LIST tail, String dataMemberName)
	{
		this.typeEnum = TypeEnum.TYPE_LIST;
		this.dataMemberName = dataMemberName;
		this.head = head;
		this.tail = tail;
	}

	public TYPE_LIST concat(TYPE_LIST right_list)
	{
		TYPE_LIST left_list = clone(this);
		TYPE_LIST nodes_iterator = left_list;
		while (nodes_iterator.tail != null)
		{
			nodes_iterator = nodes_iterator.tail;
		}
		nodes_iterator.tail = right_list;

		return left_list;
	}

	public TYPE_LIST clone(TYPE_LIST original_list)
	{
		TYPE_LIST cloned_list = new TYPE_LIST(original_list.head, null, original_list.dataMemberName);
		TYPE_LIST nodes_iterator = cloned_list;
		while (original_list.tail != null)
		{
			original_list = original_list.tail;
			nodes_iterator.tail = new TYPE_LIST(original_list.head, null, original_list.dataMemberName);
			nodes_iterator = nodes_iterator.tail;
		}

		return cloned_list;
	}
}
