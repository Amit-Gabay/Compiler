/***********/
/* PACKAGE */
/***********/
package SYMBOL_TABLE;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.PrintWriter;
import java.lang.Math;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TYPES.*;

/****************/
/* SYMBOL TABLE */
/****************/
public class SYMBOL_TABLE
{
	private int hashArraySize = 100;
	
	/**********************************************/
	/* The actual symbol table data structure ... */
	/**********************************************/
	private SYMBOL_TABLE_ENTRY[] table = new SYMBOL_TABLE_ENTRY[hashArraySize];
	private SYMBOL_TABLE_ENTRY top;
	private int top_index = 0;
	public TYPE_CLASS current_class = null;
	public TYPE_CLASS current_parent_class = null;
	public TYPE_FUNCTION current_function = null;
	public String current_scope = null;
	public int function_variable_counter = 0;
	public int class_field_counter = 0;
	
	/*********************************************/
	/* Hash function for exposition purposes ... */
	/*********************************************/
	private int hash(String s)
	{
		return (Math.abs(s.hashCode()) % hashArraySize);
	}

	/****************************************************************************/
	/* Enter a variable, function, class type or array type to the symbol table */
	/****************************************************************************/
	public void enter(String name, TYPE t, boolean is_instancable, String scope_name)
	{
		/*************************************************/
		/* [1] Compute the hash value for this new entry */
		/*************************************************/
		int hashValue = hash(name);
	
		/**************************************************************************/
		/* [3] Prepare a new symbol table entry with name, type, next and prevtop */
		/**************************************************************************/
		SYMBOL_TABLE_ENTRY entry = new SYMBOL_TABLE_ENTRY(name, t, hashValue, table[hashValue], top, top_index++, is_instancable, scope_name);

		/**********************************************/
		/* [4] Update the top of the symbol table ... */
		/**********************************************/
		top = entry;
		
		/****************************************/
		/* [5] Enter the new entry to the table */
		/****************************************/
		table[hashValue] = entry;
		
		/**************************/
		/* [6] Print Symbol Table */
		/**************************/
		PrintMe();
	}

	/*
	 * Look-up function for finding in the Symbol Table a type by its name.
	 */
	public TYPE find(String name)
	{
		SYMBOL_TABLE_ENTRY entry = table[hash(name)];
		String found_scope = null;

		if (current_class == null)
		{
			return findOriginal(name);
		}

		/* Find first occurance of name */
		while (entry != null)
		{
			if (entry.name.equals(name))
			{
				found_scope = entry.scope_name;
				break;
			}
			entry = entry.next;
		}
		if (found_scope == null || found_scope.equals("global"))
		{
			/* Search in the parents */
			TYPE_CLASS curr_parent = current_parent_class;
			while (curr_parent != null)
			{
				TYPE_LIST member = curr_parent.data_members;
				while (member != null)
				{
					if (member.dataMemberName.equals(name)) return member.head;

					member = member.tail;
				}

				curr_parent = curr_parent.father;
			}
			if (found_scope == null) return null;
			else
			{
				return entry.type;
			}
		}

		else
		{
			/* Found in a scope which is not global */
			return entry.type;
		}
	}

	/*
	* Primitive find function: makes a look-up in the Symbol Table, outside a class scope.
	 */
	public TYPE findOriginal(String name)
	{
		SYMBOL_TABLE_ENTRY entry;

		for (entry = table[hash(name)]; entry != null; entry = entry.next)
		{
			if (name.equals(entry.name))
			{
				return entry.type;
			}
		}
		
		return null;
	}


	public boolean isInstancableOriginal(String name)
	{
		SYMBOL_TABLE_ENTRY entry;

		for (entry = table[hash(name)]; entry != null; entry = entry.next)
		{
			if (name.equals(entry.name))
			{
				return entry.is_instancable;
			}
		}

		return false;
	}


	public boolean isInstancable(String name)
	{
		SYMBOL_TABLE_ENTRY entry = table[hash(name)];
		String found_scope = null;

		if (current_class == null)
		{
			return isInstancableOriginal(name);
		}

		/* Find first occurance of name */
		while (entry != null)
		{
			if (entry.name.equals(name))
			{
				found_scope = entry.scope_name;
				break;
			}
			entry = entry.next;
		}
		if (found_scope == null || found_scope.equals("global"))
		{
			/* Search in the parents */
			TYPE_CLASS curr_parent = current_parent_class;
			while (curr_parent != null)
			{
				TYPE_LIST member = curr_parent.data_members;
				while (member != null)
				{
					if (member.dataMemberName.equals(name)) return false;

					member = member.tail;
				}

				curr_parent = curr_parent.father;
			}
			if (found_scope == null) return false;
			else
			{
				return entry.is_instancable;
			}
		}

		else
		{
			/* Found in a scope which is not global */
			return entry.is_instancable;
		}
	}

	public boolean isTypeClass(String name)
	{
		boolean is_instacable = this.isInstancable(name);
		if (!is_instacable){
			return false;
		}
		if (this.find(name).typeEnum != TypeEnum.TYPE_CLASS) {
			return false;
		}
		return true;
	}

	public TYPE findCurrentScopeDeclaration()
	{
		SYMBOL_TABLE_ENTRY entry = top;

		if (entry.type.typeEnum == TypeEnum.TYPE_FOR_SCOPE_BOUNDARIES)
		{
			return null;
		}

		while (entry.prevtop.type.typeEnum != TypeEnum.TYPE_FOR_SCOPE_BOUNDARIES)
		{
			entry = entry.prevtop;
		}

		return entry.type;
	}

	public TYPE findInCurrentScope(String name)
	{
		SYMBOL_TABLE_ENTRY entry;

		for (entry = top; (entry != null && !(entry.name.equals("SCOPE-BOUNDARY"))); entry = entry.prevtop)
		{
			if (name.equals(entry.name))
			{
				return entry.type;
			}
		}

		return null;
	}

	public String findCurrentScope() {
		SYMBOL_TABLE_ENTRY entry;

		for (entry = table[hash("SCOPE-BOUNDARY")]; entry != null; entry = entry.next)
		{
			if (entry.type.typeEnum == TypeEnum.TYPE_FOR_SCOPE_BOUNDARIES)
			{
				return entry.type.name;
			}
		}

		return null;
	}


	/***************************************************************************/
	/* begine scope = Enter the <SCOPE-BOUNDARY> element to the data structure */
	/***************************************************************************/
	public void beginScope(String x)
	{
		/************************************************************************/
		/* Though <SCOPE-BOUNDARY> entries are present inside the symbol table, */
		/* they are not really types. In order to be ablt to debug print them,  */
		/* a special TYPE_FOR_SCOPE_BOUNDARIES was developed for them. This     */
		/* class only contain their type name which is the bottom sign: _|_     */
		/************************************************************************/
		enter(
			"SCOPE-BOUNDARY",
			new TYPE_FOR_SCOPE_BOUNDARIES(x), false, null);
		current_scope = x;

		/*********************************************/
		/* Print the symbol table after every change */
		/*********************************************/
		PrintMe();
	}

	/********************************************************************************/
	/* end scope = Keep popping elements out of the data structure,                 */
	/* from most recent element entered, until a <NEW-SCOPE> element is encountered */
	/********************************************************************************/
	public void endScope()
	{
		/**************************************************************************/
		/* Pop elements from the symbol table stack until a SCOPE-BOUNDARY is hit */		
		/**************************************************************************/
		while (!(top.name.equals("SCOPE-BOUNDARY")))
		{
			table[top.index] = top.next;
			top_index = top_index-1;
			top = top.prevtop;
		}
		/**************************************/
		/* Pop the SCOPE-BOUNDARY sign itself */		
		/**************************************/
		table[top.index] = top.next;
		top_index = top_index-1;
		top = top.prevtop;

		SYMBOL_TABLE_ENTRY curr_entry = table[hash("SCOPE-BOUNDARY")];
		while (curr_entry != null && !curr_entry.name.equals("SCOPE-BOUNDARY"))
		{
			curr_entry = curr_entry.next;
		}
		if (curr_entry == null)
		{
			current_scope = null;
		}
		else
		{
			current_scope = curr_entry.type.name;
		}

		/*********************************************/
		/* Print the symbol table after every change */		
		/*********************************************/
		PrintMe();
	}
	
	public static int n=0;
	
	public void PrintMe()
	{
		int i=0;
		int j=0;
		String dirname="./output/";
		String filename=String.format("SYMBOL_TABLE_%d_IN_GRAPHVIZ_DOT_FORMAT.txt",n++);

		try
		{
			/*******************************************/
			/* [1] Open Graphviz text file for writing */
			/*******************************************/
			PrintWriter fileWriter = new PrintWriter(dirname+filename);

			/*********************************/
			/* [2] Write Graphviz dot prolog */
			/*********************************/
			fileWriter.print("digraph structs {\n");
			fileWriter.print("rankdir = LR\n");
			fileWriter.print("node [shape=record];\n");

			/*******************************/
			/* [3] Write Hash Table Itself */
			/*******************************/
			fileWriter.print("hashTable [label=\"");
			for (i=0;i<hashArraySize-1;i++) { fileWriter.format("<f%d>\n%d\n|",i,i); }
			fileWriter.format("<f%d>\n%d\n\"];\n",hashArraySize-1,hashArraySize-1);
		
			/****************************************************************************/
			/* [4] Loop over hash table array and print all linked lists per array cell */
			/****************************************************************************/
			for (i=0;i<hashArraySize;i++)
			{
				if (table[i] != null)
				{
					/*****************************************************/
					/* [4a] Print hash table array[i] -> entry(i,0) edge */
					/*****************************************************/
					fileWriter.format("hashTable:f%d -> node_%d_0:f0;\n",i,i);
				}
				j=0;
				for (SYMBOL_TABLE_ENTRY it=table[i];it!=null;it=it.next)
				{
					/*******************************/
					/* [4b] Print entry(i,it) node */
					/*******************************/
					fileWriter.format("node_%d_%d ",i,j);
					fileWriter.format("[label=\"<f0>%s|<f1>%s|<f2>prevtop=%d|<f3>next\"];\n",
						it.name,
						it.type.name,
						it.prevtop_index);

					if (it.next != null)
					{
						/***************************************************/
						/* [4c] Print entry(i,it) -> entry(i,it.next) edge */
						/***************************************************/
						fileWriter.format(
							"node_%d_%d -> node_%d_%d [style=invis,weight=10];\n",
							i,j,i,j+1);
						fileWriter.format(
							"node_%d_%d:f3 -> node_%d_%d:f0;\n",
							i,j,i,j+1);
					}
					j++;
				}
			}
			fileWriter.print("}\n");
			fileWriter.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static SYMBOL_TABLE instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected SYMBOL_TABLE() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static SYMBOL_TABLE getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new SYMBOL_TABLE();

			instance.beginScope("global");

			/*****************************************/
			/* [1] Enter primitive types int, string */
			/*****************************************/
			instance.enter("int",   TYPE_INT.getInstance(), true, instance.current_scope);
			instance.enter("string",TYPE_STRING.getInstance(), true, instance.current_scope);

			/*************************************/
			/* [2] How should we handle void ??? */
			/*************************************/

			instance.enter("void",TYPE_VOID.getInstance(), true, instance.current_scope);

			/***************************************/
			/* [3] Enter library function PrintInt */
			/***************************************/
			instance.enter("PrintInt", new TYPE_FUNCTION(TYPE_VOID.getInstance(), "PrintInt", new TYPE_LIST(TYPE_INT.getInstance(), null, null), null), false, instance.current_scope);
			instance.enter("PrintString", new TYPE_FUNCTION(TYPE_VOID.getInstance(), "PrintString", new TYPE_LIST(TYPE_STRING.getInstance(), null, null), null), false, instance.current_scope);
			instance.enter("PrintTrace", new TYPE_FUNCTION(TYPE_VOID.getInstance(), "PrintTrace", null, null), false, instance.current_scope);
			// in the printtrace funciton i am not sure about the parameters
		}
		return instance;
	}
}
