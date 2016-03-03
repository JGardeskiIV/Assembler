package edu.miracosta.cs220;

public class Main {

    public enum Command {
        A_INST, C_INST, L_INST, N_INST
    }

    public static void main(String[] args) {
        System.out.println(cleanLine("     M = M +   1 // Testing...") + "\n");
        System.out.println("Decimal: 1682 Binary: " + decimalToBinary(1682) + "\n");
        System.out.println("humps123$!._ is a valid identifier: " + isValidName("humps123$!._"));
        System.out.println("Pro2:921ab is a valid identifier: " + isValidName("Pro2:921ab") + "\n");
        System.out.println("M = M + 1 is a " + parseCommandType("M=M+1").toString());
        System.out.println("0;JMP is a " + parseCommandType("0;JMP").toString());
        System.out.println("@52 is a " + parseCommandType("@52").toString());
        System.out.println("(FART) is a " + parseCommandType("(FART)").toString());
        System.out.println("The empty string is a " + parseCommandType("").toString());
    }

    /**
     * Cleans a raw instruction by removing non-essential parts
     *
     * PRECONDITION:    rawLine contains a line of assembly code and is not null
     * POSTCONDITION:   returns rawLine with no whitespace or comments anywhere in the instruction
     *
     * @param   rawLine     -   a non-null line of assembly code
     * @return  cleanedLine -   the instruction with no whitespace or comments
     */
    public static String cleanLine(String rawLine) {
        String cleanedLine = rawLine;
        int commentIndex = rawLine.indexOf("//");
        if (commentIndex != -1) {
            cleanedLine = rawLine.substring(0, commentIndex);
        }
        cleanedLine = cleanedLine.trim();
        cleanedLine = cleanedLine.replaceAll("\t", "");
        cleanedLine = cleanedLine.replaceAll(" ", "");
        return cleanedLine;
    }

    /**
     * Converts an integer from decimal notation to binary notation
     *
     * PRECONDITION:    decimal is a non-negative integer less than 2^15
     * POSTCONDITION:   returns a 16-bit binary number as a string, with the
     *                  MSB on the left and the LSB on the right
     *
     * @param   decimal -   a non-negative integer less than 2^15
     * @return  result  -   a 16-bit binary number with the MSB on the left
     */
    public static String decimalToBinary(int decimal) {
        int remainder;
        String result = "";
        for (int i = 0; i < 15; i++) {
            remainder = decimal % 2;
            decimal /= 2;
            result = remainder + result;
        }
        return 0 + result;
    }

    /**
     * Checks the validity of identifiers for assembly code symbols
     *
     * PRECONDITION:    symbol has been pulled from a cleaned line and is an identifier
     * POSTCONDITION:   returns true if symbol is valid, & false otherwise
     *
     * @param   symbol  -   an identifier pulled from a cleaned line
     * @return          -   true if the identifier is valid, false otherwise
     */
    public static boolean isValidName(String symbol) {
        final String VALID_FIRST_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String VALID_CHARS = VALID_FIRST_CHARS + "0123456789$_.:";

        if (symbol == null || symbol.length() == 0) {
            return false;
        } else {
            char current = symbol.charAt(0);
            if (VALID_FIRST_CHARS.indexOf(current) == -1) {
                return false;
            }
            for (int i = 1; i < symbol.length() - 1; i++) {
                current = symbol.charAt(i);
                if (VALID_CHARS.indexOf(current) == -1) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Determines the command type of the instruction from the parameter
     *
     * PRECONDITION:    clean has no whitespace or comments
     * POSTCONDITION:   returns the command type of the parameter as a Command:
     *                  A_INST, C_INST, L_INST, N_INST
     * @param   clean   -   an instruction with no whitespace or comments
     * @return          -   the command type of the parameter, as a Command
     */
    public static Command parseCommandType(String clean) {
        if (clean == null || clean.equals("")) {
            return Command.N_INST;
        } else {
            char first = clean.charAt(0);
            if (first == '@') {
                return Command.A_INST;
            } else if (clean.contains(";") || clean.contains("=")) {
                return Command.C_INST;
            } else if (first == '(' && clean.charAt(clean.length() - 1) == ')') {
                return Command.L_INST;
            } else {
                return Command.N_INST;
            }
        }
    }
}
