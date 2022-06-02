/***********/
/* PACKAGE */
/***********/
package MIPS;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.PrintWriter;

import IR.IRcommand;
import TYPES.*;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;

public class MIPSGenerator
{
	/***********************/
	/* The file writer ... */
	/***********************/
	private PrintWriter fileWriter;

	private boolean is_in_code_seg = true;

	/***********************/
	/* The file writer ... */
	/***********************/
	public void print_string(TEMP str_register)
	{
		fileWriter.format("\tmove $a0, $t%d\n", str_register.real);
		fileWriter.format("\tli $v0, 4\n");
		fileWriter.format("\tsyscall\n");
	}

	public void print_int(TEMP int_register)
	{
		fileWriter.format("\tmove $a0, $t%d\n", int_register.real);
		fileWriter.format("\tli $v0, 1\n");
		fileWriter.format("\tsyscall\n");
		/* Add space */
		fileWriter.format("\tli $a0, 32\n");
		fileWriter.format("\tli $v0, 11\n");
		fileWriter.format("\tsyscall\n");
	}

	public void to_code_segment()
	{
		if (this.is_in_code_seg == false)
		{
			fileWriter.format(".text\n");
			this.is_in_code_seg = true;
		}
	}

	public void to_data_segment()
	{
		if (this.is_in_code_seg == true)
		{
			fileWriter.format(".data\n");
			this.is_in_code_seg = false;
		}
	}

	/*
	* An allocation routine for allocating global int variables in the DATA section.
	* Size of allocation is always 1.
	 */
	public void declare_global_var(String var_label, int default_int_val, String default_str_label)
	{
		/* Allocate in DATA segment */
		/* If we're allocating a string pointer */
		if (default_str_label != null)
		{
			to_data_segment();
			fileWriter.format("\t%s: .word %s\n", var_label, default_str_label);
		}

		/* Else, we're allocating NIL (=0) or int value */
		else
		{
			to_data_segment();
			fileWriter.format("\t%s: .word %d\n", var_label, default_int_val);
		}
	}

	/*
	 * Always allocate (the desired size + 1);
	 * If we're initializing a class object --> we're using: int alloc_size;
	 * If we're initializing an array --> we're using: TEMP alloc_size_register;
	 */
	public void heap_allocate(TEMP dst_register, int alloc_size, TEMP alloc_size_register, String class_name)
	{
		to_code_segment();
		/* Determine the allocation size (which given by the int or by the TEMP) */
		if (alloc_size_register != null) /* Then it's an array allocation */
		{
			/* $a0 stores the allocation size */
			fileWriter.format("\taddu $a0, $t%d, 1\n", alloc_size_register.real);
		}
		else /* Then it's a class object allocation */
		{
			/* $a0 stores the allocation size */
			fileWriter.format("\tli $a0, %d\n", alloc_size);
			fileWriter.format("\taddu $a0, $a0, 1\n");
		}
		fileWriter.format("\tsll $a0, $a0, 2\n");
		/* Allocate in heap */
		fileWriter.format("\tli $v0, 9\n");
		fileWriter.format("\tsyscall\n");

		/* If it's an array, write its size in the first cell */
		if (alloc_size_register != null)
		{
			fileWriter.format("\tsw $t%d, 0($v0)\n", alloc_size_register.real);
		}

		fileWriter.format("\tmove $t%d, $v0\n", dst_register.real);
	}

	/*
	* A function for allocating CONSTANT strings, in the DATA section.
	 */
	public void data_allocate_string(TEMP dst_register, String str_value, String str_label)
	{
		to_data_segment();
		fileWriter.format("%s: .asciiz %s\n", str_label, str_value);
		if (dst_register != null)
		{
			to_code_segment();
			fileWriter.format("\tla $t%d, %s\n", dst_register.real, str_label);
		}
	}

	public void str_cmp(TEMP dst_register, TEMP left_str, TEMP right_str)
	{
		to_code_segment();
		fileWriter.format("\tli $t%d, 1\n", dst_register.real);
		/* Load the addresses of the strings into $s0, $s1 */
		fileWriter.format("\tlw $s0, 0($t%d)\n", left_str.real);
		fileWriter.format("\tlw $s1, 0($t%d)\n", right_str.real);
		String start_loop_label = IRcommand.getFreshLabel("strcmp_loop_start");
		String end_loop_label = IRcommand.getFreshLabel("strcmp_loop_end");
		String assign_false_label = IRcommand.getFreshLabel("assign_false");

		/* Compare the strings' content in a loop */
		/* ----- Loop Start ----- */
		fileWriter.format("%s:\n", start_loop_label);
		fileWriter.format("\tlb $s2, 0($s0)\n");
		fileWriter.format("\tlb $s3, 0($s1)\n");
		/* If their current char doesn't match - assign false in dst_register */
		fileWriter.format("\tbne $s2, $s3, %s\n", assign_false_label);
		/* Else - check if we got to the end of the strings */
		fileWriter.format("\tbeq $s2, $zero, %s\n", end_loop_label);
		fileWriter.format("\taddu $s0, $s0, 1\n");
		fileWriter.format("\taddu $s1, $s1, 1\n");
		fileWriter.format("\tj %s\n", start_loop_label);
		/* ----- Loop End ----- */
		fileWriter.format("%s:\n", assign_false_label);
		fileWriter.format("\tli $t%d, 0\n", dst_register.real);
		fileWriter.format("%s:\n", end_loop_label);
	}

	/*
	* Computes a string length; the returned value is in $v0.
	* The string length doesn't include the null terminator ('\0').
	* Using: $s0, $s1, $v0.
	 */
	public void str_len(TEMP str)
	{
		to_code_segment();
		/* Iterate over the string and count its length in $v0 */
		fileWriter.format("\tmove $v0, $zero\n");
		fileWriter.format("\tmove $s0, $t%d\n", str.real);
		String start_loop_label = IRcommand.getFreshLabel("strlen_loop_start");
		String end_loop_label = IRcommand.getFreshLabel("strlen_loop_end");

		/* strlen loop start */
		fileWriter.format("%s:\n", start_loop_label);
		fileWriter.format("\tlb $s1, 0($s0)\n");
		fileWriter.format("\tbeq $s1, $zero, %s\n", end_loop_label);

		/* strlen loop body */
		fileWriter.format("\taddu $v0, $v0, 1\n"); /* Increment str_len by 1 */
		fileWriter.format("\taddu $s0, $s0, 1\n"); /* Increment the str pointer by 1 */
		fileWriter.format("\tj %s\n", start_loop_label);

		/* strlen loop end */
		fileWriter.format("%s:\n", end_loop_label);
	}

	/*
	* Copies a string from src_str to $a0, NOT including the null terminator ('\0').
	* The function forwards $a0 to the end of the copied string.
	* Using: $a0, $s1, $s2.
	 */
	public void str_cpy(TEMP src_str)
	{
		String start_loop_label = IRcommand.getFreshLabel("strcpy_loop_start");
		String end_loop_label = IRcommand.getFreshLabel("strcpy_loop_end");

		to_code_segment();
		/* strcpy loop start */
		fileWriter.format("\tmove $s1, $t%d\n", src_str.real);
		fileWriter.format("%s:\n", start_loop_label);
		fileWriter.format("\tlb $s2, 0($s1)\n");
		fileWriter.format("\tbeq $s2, $zero, %s\n", end_loop_label);

		/* strcpy loop body */
		fileWriter.format("\tsb $s2, 0($a0)\n");
		fileWriter.format("\taddu $a0, $a0, 1\n");
		fileWriter.format("\taddu $s1, $s1, 1\n");
		fileWriter.format("\tj %s\n", start_loop_label);

		/* strcpy loop end */
		fileWriter.format("%s:\n", end_loop_label);
	}

	/*
	* A function for strings concatenation.
	* Using: $a0, $s0, $s7, $s5, $v0.
	 */
	public void concat_strings(TEMP dst_register, TEMP left_str, TEMP right_str)
	{
		to_code_segment();
		/* Determine the string allocation size (left_str.length() + right_str.length() + 1, for the null terminator) */
		str_len(left_str); /* Computes left_str's length into $s7 */
		fileWriter.format("\tmove $s7, $v0\n");
		fileWriter.format("\tmove $a0, $s7\n");
		str_len(right_str); /* Computes right_str length into $s5 */
		fileWriter.format("\tmove $s5, $v0\n");
		fileWriter.format("\tadd $a0, $a0, $s5\n");
		fileWriter.format("\taddu $a0, $a0, 1\n"); /* Increment length by 1, for the null terminator */

		/* Call malloc */
		fileWriter.format("\tli $v0, 9\n");
		fileWriter.format("\tsyscall\n");
		fileWriter.format("\tmove $s0, $v0\n");

		/* Copy the first string */
		fileWriter.format("\tmove $a0, $s0\n");
		str_cpy(left_str);
		/* Copy the second string */
		str_cpy(right_str);
		/* Add the null terminator */
		fileWriter.format("\tsb $zero, 0($a0)\n");

		/* Assign the result to the destination register */
		fileWriter.format("\tmove $t%d, $v0\n", dst_register.real);
	}

	public void allocate_vt(String class_name, String[] method_labels)
	{
		int methods_num = method_labels.length;

		/* Allocate the Virtual Table in the DATA segment */
		to_data_segment();
		fileWriter.format("_%s_vt:\n", class_name);
		for (int i=0; i<methods_num; i++)
		{
			fileWriter.format("\t.word %s\n", method_labels[i]);
		}
	}

	public void fill_class_object(TEMP object_pointer, String class_name, TYPE_LIST class_members)
	{
		to_code_segment();
		fileWriter.format("\tmove $s0, $t%d\n", object_pointer.real);
		fileWriter.format("\tla $s1, _%s_vt\n", class_name);
		fileWriter.format("\tsw $s1, 0($s0)\n");
		fileWriter.format("\taddu $s0, $s0, 4\n");

		TYPE_LIST curr_member = class_members;
		int default_int_value;
		String default_string_label;
		while (curr_member != null)
		{
			/* If the current class member isn't a method */
			if (curr_member.head.typeEnum != TypeEnum.TYPE_FUNCTION)
			{
				if (curr_member.head.typeEnum == TypeEnum.TYPE_INT)
				{
					default_int_value = ((TYPE_VAR) curr_member.head).default_int_value;
					fileWriter.format("\tli $s2, %d\n", default_int_value);
					fileWriter.format("\tsw $s2, 0($s0)\n");
				}
				else if (curr_member.head.typeEnum == TypeEnum.TYPE_STRING && ((TYPE_VAR) curr_member.head).default_string_label != null)
				{
					default_string_label = ((TYPE_VAR) curr_member.head).default_string_label;
					fileWriter.format("\tla $s2, %s\n", default_string_label);
					fileWriter.format("\tsw $s2, 0($s0)\n");
				}
				else
				{
					fileWriter.format("\tli $s2, 0\n");
					fileWriter.format("\tsw $s2, 0($s0)\n");
				}
				fileWriter.format("\taddu $s0, $s0, 4\n");
			}

			curr_member = curr_member.tail;
		}
	}

	public void li(TEMP t, int value)
	{
		to_code_segment();
		int idx=t.real;
		fileWriter.format("\tli $t%d, %d\n",idx,value);
	}
	public void add(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 =oprnd1.real;
		int i2 =oprnd2.real;
		int dstidx=dst.real;

		to_code_segment();
		fileWriter.format("\tadd $t%d, $t%d, $t%d\n",dstidx,i1,i2);
	}
	public void sub(TEMP dst, TEMP operand1, TEMP operand2)
	{
		int serial1 = operand1.real;
		int serial2 = operand2.real;
		int dst_serial = dst.real;

		to_code_segment();
		fileWriter.format("\tsub $t%d, $t%d, $t%d\n",dst_serial,serial1,serial2);
	}
	public void mul(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 =oprnd1.real;
		int i2 =oprnd2.real;
		int dstidx=dst.real;

		to_code_segment();
		fileWriter.format("\tmul $t%d, $t%d, $t%d\n",dstidx,i1,i2);
	}
	public void div(TEMP dst,TEMP operand1,TEMP operand2)
	{
		int serial1 = operand1.real;
		int serial2 = operand2.real;
		int dst_serial = dst.real;

		to_code_segment();
		fileWriter.format("\tdiv $t%d, $t%d, $t%d\n",dst_serial,serial1,serial2);
	}
	public void label(String label_name)
	{
		to_code_segment();
		if (label_name.equals("main"))
		{
			fileWriter.format(".globl main\n");
			fileWriter.format("%s:\n", label_name);
		}
		else
		{
			fileWriter.format("%s:\n", label_name);
		}
	}	
	public void jump(String label_name)
	{
		to_code_segment();
		fileWriter.format("\tj %s\n", label_name);
	}	
	public void blt(TEMP oprnd1,TEMP oprnd2,String label)
	{
		to_code_segment();
		int i1 =oprnd1.real;
		int i2 =oprnd2.real;
		
		fileWriter.format("\tblt $t%d, $t%d, %s\n",i1,i2,label);
	}
	public void bge(TEMP oprnd1,TEMP oprnd2,String label)
	{
		to_code_segment();
		int i1 =oprnd1.real;
		int i2 =oprnd2.real;
		
		fileWriter.format("\tbge $t%d, $t%d, %s\n",i1,i2,label);
	}
	public void bne(TEMP oprnd1,TEMP oprnd2,String label)
	{
		to_code_segment();
		int i1 =oprnd1.real;
		int i2 =oprnd2.real;
		
		fileWriter.format("\tbne $t%d, $t%d, %s\n",i1,i2,label);
	}
	public void beq(TEMP oprnd1,TEMP oprnd2,String label)
	{
		to_code_segment();
		int i1 =oprnd1.real;
		int i2 =oprnd2.real;
		
		fileWriter.format("\tbeq $t%d, $t%d, %s\n",i1,i2,label);
	}
	public void beqz(TEMP condition, String label)
	{
		to_code_segment();
		int condition_reg_index = condition.real;
				
		fileWriter.format("\tbeq $t%d, $zero, %s\n", condition_reg_index, label);
	}

	public void before_call(TEMP_LIST parameters)
	{
		to_code_segment();
		/* If there are any parameters */
		if (parameters != null)
		{
			fileWriter.format("\tsubu $sp, $sp, %d\n", (parameters.length * 4));
			/* Insert the parameters to the stack in reverse order */
			int curr_index = 0;
			TEMP_LIST curr_param = parameters;
			while (curr_param != null)
			{
				fileWriter.format("\tsw $t%d, %d($sp)\n", curr_param.data.real, curr_index);
				curr_index += 4;
				curr_param = curr_param.next;
			}
		}
	}

	public void call_function(String function_label)
	{
		to_code_segment();
		/* Call the function */
		fileWriter.format("\tjal %s\n", function_label);
	}

	public void call_method(TEMP class_register, int method_id)
	{
		to_code_segment();
		/* Load to $s0 the Virtual Table pointer */
		fileWriter.format("\tlw $s0, 0($t%d)\n", class_register.real);
		/* Obtain the desired method address */
		int method_offset = (method_id - 1) * 4;
		fileWriter.format("\tlw $s1, %d($s0)\n", method_offset);

		/* Store the curren context in $s6 (The current class register, used for this.field_name access) */
		fileWriter.format("\tmove $s6, $t%d\n", class_register.real);

		/* Call the method */
		fileWriter.format("\tjalr $s1\n");
	}

	public void prologue(int local_var_num)
	{
		to_code_segment();
		/* Store the previous $ra */
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $ra, 0($sp)\n");
		/* Store the previous $fp */
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $fp, 0($sp)\n");
		/* Change the $fp to the new $fp */
		fileWriter.format("\tmove $fp, $sp\n");
		/* Back-up the registers */
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $t0, 0($sp)\n");
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $t1, 0($sp)\n");
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $t2, 0($sp)\n");
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $t3, 0($sp)\n");
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $t4, 0($sp)\n");
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $t5, 0($sp)\n");
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $t6, 0($sp)\n");
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $t7, 0($sp)\n");
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $t8, 0($sp)\n");
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $t9, 0($sp)\n");
		/* Allocate enough space in the stack for the local variables */
		fileWriter.format("\tsubu $sp, $sp, %d\n", (local_var_num * 4));
	}

	public void epilogue(String epilogue_label)
	{
		to_code_segment();
		fileWriter.format("%s:\n", epilogue_label);
		/* Move the $fp to the $sp */
		fileWriter.format("\tmove $sp, $fp\n");
		/* Restore the back-upped registers */
		fileWriter.format("\tlw $t0, -4($sp)\n");
		fileWriter.format("\tlw $t1, -8($sp)\n");
		fileWriter.format("\tlw $t2, -12($sp)\n");
		fileWriter.format("\tlw $t3, -16($sp)\n");
		fileWriter.format("\tlw $t4, -20($sp)\n");
		fileWriter.format("\tlw $t5, -24($sp)\n");
		fileWriter.format("\tlw $t6, -28($sp)\n");
		fileWriter.format("\tlw $t7, -32($sp)\n");
		fileWriter.format("\tlw $t8, -36($sp)\n");
		fileWriter.format("\tlw $t9, -40($sp)\n");
		/* Pop the previous $fp from the stack */
		fileWriter.format("\tlw $fp, 0($sp)\n");
		fileWriter.format("\taddu $sp, $sp, 4\n");
		/* Pop the previous $ra from the stack */
		fileWriter.format("\tlw $ra, 0($sp)\n");
		fileWriter.format("\taddu $sp, $sp, 4\n");
		/* Jump to the caller context (in code) */
		fileWriter.format("\tjr $ra\n");
	}

	public void after_call(TEMP result_register, TEMP_LIST parameters)
	{
		to_code_segment();
		if (parameters != null){
			fileWriter.format("\taddi $sp, $sp, %d\n", (parameters.length * 4));
		}
		if (result_register != null){
			fileWriter.format("\tmove $t%d, $v0\n", result_register.real);
		}
	}

	public void local_var_set(int local_var_id, TEMP value_register, int constant_int, String constant_str_label)
	{
		to_code_segment();
		int var_offset = (-1) * (local_var_id + 10) * 4;
		if (value_register != null)
		{
			fileWriter.format("\tsw $t%d, %d($fp)\n", value_register.real, var_offset);
		}
		else if (constant_str_label != null)
		{
			fileWriter.format("\tla $s0, %s\n", constant_str_label);
			fileWriter.format("\tsw $s0, %d($fp)\n", var_offset);
		}
		else
		{
			fileWriter.format("\tli $s0, %d\n", constant_int);
			fileWriter.format("\tsw $s0, %d($fp)\n", var_offset);
		}
	}

	public void local_var_access(TEMP dst_register, int local_var_id)
	{
		to_code_segment();
		int var_offset = (-1) * (local_var_id + 10) * 4;
		fileWriter.format("\tlw $t%d, %d($fp)\n", dst_register.real, var_offset);
	}

	public void global_var_set(String var_label, TEMP value_register, int constant_int, String constant_str_label)
	{
		to_code_segment();
		fileWriter.format("\tla $s0, %s\n", var_label);

		if (value_register != null)
		{
			fileWriter.format("\tsw $t%d, 0($s0)\n", value_register.real);
		}
		else if (constant_str_label != null)
		{
			fileWriter.format("\tla $s1, %s\n", constant_str_label);
			fileWriter.format("\tsw $s1, 0($s0)\n");
		}
		else
		{
			fileWriter.format("\tli $s1, %d\n", constant_int);
			fileWriter.format("\tsw $s1, 0($s0)\n");
		}
	}

	public void global_var_access(TEMP dst_register, String var_label)
	{
		to_code_segment();
		fileWriter.format("\tla $s0, %s\n", var_label);
		fileWriter.format("\tlw $t%d, 0($s0)\n", dst_register.real);
	}

	public void param_set(int param_id, TEMP value_register, int constant_int, String constant_str_label)
	{
		to_code_segment();
		int param_offset = (param_id + 1) * 4;
		if (value_register != null)
		{
			fileWriter.format("\tsw $t%d, %d($fp)\n", value_register.real, param_offset);
		}
		else if (constant_str_label != null)
		{
			fileWriter.format("\tla $s0, %s\n", constant_str_label);
			fileWriter.format("\tsw $s0, %d($fp)\n", param_offset);
		}
		else
		{
			fileWriter.format("\tli $s0, %d\n", constant_int);
			fileWriter.format("\tsw $s0, %d($fp)\n", param_offset);
		}
	}

	public void param_access(TEMP dst_register, int param_id)
	{
		to_code_segment();
		int param_offset = (param_id + 1) * 4;
		fileWriter.format("\tlw $t%d, %d($fp)\n", dst_register.real, param_offset);
	}

	public void array_set(TEMP array_register, TEMP subscript_register, TEMP value_register, int constant_int, String constant_str_label)
	{
		to_code_segment();
		/* Add 1 to the subscript (the first cell is the array size) */
		fileWriter.format("\taddu $t%d, $t%d, 1\n", subscript_register.real, subscript_register.real);
		/* Compute the offset, by multiplying the subscript by 4 */
		fileWriter.format("\tsll $s0, $t%d, 2\n", subscript_register.real);
		/* Compute the absolute address of the array index */
		fileWriter.format("\tadd $s0, $s0, $t%d\n", array_register.real);
		/* Write the assigned expression into the desired array cell */
		if (value_register != null)
		{
			fileWriter.format("\tsw $t%d, 0($s0)\n", value_register.real);
		}
		else if (constant_str_label != null)
		{
			fileWriter.format("\tla $s1, %s\n", constant_str_label);
			fileWriter.format("\tsw $s1, 0($s0)\n");
		}
		else
		{
			fileWriter.format("\tli $s1, %d\n", constant_int);
			fileWriter.format("\tsw $s1, 0($s0)\n");
		}
	}

	public void array_access(TEMP dst_register, TEMP array_register, TEMP subscript_register)
	{
		to_code_segment();
		/* Add 1 to the subscript (the first cell is the array size) */
		fileWriter.format("\taddu $t%d, $t%d, 1\n", subscript_register.real, subscript_register.real);
		/* Compute the offset, by multiplying the subscript by 4 */
		fileWriter.format("\tsll $s0, $t%d, 2\n", subscript_register.real);
		/* Compute the absolute address of the array index */
		fileWriter.format("\tadd $s0, $s0, $t%d\n", array_register.real);
		fileWriter.format("\tlw $t%d, 0($s0)\n", dst_register.real);
	}

	public void field_set(TEMP class_register, int field_index, TEMP value_register, int constant_int, String constant_str_label)
	{
		to_code_segment();
		/* Compute the offset, by multiplying the (field index + 1) by 4 */
		fileWriter.format("\tli $s0, %d\n", field_index);
		fileWriter.format("\tsll $s0, $s0, 2\n");
		/* Compute the absolute address of the class field */
		if (class_register != null)
		{
			fileWriter.format("\tadd $s1, $t%d, $s0\n", class_register.real);
		}
		else
		{
			fileWriter.format("\tadd $s1, $s6, $s0\n");
		}
		/* Store the assigned expression in the desired class field */
		if (value_register != null)
		{
			fileWriter.format("\tsw $t%d, 0($s1)\n", value_register.real);
		}
		else if (constant_str_label != null)
		{
			fileWriter.format("\tla $s2, %s\n", constant_str_label);
			fileWriter.format("\tsw $s2, 0($s1)\n");
		}
		else
		{
			fileWriter.format("\tli $s2, %d\n", constant_int);
			fileWriter.format("\tsw $s2, 0($s1)\n");
		}
	}

	public void field_access(TEMP dst_register, TEMP class_register, int field_index)
	{
		to_code_segment();
		/* Compute the offset, by multiplying the (field index + 1) by 4 */
		fileWriter.format("\tli $s0, %d\n", field_index);
		fileWriter.format("\tsll $s0, $s0, 2\n");
		/* Compute the absolute address of the class field */
		if (class_register != null)
		{
			fileWriter.format("\tadd $s1, $t%d, $s0\n", class_register.real);
		}
		else /* the class object pointer is in $s6 */
		{
			fileWriter.format("\tadd $s1, $s6, $s0\n");
		}
		fileWriter.format("\tlw $t%d, 0($s1)\n", dst_register.real);
	}

	public void assign_nil(TEMP dst_register)
	{
		to_code_segment();
		fileWriter.format("\tli $t%d, 0\n", dst_register.real);
	}

	public void assign_int(TEMP dst_register, int value)
	{
		to_code_segment();
		fileWriter.format("\tli $t%d, %d\n", dst_register.real, value);
	}

	public void return_exp(TEMP exp_register)
	{
		to_code_segment();
		fileWriter.format("\tmove $v0, $t%d\n", exp_register.real);
	}

	public void fix_overflow(TEMP value_register)
	{
		to_code_segment();
		int upper_bound = 32767;  /* +2^15 - 1 */
		int lower_bound = -32768; /* -2^15 + 1 */
		String upper_overflow_label = IRcommand.getFreshLabel("upper_overflow");
		String lower_overflow_label = IRcommand.getFreshLabel("lower_overflow");
		String fix_end_label = IRcommand.getFreshLabel("overflow_fix_end");

		fileWriter.format("\tli $s0, %d\n", lower_bound);
		fileWriter.format("\tli $s1, %d\n", upper_bound);
		fileWriter.format("\tbgt $t%d, $s1, %s\n", value_register.real, upper_overflow_label);
		fileWriter.format("\tblt $t%d, $s0, %s\n", value_register.real, lower_overflow_label);
		fileWriter.format("\tj %s\n", fix_end_label);
		/* Handle upper overflow */
		fileWriter.format("%s:\n", upper_overflow_label);
		fileWriter.format("\tli $t%d, %d\n", value_register.real, upper_bound);
		fileWriter.format("\tj %s\n", fix_end_label);
		/* Handle lower overflow */
		fileWriter.format("%s:\n", lower_overflow_label);
		fileWriter.format("\tli $t%d, %d\n", value_register.real, lower_bound);
		/* Overflow fix end */
		fileWriter.format("%s:\n", fix_end_label);
	}

	public void check_access_violation(TEMP array_register, TEMP index_register)
	{
		to_code_segment();
		String handle_violation = IRcommand.getFreshLabel("access_violation");
		String check_end = IRcommand.getFreshLabel("violation_check_end");
		/* Load the array size into $s0 */
		fileWriter.format("\tlw $s0, 0($t%d)\n", array_register.real);
		fileWriter.format("\tblt $t%d, $zero, %s\n", index_register.real, handle_violation);
		fileWriter.format("\tbge $t%d, $s0, %s\n", index_register.real, handle_violation);
		fileWriter.format("\tj %s\n", check_end);
		/* Handle access violation */
		fileWriter.format("%s:\n", handle_violation);
		/* Print "Access Violation" */
		fileWriter.format("\tla $a0, %s\n", "string_access_violation");
		fileWriter.format("\tli $v0, 4\n");
		fileWriter.format("\tsyscall\n");
		/* Exit */
		fileWriter.format("\tli $v0, 10\n");
		fileWriter.format("\tsyscall\n");
		/* access violation check end */
		fileWriter.format("%s:\n", check_end);
	}

	public void check_division_by_zero(TEMP right_operand)
	{
		to_code_segment();
		String check_end_label = IRcommand.getFreshLabel("zero_division_check_end");

		fileWriter.format("\tbne $t%d, $zero, %s\n", right_operand.real, check_end_label);
		/* Handle Zero Division */
		/* Print "Illegal Division By Zero" */
		fileWriter.format("\tla $a0, %s\n", "string_illegal_div_by_0");
		fileWriter.format("\tli $v0, 4\n");
		fileWriter.format("\tsyscall\n");
		/* Exit */
		fileWriter.format("\tli $v0, 10\n");
		fileWriter.format("\tsyscall\n");
		fileWriter.format("%s:\n", check_end_label);
	}

	public void check_invalid_dereference(TEMP class_register)
	{
		to_code_segment();
		String check_end_label = IRcommand.getFreshLabel("invalid_dereference_check_end");

		if (class_register != null)
		{
			fileWriter.format("\tbne $t%d, $zero, %s\n", class_register.real, check_end_label);
		}
		else
		{
			fileWriter.format("\tbne $s6, $zero, %s\n", check_end_label);
		}
		/* Handle Invalid Dereference */
		/* Print "Invalid Pointer Dereference" */
		fileWriter.format("\tla $a0, %s\n", "string_invalid_ptr_dref");
		fileWriter.format("\tli $v0, 4\n");
		fileWriter.format("\tsyscall\n");
		/* Exit */
		fileWriter.format("\tli $v0, 10\n");
		fileWriter.format("\tsyscall\n");
		fileWriter.format("%s:\n", check_end_label);
	}

	public void exit()
	{
		to_code_segment();
		fileWriter.format("\tli $v0, 10\n");
		fileWriter.format("\tsyscall\n");
		fileWriter.close();
	}
	
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static MIPSGenerator instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected MIPSGenerator() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static MIPSGenerator getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new MIPSGenerator();

			try
			{
				/*********************************************************************************/
				/* [1] Open the MIPS text file and write data section with error message strings */
				/*********************************************************************************/
				String dirname="./output/";
				String filename= "MIPS.txt";

				/***************************************/
				/* [2] Open MIPS text file for writing */
				/***************************************/
				instance.fileWriter = new PrintWriter(dirname+filename);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			/*****************************************************/
			/* [3] Print data section with error message strings */
			/*****************************************************/
			instance.to_data_segment();
			instance.fileWriter.print("string_access_violation: .asciiz \"Access Violation\"\n");
			instance.fileWriter.print("string_illegal_div_by_0: .asciiz \"Division By Zero\"\n");
			instance.fileWriter.print("string_invalid_ptr_dref: .asciiz \"Invalid Pointer Dereference\"\n");
		}
		return instance;
	}
}
