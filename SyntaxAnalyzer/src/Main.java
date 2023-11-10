/*
CSCI 316
PROJECT 2
By Maheen Naqvi and Mindy Schreck
 */
import java.io.*;
import java.util.Scanner;
import java.util.Stack;

public class SyntaxChecker
{


    public static boolean isOperators(String lex)
    {   //Op = Operators
        String OP[] =
                {
                        ":=", "+","-", "*", "/", "%", "=", "<>", ">", "<", ">=", "<=", "&", "|", "!", "~", "<<", ">>", "(", ")", "(*", "*)", "[", "]", "{", "}"
                } ;
        for (int i = 0; i < OP.length; i++)
        {
            if(lex.equals(OP[i]))
                return true; // if operator

        }
        return false; // if not an operator
    }

    public static String ResWords (String lex)
    {
        String RW[] =
                {
                        "and", "array", "begin", "case", "const",
                        "div", "do", "downto", "else", "end",
                        "file", "for", "function", "goto", "if",
                        "in", "label", "mod", "nil", "not",
                        "of", "or", "packed", "procedure", "program",
                        "record", "repeat", "set", "then", "to",
                        "type", "until", "var", "while", "with"
                };
        for (int i = 0; i < RW.length; i++)
        {
            if (lex.equalsIgnoreCase(RW[i]))
                return "KeyWord";
        }
        return "IDENT";
    }


    public static void main(String[] args) throws IOException
    {
        String input = "PROGRAM  ChangeMaker;\n" +
                "  (* Make change  for a dollar *)\n" +
                "  CONST remove from\n" +
                "  Dollar := 100;\n" +
                "  VAR\n" +
                "  Cost:      INTEGER;\n" +
                "  Remainder: INTEGER\n" +
                "  Dimes = INTEGER;\n" +
                "  BEGIN\n" +
                "  (* Input the Cost *)\n" +
                "  Write('Enter the  cost in cents: ');\n" +
                "  Read(Cost);\n" +
                "  (* Make the  Change in dimes )\n" +
                "  Remainder := 100 - Cost;\n" +
                "  Dimes :=  Remainder DIV 10;\n" +
                "  Remainder == Remainder % 10;\n" +
                "  Remainder =  Remainder MOD 5;\n" +
                "  IF (Remainder = 0) THEN Write (“No money left”) ELSE Write (Remainder);\n" +
                "  END. (*  ChangeMaker *)";
        // Create a scanner to read the input
        Scanner scanner = new Scanner(input);
        // Create a stack to keep track of matching parentheses, quotes, and begin/end statements
        Stack<String> stack = new Stack<>();
        // Read each line of the input
        while (scanner.hasNextLine()) {
            // Read the next line of input
            String line = scanner.nextLine();
            // Use the lexical analyzer to break down the line into lexemes
            int index = 0;
            while (index < line.length()) {
                String lexeme = "";
                // Check for keywords and other identifiers
                if (Character.isLetter(line.charAt(index))) {
                    // Check for keywords and other identifiers
                    String token = null;
                    if (Character.isLetter(line.charAt(index))) {
                        lexeme = lexeme + line.charAt(index);
                        index++;
                        while (index < line.length() && (Character.isLetter(line.charAt(index)) ||
                                Character.isDigit(line.charAt(index)))) {
                            lexeme = lexeme + line.charAt(index);
                            index++;
                        }
                        token = ResWords(lexeme);
                        // Check for assignment statements
                        if (token.equals("ASSIGN_OP")) {
                            // Check for a valid variable name on the LHS assignment
                            if (!stack.empty() && stack.peek().equals("IDENT"))
                            {
                                stack.pop();
                            } else
                            {
                                System.out.println("Syntax error: expected a var name on the left side of asnmnt");
                            }
                            //Check for valid  expr on RHS of assigment
                            String expr = scanner.next();
                            if (!isValidExpression(expr)) {
                                System.out.println(("Syntax error: invalid expression"));
                            }
                        }
                    }
                    // Check for variable declarations and initializations
                    if (token.equals("VAR")) {
                        // Parse the list of variable names and types
                        while (scanner.hasNext()) {
                            String name = scanner.next();
                            String type = scanner.next();
                            // Check that the name is a valid identifier and the type is a valid keyword
                            if (!ResWords(name).equals("IDENT")) {
                                System.out.println("Syntax error: invalid variable name " + name);
                            }
                            if (!ResWords(type).equals("IDENT")) {
                                System.out.println("Syntax error: invalid variable type " + type);
                            }
                            // Check for initializations
                            if (scanner.hasNext() && scanner.next().equals(":=")) {
                                // Check for a valid expression after the initialization
                                String expr = scanner.next();
                                if (!isValidExpression(expr)) {
                                    System.out.println("Syntax error: invalid expression " + expr);
                                }
                            }
                        }
                    }
                    // Check for arithmetic operations
                    if (token.equals("ADD_OP") || token.equals("SUB_OP") ||
                            token.equals("MUL_OP") || token.equals("DIV_OP") ||
                            token.equals("MOD_OP")) {
                        // Check for valid operands before and after the operator
                        if (stack.size() >= 2 && isOperators(stack.get(stack.size() - 2)) &&
                                isOperators(stack.get(stack.size() - 1))) {
                            // Pop the operands off the stack
                            stack.pop();
                            stack.pop();
                            // Push the result of the operation onto the stack
                            stack.push("EXPR");
                        } else {
                            System.out.println("Syntax error: invalid operands for arithmetic operation");
                        }
                    }
                    // Check for boolean expressions
                    if (token.equals("EQUALS") || token.equals("NOT_EQUALS") ||
                            token.equals("GREATER") || token.equals("LESS") ||
                            token.equals("GREATER_EQUALS") || token.equals("LESS_EQUALS")) {
                        // Check for valid operands before and after the operator
                        if (stack.size() >= 2 && isOperators(stack.get(stack.size() - 2)) &&
                                isOperators(stack.get(stack.size() - 1))) {
                            // Pop the operands off the stack
                            stack.pop();
                            stack.pop();
                            // Push the result of the boolean expression onto the stack
                            stack.push("BOOL_EXPR");
                        } else {
                            System.out.println("Syntax error: invalid operands for boolean expression");
                        }
                    }
                    // Check for "if" statements
                    if (token.equals("IF")) {
                        // Check for a valid boolean expression in parentheses after the "if"
                        String expr = scanner.next();
                        if (expr.startsWith("(") && expr.endsWith(")") &&
                                isValidBooleanExpression(expr.substring(1, expr.length() - 1))) {
                            // Check for a "then" token and a valid statement after the boolean expression
                            String thenToken = scanner.next();
                            if (thenToken.equals("THEN") && isValidStatement(scanner.next())) {
                                // Check for an "else" token and a valid statement after the "then" clause (optional)
                                if (scanner.hasNext()) {
                                    String elseToken = scanner.next();
                                    if (elseToken.equals("ELSE") && isValidStatement(scanner.next())) {
                                        // "if" statement is valid
                                    } else {
                                        System.out.println("Syntax error: invalid 'else' clause in 'if' statement");
                                    }
                                }
                                // "if" statement is valid
                            } else {
                                System.out.println("Syntax error: invalid 'then' clause or statement in 'if' statement");
                            }
                        } else {
                            System.out.println("Syntax error: invalid boolean expression in  'if' statement");
                        }
                    }

                    // Check for while loops
                    if (token.equals("WHILE")) {
                        // Check for a valid boolean expression in parentheses after the "while"
                        String expr = scanner.next();
                        if (expr.startsWith("(") && expr.endsWith(")") && isValidBooleanExpression(expr.substring(1, expr.length() - 1)))
                        {
                            // Check for a "do" token and a valid statement after the boolean expression
                            String doToken = scanner.next();
                            if (doToken.equals("DO") && isValidStatement(scanner.next()))
                            {
                                // While loop is valid
                            } else {
                                System.out.println("Syntax error: invalid 'd' clause or statement in while loop");
                            }
                        } else {
                            System.out.println("Syntax error: invalid boolean expression in while loop");
                        }
                    }
                }
            }
        }
    }

    /*logically should be correct but some unfinished methods */

    private static boolean isValidBooleanExpression(String substring) {
    }

    private static boolean isValidStatement(String next) {
    }


}






}