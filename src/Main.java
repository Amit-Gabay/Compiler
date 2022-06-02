import java.io.*;
import java.io.PrintWriter;
import java_cup.runtime.Symbol;
import AST.*;
import IR.*;
import MIPS.*;

public class Main
{
	static public void main(String argv[])
	{
		Lexer l = null;
		Parser p = null;
		Symbol s;
		AST_PROGRAM AST;
		FileReader file_reader = null;
		PrintWriter file_writer = null;
		String inputFilename = argv[0];
		String outputFilename = argv[1];

		try
		{
			/************/
			/* [1] Initialize a file reader */
			/************/
			file_reader = new FileReader(inputFilename);

			/************/
			/* [2] Initialize a file writer */
			/************/
			file_writer = new PrintWriter(outputFilename);

			/**********/
			/* [3] Initialize a new lexer */
			/**********/
			l = new Lexer(file_reader);

			/***********/
			/* [4] Initialize a new parser */
			/***********/
			p = new Parser(l);

			/*************/
			/* [5] 3 ... 2 ... 1 ... Parse !!! */
			/*************/
			AST = (AST_PROGRAM) p.parse().value;

			/*********/
			/* [6] Print the AST ... */
			/*********/
			AST.PrintMe();

			/**********/
			/* [7] Semant the AST ... */
			/**********/
			AST.SemantMe();

			/********/
			/* [8] IR the AST ... */
			/********/
			AST.IRme();
			Function_Start_End_List func_commands_boundaries = Analysis.initialize_list();
			while(func_commands_boundaries!=null){
				Analysis.AnalysisMe(func_commands_boundaries);
				func_commands_boundaries = func_commands_boundaries.next;
			}

			/*********/
			/* [9] MIPS the IR ... */
			/*********/
			IR.getInstance().MIPSme();

			/**************/
			/* [10] Finalize AST GRAPHIZ DOT file */
			/**************/
			AST_GRAPHVIZ.getInstance().finalizeFile();

			/*********/
			/* [12] Close output file */
			/*********/
			file_writer.close();
		}
		catch (Error e)
		{
			if (p != null && file_writer != null && l != null)
			{
				/* syntax error */
				if (p.error_code == 1)
				{
					file_writer.print("ERROR("+Integer.toString(l.getLine())+")");
				}
				/* lexical error */
				else if(p.error_code == 2)
				{
					file_writer.print("ERROR");
				}
				file_writer.close();
			}
		}

		catch (SemanticException e)
		{
			file_writer.print("ERROR("+e.line+")");
			file_writer.close();
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}