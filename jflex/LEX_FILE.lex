/***************************/
/* FILE NAME: LEX_FILE.lex */
/***************************/

/*************/
/* USER CODE */
/*************/
import java_cup.runtime.*;
import javafx.util.Pair;

/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/

%%

/************************************/
/* OPTIONS AND DECLARATIONS SECTION */
/************************************/
   
/*****************************************************/ 
/* Lexer is the name of the class JFlex will create. */
/* The code will be written to the file Lexer.java.  */
/*****************************************************/ 
%class Lexer

/********************************************************************/
/* The current line number can be accessed with the variable yyline */
/* and the current column number with the variable yycolumn.        */
/********************************************************************/
%line
%column

/*******************************************************************************/
/* Note that this has to be the EXACT same name of the class the CUP generates */
/*******************************************************************************/
%cupsym TokenNames

/******************************************************************/
/* CUP compatibility mode interfaces with a CUP generated parser. */
/******************************************************************/
%cup

/****************/
/* DECLARATIONS */
/****************/
/*****************************************************************************/   
/* Code between %{ and %}, both of which must be at the beginning of a line, */
/* will be copied verbatim (letter to letter) into the Lexer class code.     */
/* Here you declare member variables and functions that are used inside the  */
/* scanner actions.                                                          */  
/*****************************************************************************/   
%{
	/*********************************************************************************/
	/* Create a new java_cup.runtime.Symbol with information about the current token */
	/*********************************************************************************/
	private Symbol symbol(int type)               {return new Symbol(type, yyline, yycolumn);}
	private Symbol symbol(int type, Object value) {return new Symbol(type, yyline, yycolumn, value);}

	/*******************************************/
	/* Enable line number extraction from main */
	/*******************************************/
	public int getLine() { return yyline + 1; } 

	/**********************************************/
	/* Enable token position extraction from main */
	/**********************************************/
	public int getTokenStartPosition() { return yycolumn + 1; } 
%}

/***********************/
/* MACRO DECALARATIONS */
/***********************/
LineTerminator	= \r|\n|\r\n
NonLineTerminator = [ \t\f]
WhiteSpace	= {LineTerminator} | {NonLineTerminator}
COMMENT_STAR	= ([A-Za-z0-9]|{WhiteSpace}|[(){}\[\]?!*+-\.;])
COMMENT_SLASH	= ([A-Za-z0-9]|{WhiteSpace}|[(){}\[\]?!/+-\.;])
COMMENT_PADDING = ([A-Za-z0-9]|{WhiteSpace}|[(){}\[\]?!+-\.;])
COMMENT_CONTENT = ({COMMENT_STAR}*{COMMENT_PADDING}{COMMENT_SLASH}*)* | {COMMENT_STAR}* | {COMMENT_SLASH}*
COMMENT_TYPE1 	= ("//"([A-Za-z0-9]|{NonLineTerminator}|[(){}\[\]?!*+-/\.;])*{LineTerminator})
COMMENT_TYPE2	= ("/*"{COMMENT_CONTENT}"*/")
COMMENT		= ({COMMENT_TYPE1} | {COMMENT_TYPE2})
INTEGER		= 0 | [1-9][0-9]*
STRING 		= \"[a-zA-Z]*\"
ID		= [a-zA-Z][a-zA-Z0-9]*

COMMENT_ERROR	= "/*" | "//"
INTEGER_ERROR	= (0+[0-9]+) | ([0-9][0-9][0-9][0-9][0-9][0-9]+)




/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/

%%

/************************************************************/
/* LEXER matches regular expressions to actions (Java code) */
/************************************************************/

/**************************************************************/
/* YYINITIAL is the state at which the lexer begins scanning. */
/* So these regular expressions will only be matched if the   */
/* scanner is in the start state YYINITIAL.                   */
/**************************************************************/

<YYINITIAL> {

"+"				{ return symbol(TokenNames.PLUS, yyline+1);}
"-"				{ return symbol(TokenNames.MINUS, yyline+1);}
"*"				{ return symbol(TokenNames.TIMES, yyline+1);}
"/"				{ return symbol(TokenNames.DIVIDE, yyline+1);}
"("				{ return symbol(TokenNames.LPAREN, yyline+1);}
")"				{ return symbol(TokenNames.RPAREN, yyline+1);}
"["				{ return symbol(TokenNames.LBRACK, yyline+1);}
"]"				{ return symbol(TokenNames.RBRACK, yyline+1);}
"{"				{ return symbol(TokenNames.LBRACE, yyline+1);}
"}"				{ return symbol(TokenNames.RBRACE, yyline+1);}
"nil"				{ return symbol(TokenNames.NIL, yyline+1);}
","				{ return symbol(TokenNames.COMMA, yyline+1);}
"."				{ return symbol(TokenNames.DOT, yyline+1);}
";"				{ return symbol(TokenNames.SEMICOLON, yyline+1);}
"int"				{ return symbol(TokenNames.TYPE_INT, yyline+1);}
":="				{ return symbol(TokenNames.ASSIGN, yyline+1);}
"="				{ return symbol(TokenNames.EQ, yyline+1);}
"<"				{ return symbol(TokenNames.LT, yyline+1);}
">"				{ return symbol(TokenNames.GT, yyline+1);}
"void"				{ return symbol(TokenNames.TYPE_VOID, yyline+1);}
"array"				{ return symbol(TokenNames.ARRAY, yyline+1);}
"class"				{ return symbol(TokenNames.CLASS, yyline+1);}
"extends"			{ return symbol(TokenNames.EXTENDS, yyline+1);}
"return"			{ return symbol(TokenNames.RETURN, yyline+1);}
"while"				{ return symbol(TokenNames.WHILE, yyline+1);}
"if"				{ return symbol(TokenNames.IF, yyline+1);}
"new"				{ return symbol(TokenNames.NEW, yyline+1);}
"string"			{ return symbol(TokenNames.TYPE_STRING, yyline+1);}
{INTEGER}			{ return symbol(TokenNames.INT,    new Pair<Integer,Integer>(new Integer(yytext()), yyline+1));}
{STRING}			{ return symbol(TokenNames.STRING, new Pair<String,Integer>(new String(yytext()), yyline+1));}
{ID}				{ return symbol(TokenNames.ID,     new Pair<String,Integer>(new String(yytext()), yyline+1));}   
{COMMENT}			{ /* Just skip what was found, do nothing */ }
{WhiteSpace}			{ /* Just skip what was found, do nothing */ }
{COMMENT_ERROR}			{ throw new Error("Illegal Token!");}
{INTEGER_ERROR}			{ throw new Error("Illegal Token!");}
<<EOF>>				{ return symbol(TokenNames.EOF);}
}
