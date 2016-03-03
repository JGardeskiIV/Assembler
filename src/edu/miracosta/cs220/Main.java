package edu.miracosta.cs220;

public class Main {

    public static void main(String[] args) {

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
    public String cleanLine(String rawLine) {
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
    public String decimalToBinary(int decimal) {
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
    public boolean isValidName(String symbol) {
        final String VALID_FIRST_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String VALID_CHARS = VALID_FIRST_CHARS + "$_.:";

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
}
