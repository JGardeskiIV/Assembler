/************************************************************************************
 *
 * Class name:    Parser
 * Package:       edu.miracosta.cs220
 * Description:   A file-processing class that takes an .asm file as input, tracks
 *                movement throughout the file, and breaks down each line of assembly
 *                code into its corresponding parts.
 *
 * History:       Mar. 4, J, author, taken from Lab #6 UML & Method Layout
 *
 * Methods:       Public:   Parser(String), hasMoreCommands(), advance()
 *                          getCommandType(), getCommandTypeString()
 *                          getSymbol(), getDest(), getComp(), getJump(),
 *                          getRawLine(), getCleanLine(), getLineNumber()
 *
 *                Private:  cleanLine(), parseCommandType(), parse(),
 *                          parseSymbol(), parseDest(), parseComp(), parseJump()
 *
 * Notes:
 *
 ************************************************************************************/
package edu.miracosta.cs220;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Parser {

    /*************
     * Constants *
     *************/
    public enum Command {
        A_INSTRUCTION,
        C_INSTRUCTION,
        L_INSTRUCTION,
        N_INSTRUCTION
    }

    /**********************
     * Instance Variables *
     **********************/

    //  File Management & Debugging
    private Scanner inputFile;
    private int lineNumber;
    private String rawLine;

    //  Parsed Command Parts
    private String cleanLine;
    private Command commandType;
    private String symbol;
    private String compMnemonic;
    private String destMnemonic;
    private String jumpMnemonic;

    /****************
     * Constructors *
     ****************/

    /**
     * Opens input file/stream and prepares to parse it.
     *
     * PRECONDITION:    a file name to be opened has been collected/received
     * POSTCONDITION:   the file has been opened and is reaady for parsing,
     *                  or a FileNotFoundException has been thrown
     * @param   inFileName  -   the name of the file to be opened
     */
    public Parser(String inFileName) throws FileNotFoundException {
        //  File Constructor can throw a NullPointerException if inFileName = null
        if (inFileName != null) {
            inputFile = new Scanner(new File(inFileName));
            lineNumber = 0;
            rawLine = "";
            cleanLine = "";
            commandType = Command.N_INSTRUCTION;
            symbol = "";
            compMnemonic = "";
            destMnemonic = "";
            jumpMnemonic = "";
        } else {
            throw new FileNotFoundException();
        }
    }

    /***********************
     * Public File Methods *
     ***********************/

    /**
     * Returns a boolean of whether or not more commands are left in the file.
     * If there are no more commands left, the stream is closed.
     *
     * PRECONDITION:    the file stream has been opened
     * POSTCONDITION:   the file is ready for more reading (true) OR
     *                  the file has been closed (false)
     *
     * @return      -   true if there are more commands to be read, false otherwise
     */
    public boolean hasMoreCommands() {
        //  Scanner's hasNextLine() throws an IllegalStateException if the scanner is closed. Check this first.
        try {
            if (inputFile.hasNextLine()) {
                return true;
            } else {
                //  No lines left & inputFile is still open -> close it.
                inputFile.close();
                return false;
            }
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * Reads next line from file and parses it into instance variables.
     *
     * PRECONDITION:    there are more lines in the file to parse (check with hasMoreCommands() first)
     * POSTCONDITION:   the next instruction has been read in and cleaned, and the parse method is
     *                  breaking the instruction into its necessary parts
     */
    public void advance() {
        lineNumber++;
        //  Read the line in
        rawLine = inputFile.nextLine();
        //  Clean it up
        cleanLine = cleanLine(rawLine);
        //  Break it down
        parse();
    }

    /******************
     * Helper Methods *
     ******************/

    /**
     * Cleans a raw instruction by removing non-essential parts
     *
     * PRECONDITION:    rawLine contains a line of assembly code and is not null
     * POSTCONDITION:   returns rawLine with no whitespace or comments anywhere in the instruction
     *
     * @param   rawLine     -   a non-null line of assembly code
     * @return  cleanedLine -   the instruction with no whitespace or comments
     */
    private String cleanLine(String rawLine) {
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
     * Determines the command type of the instruction from the parameter
     *
     * PRECONDITION:    clean has no whitespace or comments
     * POSTCONDITION:   returns the command type of the parameter as a Command:
     *                  A_INST, C_INST, L_INST, N_INST
     * @param   clean   -   an instruction with no whitespace or comments
     * @return          -   the command type of the parameter, as a Command
     */
    private Command parseCommandType(String clean) {
        if (clean == null || clean.equals("")) {
            return Command.N_INSTRUCTION;
        } else {
            char first = clean.charAt(0);
            if (first == '@') {
                return Command.A_INSTRUCTION;
            } else if (clean.contains(";") || clean.contains("=")) {
                return Command.C_INSTRUCTION;
            } else if (first == '(' && clean.charAt(clean.length() - 1) == ')') {
                return Command.L_INSTRUCTION;
            } else {
                return Command.N_INSTRUCTION;
            }
        }
    }

    /**
     * Parses the line depending on the instruction type.
     *
     * PRECONDITION:    a new instruction has been read in -> advance() has been called
     * POSTCONDITION:   commandType has been determined, and the corresponding helper parse
     *                  method(s) are in the process of populating relevant instance variables
     */
    private void parse() {
        commandType = parseCommandType(cleanLine);
        switch(commandType) {
            case N_INSTRUCTION:
                break;
            case A_INSTRUCTION:
            case L_INSTRUCTION:
                parseSymbol();
                break;
            case C_INSTRUCTION:
                parseComp();
                parseDest();
                parseJump();
                break;
        }
    }

    /**
     * Parses symbol for A- or L- Instructions.
     *
     * PRECONDITION:    the command is an A- or L- command
     * POSTCONDITION:   the symbol instance variable is populated with the symbol in the command
     */
    private void parseSymbol() {
        //  Get rid of the '@' or the '('
        //  TODO Does this need to be error-checked? If so, what happens if the '@', '(', or ')' is missing?
        symbol = cleanLine.substring(1);
        if (commandType == Command.L_INSTRUCTION) {
            symbol = symbol.substring(0, symbol.length() - 1);
        }
    }

    /**
     * Parses line to get the comp part of a C-Instruction
     *
     * PRECONDITION:    the command is a C- command
     * POSTCONDITION:   compMnemonic has been populated with the comp portion of the command
     */
    private void parseComp() {
        //  TODO - Does this need to be error-checked? what if semi-colon at the end? Or '=' or ';' is missing?
        int eqIndex = cleanLine.indexOf('=');
        int scIndex = cleanLine.indexOf(';');
        if (eqIndex != -1) {
            if (scIndex == -1) {
                //  dest=comp
                compMnemonic = cleanLine.substring(eqIndex + 1);
            } else {
                //  dest=comp;jmp
                compMnemonic = cleanLine.substring(eqIndex + 1, scIndex);
            }
        } else {
            //  comp;jmp
            compMnemonic = cleanLine.substring(0, scIndex);
        }
    }

    /**
     * Parses the line to get the dest part of a C-Instruction
     *
     * PRECONDITION:    the command is a C- command
     * POSTCONDITION:   destMnemonic has been populated with the dest portion of the command
     */
    private void parseDest() {
        int eqIndex = cleanLine.indexOf('=');
        if (eqIndex != -1) {
            destMnemonic = cleanLine.substring(0, eqIndex);
        } else {
            destMnemonic = "null";
        }
    }

    /**
     * Parses the line to get the jump part of a C-Instruction
     *
     * PRECONDITION:    the command is a C- command
     * POSTCONDITION:   jumpMnemonic has been populated with the jump portion of the command
     */
    private void parseJump() {
        int scIndex = cleanLine.indexOf(';');
        if (scIndex != -1) {
            jumpMnemonic = cleanLine.substring(scIndex + 1);
        } else {
            jumpMnemonic = "null";
        }
    }

    /***********
     * Getters *
     ***********/

    /**
     * Getter for command type.
     *
     * PRECONDITION:    the type has already been determined (advance() has been called -> parse() -> parseCommandType())
     * POSTCONDITION:   N/A
     *
     * @return      -   the command type as a Command
     */
    public Command getCommandType() {
        return commandType;
    }

    /**
     * Getter for symbol name.
     *
     * PRECONDITION:    the command is an A-Instruction that has been parsed (advance() & check w/ getCommandType())
     * POSTCONDITION:   N/A
     *
     * @return      -   the symbol name as a String
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Getter for the comp part of a C-Instruction.
     *
     * PRECONDITION:    the command is a C-Instruction that has been parsed (advance() & check w/ getCommandType())
     * POSTCONDITION:   N/A
     *
     * @return      -   the comp part as a String
     */
    public String getComp() {
        return compMnemonic;
    }

    /**
     * Getter for the dest part of a C-Instruction.
     *
     * PRECONDITION:    the command is a C-Instruction that has been parsed (advance() & check w/ getCommandType())
     * POSTCONDITION:   N/A
     *
     * @return      -   the dest part as a String
     */
    public String getDest() {
        return destMnemonic;
    }

    /**
     * Getter for the jump part of a C-Instruction.
     *
     * PRECONDITION:    the command is a C-Instruction that has been parsed (advance() & check w/ getCommandType())
     * POSTCONDITION:   N/A
     *
     * @return      -   the jump part as a String
     */
    public String getJump() {
        return jumpMnemonic;
    }

    /**
     * Getter for String version of command type (Debugging).
     *
     * PRECONDITION:    the type has already been determined (advance() has been called -> parse() -> parseCommandType())
     * POSTCONDITION:   N/A
     *
     * @return      -   the command type as a String
     */
    public String getCommandTypeString() {
        return commandType.toString();
    }

    /**
     * Getter for rawLine from the file (Debugging).
     *
     * PRECONDITION:    rawLine has been populated with a line from the file (advance() has been called)
     * POSTCONDITION:   N/A
     *
     * @return      -   rawLine
     */
    public String getRawLine() {
        return rawLine;
    }

    /**
     * Getter for cleanLine from the file (Debugging).
     *
     * PRECONDITION:    cleanLine has been populated with a cleaned line from the file (advance() has been called)
     * POSTCONDITION:   N/A
     *
     * @return      -   cleanLine
     */
    public String getCleanLine() {
        return cleanLine;
    }

    /**
     * Getter for lineNumber (Debugging).
     *
     * PRECONDITION:    lineNumber represents the current line being parsed (advance() has been called)
     * POSTCONDITION:   N/A
     *
     * @return      -   lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }
}
