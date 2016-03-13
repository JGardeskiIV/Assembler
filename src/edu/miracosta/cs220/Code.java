/************************************************************************************
 *
 * Class name:    Code
 * Package:       edu.miracosta.cs220
 * Description:   Uses HashMaps to store the comp, dest, and jump component
 *                possibilities and their binary equivalents to be used as
 *                lookup references.
 *
 * History:       Mar. 4, J, author, taken from Lab #6 UML & Method Layout
 *
 * Methods:       Public:   Code(), getComp(String), getDest(String), getJump(String)
 *                          decimalToBinary(int)
 *
 ************************************************************************************/
package edu.miracosta.cs220;

import java.util.HashMap;

public class Code {

    /**********************
     * Instance Variables *
     **********************/
    private HashMap<String, String> compCodes;
    private HashMap<String, String> destCodes;
    private HashMap<String, String> jumpCodes;

    /****************
     * Constructors *
     ****************/

    /**
     * Initializes hashmaps with binary codes for easy lookup
     *
     * PRECONDITION:    N/A
     * POSTCONDITION:   all hashmaps have lookups for valid codes
     */
    Code() {
        compCodes = new HashMap<>();
        destCodes = new HashMap<>();
        jumpCodes = new HashMap<>();
        //  Build compCodes table
        compCodes.put("0",   "0101010");
        compCodes.put("1",   "0111111");
        compCodes.put("-1",  "0111010");
        compCodes.put("D",   "0001100");
        compCodes.put("A",   "0110000");
        compCodes.put("!D",  "0001101");
        compCodes.put("!A",  "0110001");
        compCodes.put("-D",  "0001111");
        compCodes.put("-A",  "0110011");
        compCodes.put("D+1", "0011111");
        compCodes.put("A+1", "0110111");
        compCodes.put("D-1", "0001110");
        compCodes.put("A-1", "0110010");
        compCodes.put("D+A", "0000010");
        compCodes.put("A+D", "0000010");
        compCodes.put("D-A", "0010011");
        compCodes.put("A-D", "0000111");
        compCodes.put("D&A", "0000000");
        compCodes.put("A&D", "0000000");
        compCodes.put("D|A", "0010101");
        compCodes.put("A|D", "0010101");
        compCodes.put("M",   "1110000");
        compCodes.put("!M",  "1110001");
        compCodes.put("-M",  "1110011");
        compCodes.put("M+1", "1110111");
        compCodes.put("M-1", "1110010");
        compCodes.put("D+M", "1000010");
        compCodes.put("M+D", "1000010");
        compCodes.put("D-M", "1010011");
        compCodes.put("M-D", "1000111");
        compCodes.put("D&M", "1000000");
        compCodes.put("M&D", "1000000");
        compCodes.put("D|M", "1010101");
        compCodes.put("M|D", "1010101");
        //  Build destCodes table
        destCodes.put("null", "000");
        destCodes.put("M",    "001");
        destCodes.put("D",    "010");
        destCodes.put("MD",   "011");
        destCodes.put("A",    "100");
        destCodes.put("AM",   "101");
        destCodes.put("AD",   "110");
        destCodes.put("AMD",  "111");
        //  Build jumpCodes table
        jumpCodes.put("null", "000");
        jumpCodes.put("JGT",  "001");
        jumpCodes.put("JEQ",  "010");
        jumpCodes.put("JGE",  "011");
        jumpCodes.put("JLT",  "100");
        jumpCodes.put("JNE",  "101");
        jumpCodes.put("JLE",  "110");
        jumpCodes.put("JMP",  "111");
    }

    /******************
     * Public Methods *
     ******************/

    /**
     * Converts a given comp mnemonic to a string of (7) bits
     *
     * PRECONDITION:    mnemonic comes from the comp of a C-Instruction
     * POSTCONDITION:   N/A
     *
     * @param   mnemonic    -   the comp portion of a C-Instruction
     * @return              -   mnemonic as a string of (7) bits, or null
     */
    public String getComp(String mnemonic) {
        return compCodes.get(mnemonic);
    }

    /**
     * Converts a given dest mnemonic to a string of (3) bits
     *
     * PRECONDITION:    mnemonic comes from the dest of a C-Instruction
     * POSTCONDITION:   N/A
     *
     * @param   mnemonic    -   the dest portion of a C-Instruction
     * @return              -   mnemonic as a string of (3) bits, or null
     */
    public String getDest(String mnemonic) {
        return destCodes.get(mnemonic);
    }

    /**
     * Converts a given jump mnemonic to a string of (3) bits
     *
     * PRECONDITION:    mnemonic comes from the jump of a C-Instruction
     * POSTCONDITION:   N/A
     *
     * @param   mnemonic    -   the jump portion of a C-Instruction
     * @return              -   mnemonic as a string of (3) bits, or null
     */
    public String getJump(String mnemonic) {
        return jumpCodes.get(mnemonic);
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
}
