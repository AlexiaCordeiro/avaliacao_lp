package lexical;

public class Token {

    public static enum Type {
        // SPECIALS
        UNEXPECTED_EOF,
        INVALID_TOKEN,
        END_OF_FILE,
    
        // Symbols.
        OPEN_PAR,          // (
        CLOSE_PAR,         // )
        OPEN_CHEV,        // <
        CLOSE_CHEV,      // >
        HASHTAG,         //#

        // Operators.    
        NOT_DEFINED,    // !DEFINDED
        DEFINED, //DEFINE

        // Keywords.
        DEFINE,              
        UNDEF,              
        ERROR,             
        INCLUDE,              
        IFDEF,             
        IFNDEF,            
        IF,               
        ELIF,                
        ELSE,         
        ENDIF,             
        
        //OHTERS
        NAME,          // identifier
        NUMBER,        // integer
        TEXT           // string
    };

    public String lexeme;
    public Type type;
    public int line;

    public Token(String lexeme, Type type) {
        this.lexeme = lexeme;
        this.type = type;
        this.line = 0;
    }

    public String toString() {
        return new StringBuffer()
            .append("(\"")
            .append(this.lexeme)
            .append("\", ")
            .append(this.type)
            .append(", ")
            .append(this.line)
            .append(")")
            .toString();
    }

}
