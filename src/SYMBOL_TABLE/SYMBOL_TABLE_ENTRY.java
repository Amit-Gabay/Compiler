/***********/
/* PACKAGE */
/***********/
package SYMBOL_TABLE;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TYPES.*;

/**********************/
/* SYMBOL TABLE ENTRY */
/**********************/
public class SYMBOL_TABLE_ENTRY
{
	/*********/
	/* index */
	/*********/
	int index;
	
	/********/
	/* name */
	/********/
	public String name;

	/******************/
	/* TYPE value ... */
	/******************/
	public TYPE type;
	public boolean is_instancable; /* A flag which differentiates between instance-able types and non instance-able types */
	String scope_name;

	/*********************************************/
	/* prevtop and next symbol table entries ... */
	/*********************************************/
	public SYMBOL_TABLE_ENTRY prevtop;
	public SYMBOL_TABLE_ENTRY next;

	/****************************************************/
	/* The prevtop_index is just for debug purposes ... */
	/****************************************************/
	public int prevtop_index;

	public KindEnum var_kind;
	public int var_id;
	public String var_label;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public SYMBOL_TABLE_ENTRY(
		String name,
		TYPE type,
		int index,
		SYMBOL_TABLE_ENTRY next,
		SYMBOL_TABLE_ENTRY prevtop,
		int prevtop_index,
		boolean is_instancable,
		String scope_name)
	{
		this.index = index;
		this.name = name;
		this.type = type;
		this.next = next;
		this.prevtop = prevtop;
		this.prevtop_index = prevtop_index;
		this.is_instancable = is_instancable;
		this.scope_name = scope_name;
	}
}
