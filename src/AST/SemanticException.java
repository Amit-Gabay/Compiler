package AST;

public class SemanticException extends Exception
{
    public int line;

    public SemanticException(int line)
    {
        this.line = line;
    }
}
