import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import lexical.LexicalAnalysis;
import lexical.Token;
import lexical.Token.Type;
import syntatic.SyntaticAnalysis;

public class macros {
    public static void main(String[] args) {
        try {
            switch (args.length) {
                case 0:
                    runPrompt();
                    break;
                case 1:
                    runFile(args[0]);
                    break;
                default:
                    System.out.println("Usage: java macros [Macros file]");
                    break;
            }
        } catch (Exception e) {
            System.err.println("Internal error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runPrompt() throws Exception {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) {
                System.out.println();
                break;
            }

            run(new ByteArrayInputStream(line.getBytes()));
        }
    }

    private static void runFile(String filename) throws Exception {
        run(new FileInputStream(filename));
    }

    private static void run(InputStream is) {
        try (LexicalAnalysis l = new LexicalAnalysis(is)) {
            // O código a seguir é usado apenas para testar o analisador léxico.
            // TODO: depois de pronto, comentar o código abaixo.
             Token lex;
             do {
                 lex = l.nextToken();
                 System.out.printf("%02d: (\"%s\", %s)\n", lex.line,
                     lex.lexeme, lex.type);
             } while (lex.type != Type.END_OF_FILE &&
                      lex.type != Type.INVALID_TOKEN &&
                      lex.type != Type.UNEXPECTED_EOF);
            

            
            SyntaticAnalysis s = new SyntaticAnalysis(l);
            s.process();

            System.out.println("Sim");
        } catch (Exception e) {
            System.out.println("Não");

             //System.out.println(e.getMessage());
            // e.printStackTrace();
        }
    }

}
