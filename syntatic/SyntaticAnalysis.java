package syntatic;

import lexical.LexicalAnalysis;
import lexical.Token;
import lexical.Token.Type;


public class SyntaticAnalysis {

    private LexicalAnalysis lex;
    private Token previous;
    private Token current;
    private Token next;

    public SyntaticAnalysis(LexicalAnalysis lex) {
        this.lex = lex;
        this.previous = null;
        this.current = lex.nextToken();
        this.next = lex.nextToken();       
    }

    public void process() {
        procCode();
        eat(Type.END_OF_FILE);
    }

    private void advance() {
        //System.out.println("Found " + current);
        previous = current;
        current = next;
        next = lex.nextToken();
    }

    private void eat(Token.Type type) {
        if (type == current.type) {
            advance();
        } else {
            reportError();
        }
    }

    private boolean check(Token.Type ...types) {
        for (Token.Type type : types) {
            if (current.type == type)
                return true;
        }

        return false;
    }

    private boolean checkNext(Token.Type ...types) {
        for (Token.Type type : types) {
            if (next.type == type)
                return true;
        }

        return false;
    }

    private boolean match(Token.Type ...types) {
        if (check(types)) {
            advance();
            return true;
        } else {
            return false;
        }
    }

    private void reportError() {
        String reason;
        switch (current.type) {
            case INVALID_TOKEN:
                reason = String.format("Lexema inválido [%s]", current.lexeme);
                break;
            case UNEXPECTED_EOF:
            case END_OF_FILE:
                reason = "Fim de arquivo inesperado";
                break;
            default:
                reason = String.format("Lexema não esperado [%s]", current.lexeme);
                break;
        }

        throw new SyntaticException(current.line, reason);
    }
    //<code> ::= { <macro> }
    private void procCode(){
        //List<Macro> macros = new ArrayList<Macro>();

        while(check(Type.HASHTAG)){
            procMacro();
            eat(Type.HASHTAG);
        }

    }

    //<macro> ::= <define> | <undef> | <error> | <include> | <ifdef> | <ifndef> | <if>
    private void procMacro(){
        //Macro macro = null;

        if (checkNext(Type.HASHTAG)) {
            
            if (match(Type.DEFINE)) {
                procDefine();
            }else if (match(Type.UNDEF)) {
                procUndef();
            }else if(match(Type.ERROR)) {
                procError();
            }else if(match(Type.INCLUDE)) {
                procInclude();
            }else if(match(Type.IFDEF)) {
                procIfdef();
            }else if(match(Type.IFNDEF)) {
                procIfndef();
            }else if(match(Type.IF)) {
                procIf();
            }
        }

    }
    
    //<define> ::= '#' define <name> [ <number> | <text> ]
    private void procDefine(){
        Token token = null;

        eat(Type.HASHTAG);
        eat(Type.DEFINE);

        procName();

        if(match(Type.NUMBER, Type.TEXT)){
            token = previous;
        }

        if (token != null){
            switch(token.type){
                case NUMBER:
                    procNumber();
                    break;
                case TEXT:
                default:
                    procText();
                    break;
            }
        }
    }
    //<undef> ::= '#' undef <name>
    private void procUndef(){
        eat(Type.HASHTAG);
        eat(Type.UNDEF);
        procName();
    }
    //<error> ::= '#' error <text>
    private void procError(){
        eat(Type.HASHTAG);
        eat(Type.ERROR);

        procText();
    }
    //<include> ::= '#' include ( <text> | '<' <name> '>')
    private void procInclude(){
        eat(Type.HASHTAG);
        eat(Type.INCLUDE);

        if(match(Type.OPEN_CHEV,Type.TEXT)){
            switch(previous.type){

                case TEXT:
                    procText();
                    break;
                case OPEN_CHEV:
                default:
                    procName();
                    eat(Type.CLOSE_CHEV);
                    break;
            }
        }
    }

    //<ifdef> ::= '#' ifdef <name> <macro> [ <elif> | <else> ] <endif>
    private void procIfdef(){
        Token token = null;
        eat(Type.HASHTAG);
        eat(Type.IFDEF);

        procName();
        procMacro();

        if(match(Type.HASHTAG)){
            if(match(Type.ELIF, Type.ELSE)){
                token = previous;
            }
        }
        if (token != null){
            switch(token.type){
                case ELIF:
                    procElif();
                    break;
                case ELSE:
                default:
                    procElse();
                    break;
            }
        }
        procEndif();
    }
    
    //<ifndef> ::= '#' ifndef <name> <macro> [ <elif> | <else> ] <endif>
    private void procIfndef(){
        Token token = null;
        eat(Type.HASHTAG);
        eat(Type.IFNDEF);
        procName();
        procMacro();
        if(match(Type.HASHTAG)){
            if(match(Type.ELIF, Type.ELSE)){
                token = previous;
            }
        }

        if (token != null){
            switch(token.type){
                case ELIF:
                    procElif();
                    break;
                case ELSE:
                default:
                    procElse();
                    break;
            }
        }
        procEndif();
    }
    
    //<if> ::= '#' if ( 'defined(name)'| '!defined(name)' ) <macro> [ <elif> | <else> ] <endif>
    private void procIf(){
        
        Token token = null;

        eat(Type.HASHTAG);
        eat(Type.IF);

        if(match(Type.DEFINED,Type.NOT_DEFINED)){
            switch(previous.type){
                case DEFINED:
                    eat(Type.OPEN_PAR);
                    eat(Type.NAME);
                    eat(Type.CLOSE_PAR);
                    break;
                case NOT_DEFINED:
                default:
                    eat(Type.OPEN_PAR);
                    eat(Type.NAME);
                    eat(Type.CLOSE_PAR);
                    break;
            }
        }

        procMacro();

        if(match(Type.HASHTAG)){
            if(match(Type.ELIF, Type.ELSE)){
                token = previous;
            }
        }

        if (token != null){
            switch(token.type){
                case ELIF:
                    procElif();
                    break;
                case ELSE:
                default:
                    procElse();
                    break;
            }
        }
        
        procEndif();

    }
    
    //<elif> ::= '#' elif ( 'defined(name)'| '!defined(name)' ) <macro> [ <elif> | <else> ]
    private void procElif(){
        Token token = null;

        eat(Type.HASHTAG);
        eat(Type.ELIF);

        if(match(Type.DEFINED,Type.NOT_DEFINED)){
            switch(previous.type){
                case DEFINED:
                    eat(Type.OPEN_PAR);
                    eat(Type.NAME);
                    eat(Type.CLOSE_PAR);
                    break;
                case NOT_DEFINED:
                default:
                    eat(Type.OPEN_PAR);
                    eat(Type.NAME);
                    eat(Type.CLOSE_PAR);
                    break;
            }
        }

        procMacro();

        if(match(Type.HASHTAG)){
            if(match(Type.ELIF, Type.ELSE)){
                token = previous;
            }
        }

        if (token != null){
            switch(token.type){
                case ELIF:
                    procElif();
                    break;
                case ELSE:
                default:
                    procElse();
                    break;
            }
        }
    }
    //<else> ::= '#' else <macro>

    private void procElse(){
        eat(Type.HASHTAG);
        eat(Type.ELSE);

        procMacro();
    }
    
    //<endif> ::= '#' endif

    private void procEndif(){
        eat(Type.HASHTAG);
        eat(Type.ENDIF);
    }

    private Token procNumber() {
        eat(Type.NUMBER);
        return previous;
    }

    private Token procText() {
        eat(Type.TEXT);
        return previous;
    }

    private Token procName() {
        eat(Type.NAME);
        return previous;
    }
}