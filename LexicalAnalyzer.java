/*
* Project 1: Lexical Analyzer
* Description:program is able to accept as input a file containing statements
in Pascal and correctly perform a lexical analysis of each statement.
* Code by Maheen Naqvi and Amanda Schreck
* CSCI 323 : T/TH- 9:15- 10:30
* Project Due: Wed, 03/15
 */

import java.util.Scanner;
import java.io.*;

public class LexicalAnalyzer {
    // the array of the pascal reserved words in ResWords
    public static String ReservedWords(String lex) {
        String ResWords [] =
                {
                        "and", "array", "begin", "case", "const",
                        "div", "do", "downto", "else", "end",
                        "file", "for", "function", "goto", "if",
                        "in", "label", "mod", "nil", "not",
                        "of", "or", "packed", "procedure", "program",
                        "record", "repeat", "set", "then", "to",
                        "type", "until", "var", "while", "with"
                };

        // if lex equals res word then return keyword despite their cases
        for (int i = 0; i < ResWords.length; i++) {
            if (lex.equalsIgnoreCase(ResWords[i]))
                return "KeyWord";
        }
        return "Identifier";
    }

    // array  of Operators
    public static boolean isOperators(String lex) {
        String Operators[] =
                {
                        ":=", "+", "-", "*", "/", "%",
                        "=", "<>", ">", "<", ">=", "<=",
                        "&", "|", "!", "~", "<<", ">>",
                        "(", ")", "(*", "*)", "[", "]",
                        "{", "}"
                };

        for (int i = 0; i < Operators.length; i++) {
            // if it is an operator
            if (lex.equals(Operators[i]))
                return true;

        }
        // otherwise it is not an operator
        return false;
    }

    // looks up for acceptable operators and other identifiers
    public static String LookUp(String lex) {

        //List of acceptable Operators
        String OperatorA[] =
                {
                        ":=", "+", "-", "*", "/", "%",
                        "=", "<>", ">", "<", ">=", "<=",
                        "&", "|", "!", "~", "<<", ">>",
                        "(", ")", "(*", "*)", "[", "]",
                        "{", "}", "'", ",", ".", ":", ";"
                };

        //an array of recognized tokens
        String RecognizedTokens[] =
                {
                        "ASSIGN_OP", "ADD_OP", "SUB_OP", "MUL_OP",
                        "DIV_OP", "MOD_OP", "EQUALS", "NOT_EQUALS",
                        "GREATER", "LESS", "GREATER_EQUALS", "LESS_EQUALS",
                        "AND", "OR", "NOT", "ONE_COMP_OP", "LS_OP",
                        "RS_OP", "OPEN_PAREN", "CLOSE_PAREN", "OPEN_COMMENT",
                        "CLOSE_COMMENT", "OPEN_BRACK", "CLOSE_BRACK",
                        "OPEN_BRACE", "CLOSE_BRACE", "SEPARATOR",
                        "SEPARATOR", "SEPARATOR", "COLON", "SEMICOLON"
                };
        // if lex = acceptable operator then return recognized token else return unknown
        for (int i = 0; i < OperatorA.length; i++) {
            if (lex.equals(OperatorA[i]))
                return RecognizedTokens[i];
        }
        return "Unknown";
    }

    public static void LexicalAnalyzer(Scanner in) throws IOException {
        String input;
        String lexeme;
        int index;

        //while there exists a next line in the input for the lexical analyzer to read
        while (in.hasNext())
        {
            index = 0;
            input = in.next();

            while (index < input.length()) {
                lexeme = "";

                // it looks for keywords and other identifiers in a certain string
                if (Character.isLetter(input.charAt(index))) {
                    lexeme = lexeme + input.charAt(index);
                    index++;

                    while (index < input.length() && (Character.isLetter(input.charAt(index)) || Character.isDigit(input.charAt(index)))) {
                        lexeme = lexeme + input.charAt(index);
                        index++;
                    }
                    System.out.println("Token: " + ReservedWords(lexeme) + "\n" + "     Lexeme: " + lexeme);
                }
                //it looks for numbers
                else if (Character.isDigit(input.charAt(index))) {
                    lexeme = lexeme + input.charAt(index);
                    index++;

                    while (index < input.length() && Character.isDigit(input.charAt(index))) {
                        lexeme = lexeme + input.charAt(index);
                        index++;
                    }
                    System.out.println("Token: NumLit" + "\n     Lexeme: " + lexeme);
                }
                //it looks for operators and other special symbols
                else {
                    if (!(input.charAt(index) == ' ')) {
                        lexeme = lexeme + input.charAt(index);
                        index++;

                        if (isOperators(lexeme)) {
                            while (index < input.length() && isOperators(lexeme + input.charAt(index))) {
                                lexeme = lexeme + input.charAt(index);
                                index++;
                            }
                        }
                    }
                    System.out.print("Token: " + LookUp(lexeme) + "\n     Lexeme:  " + lexeme + " \n");
                }
            }
        }
        in.close();
    }

    public static void main(String[] args) throws IOException {
        Scanner File = new Scanner(new FileReader("Parse Project Pascal Code.txt"));

        LexicalAnalyzer(File);
    }
}