package lexical;

import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.HashMap;
import java.util.Map;
public class LexicalAnalysis implements AutoCloseable {

    private int line;
    private PushbackInputStream input;
    private static Map<String, Token.Type> keywords;

    static {
        keywords = new HashMap<String, Token.Type>();
         // Symbols.

         keywords.put("(", Token.Type.OPEN_PAR);
         keywords.put(")", Token.Type.CLOSE_PAR);
         keywords.put("<", Token.Type.OPEN_CHEV);
         keywords.put(">", Token.Type.CLOSE_CHEV); 
         keywords.put("#", Token.Type.HASHTAG);

         // Operators.
  
         keywords.put("!", Token.Type.NOT_DEFINED);
         keywords.put("d", Token.Type.DEFINED);

 
         // Keywords.
         keywords.put("define", Token.Type.DEFINE);
         keywords.put("undef", Token.Type.UNDEF);
         keywords.put("error", Token.Type.ERROR);
         keywords.put("include", Token.Type.INCLUDE);
         keywords.put("ifdef", Token.Type.IFDEF);
         keywords.put("ifndef", Token.Type.IFNDEF);
         keywords.put("if", Token.Type.IF);
         keywords.put("elif", Token.Type.ELIF);
         keywords.put("else", Token.Type.ELSE);
         keywords.put("endif", Token.Type.ENDIF);

    }



    public LexicalAnalysis(InputStream is) {
        input = new PushbackInputStream(is);
        line = 1;
    }

    public void close() {
        try {
            input.close();
        } catch (Exception e) {
            throw new LexicalException("Unable to close file");
        }
    }

    public int getLine() {
        return this.line;
    }

    public Token nextToken() {
        Token token = new Token("", Token.Type.END_OF_FILE);

        int state = 1;
        while (state != 7 && state != 8) {
            int c = getc();
            switch (state) {
                case 1:
                if (c == ' ' || c == '\t' ||
                        c == '\r') {
                    state = 1;
                } else if (c == '\n') {
                    state = 1;
                    line++;
                } else if (c == '#') {
                    state = 2;
                    token.lexeme += (char) c;  
                }else if(c == '"'){
                    state = 4;
                    token.lexeme += (char) c;
                }else if (Character.isDigit(c)) {
                    state = 5;
                    token.lexeme += (char) c;
                }else if (Character.isLetter(c)) {
                    state = 6;
                    token.lexeme += (char) c;
                }else if(c == 'd'){
                    state = 7;
                    token.lexeme += (char) c;
                }else if(c == '!'){
                    state = 7;
                    token.lexeme += (char) c;
                }else if (c == '(' || c == ')' || c == '<' || c == '>') {
                    state = 7;
                    token.lexeme += (char) c;
                } else if (c == -1) {
                    state = 8;
                    token.type = Token.Type.END_OF_FILE;
                }else {
                    state = 8;
                    token.lexeme += (char) c;
                    token.type = Token.Type.INVALID_TOKEN;
                }
                    break;
                case 2:
                    if (c == ' ') {
                        state = 2;
                        token.lexeme += (char) c;
                        
                    }else{
                        state = 3;
                        ungetc(c);
                    }
                    break;
                case 3:
                    if(Character.isLetter(c)){
                        state = 3;
                        token.lexeme += (char) c; 
                    }else{
                        state = 7;
                        ungetc(c);
                    }
                    break;
                case 4:
                    if (c != '"') {
                        state = 4;
                        token.lexeme += (char) c; 
                    }else{
                        state = 7;
                        ungetc(c);
                    }
                    break;
                case 5:
                    if (Character.isDigit(c)) {
                        state = 5;
                        token.lexeme += (char) c; 
                    }else{
                        state = 8;
                        ungetc(c);
                    }
                    break;
                case 6:
                    if (Character.isDigit(c)) {
                        state = 6;
                        token.lexeme += (char) c; 
                        
                    }else{
                        state = 8;
                        ungetc(c);
                    }

                    break;

                default:
                    throw new RuntimeException("Unreachable");
            }
        }
    

        if (state == 7)
            token.type = keywords.containsKey(token.lexeme) ?   
                keywords.get(token.lexeme) : Token.Type.NAME;
                token.line = this.line;

        return token;
    }

    private int getc() {
        try {
            return input.read();
        } catch (Exception e) {
            throw new LexicalException("Unable to read file");
        }
    }

    private void ungetc(int c) {
        if (c != -1) {
            try {
                input.unread(c);
            } catch (Exception e) {
                throw new LexicalException("Unable to ungetc");
            }
        }
    }

}
