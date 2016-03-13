/************************************************************************************
 *
 * Class name:    SymbolTable
 * Package:       edu.miracosta.cs220
 * Description:   Uses a symbol table as a HashMap to account for predefined and
*                 programmer-defined assembly symbols.
 *
 * History:       Mar. 4, J, author, taken from Lab #6 UML & Method Layout
 *
 * Methods:       Public:   SymbolTable(), addEntry(String, int), contains(String),
 *                          getAddress(String)
 *
 *                Private:  isValidName(String)
 *
 ************************************************************************************/
package edu.miracosta.cs220;

import java.util.HashMap;

public class SymbolTable {

    /*************
     * Constants *
     *************/
    private static final String INITIAL_VALID_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ$_.:";
    private static final String ALL_VALID_CHARS = INITIAL_VALID_CHARS + "0123456789";

    /**********************
     * Instance Variables *
     **********************/
    private HashMap<String, Integer> symbolTable;

    /****************
     * Constructors *
     ****************/

    /**
     * Initializes symbolTable hashmap with predefined symbols.
     *
     * PRECONDITION:    N/A
     * POSTCONDITION:   symbolTable has lookups for all valid predefined symbols
     */
    public SymbolTable() {
        symbolTable = new HashMap<>();
        //  Virtual Registers
        symbolTable.put("R0", 0);
        symbolTable.put("R1", 1);
        symbolTable.put("R2", 2);
        symbolTable.put("R3", 3);
        symbolTable.put("R4", 4);
        symbolTable.put("R5", 5);
        symbolTable.put("R6", 6);
        symbolTable.put("R7", 7);
        symbolTable.put("R8", 8);
        symbolTable.put("R9", 9);
        symbolTable.put("R10", 10);
        symbolTable.put("R11", 11);
        symbolTable.put("R12", 12);
        symbolTable.put("R13", 13);
        symbolTable.put("R14", 14);
        symbolTable.put("R15", 15);
        //  VM Control Pointers
        symbolTable.put("SP", 0);
        symbolTable.put("LCL", 1);
        symbolTable.put("ARG", 2);
        symbolTable.put("THIS", 3);
        symbolTable.put("THAT", 4);
        //  I/O Pointers
        symbolTable.put("SCREEN", 16384);
        symbolTable.put("KBD", 24576);
    }

    /******************
     * Public Methods *
     ******************/

    /**
     * Adds a new pair [symbol, address] to the symbolTable hashmap.
     *
     * PRECONDITION:    symbol/address pair is not in symbolTable (check w/ contains first)
     * POSTCONDITION:   returns true if symbol is a valid identifier and was added to the table (false otherwise)
     *
     * @param   symbol  -   an identifier/symbol from an A or L instruction
     * @param   address -   the ROM or RAM address of the symbol
     * @return          -   true if symbol is valid & is added to symbolTable, false otherwise
     */
    public boolean addEntry(String symbol, int address) {
        if (isValidName(symbol)) {
            symbolTable.put(symbol, address);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns boolean of whether symbolTable hashmap has symbol as a key or not.
     *
     * PRECONDITION:    N/A
     * POSTCONDITION:   N/A
     *
     * @param   symbol  -   an identifier/symbol from an A or L instruction
     * @return          -   true if symbolTable contains the symbol, false otherwise
     */
    public boolean contains(String symbol) {
        return symbolTable.containsKey(symbol);
    }

    /**
     * Returns the address in the symbolTable hashmap of the given symbol.
     *
     * PRECONDITION:    symbol exists in symbolTable (check w/ contains first)
     * POSTCONDITION:   N/A
     *
     * @param   symbol  -   an identifier/symbol from an A or L instruction
     * @return          -   the symbol's address as given in the hashmap, or null
     */
    public int getAddress(String symbol) {
        return symbolTable.get(symbol);
    }

    /******************
     * Helper Methods *
     ******************/

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
        if (symbol == null || symbol.length() == 0) {
            return false;
        } else {
            char current = symbol.charAt(0);
            if (INITIAL_VALID_CHARS.indexOf(current) == -1) {
                return false;
            }
            for (int i = 1; i < symbol.length() - 1; i++) {
                current = symbol.charAt(i);
                if (ALL_VALID_CHARS.indexOf(current) == -1) {
                    return false;
                }
            }
            return true;
        }
    }
}
