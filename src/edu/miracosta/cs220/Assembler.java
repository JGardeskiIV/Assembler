/************************************************************************************
 *
 * Class name:    SymbolTable
 * Package:       edu.miracosta.cs220
 * Description:   Uses a symbol table as a HashMap to account for predefined and
 *                 programmer-defined assembly symbols.
 *
 * History:       Mar. 4, J, author, taken from Lab #6 UML & Method Layout
 *				  Mar. 7, J, updated documentation & basic error handling
 *
 * Methods:       Public:   SymbolTable(), addEntry(String, int), contains(String),
 *                          getAddress(String)
 *
 *                Private:  isValidName(String)
 *
 ************************************************************************************/
package edu.miracosta.cs220;

import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.PrintWriter;

public class Assembler {

    /**********************
     * Instance Variables *
     **********************/
    private static SymbolTable symbolTable;

    /**
     * Handles the translation of .asm source code to .hack binary code.
     * -	gets .asm input file and creates corresponding .hack output file
     * - 	accounts for symbols and performs asm to binary translation
     * - 	reports found errors to the user or outputs a message of completion
     *
     * PRECONDITION:    a file may have been entered from the command line
     * POSTCONDITION:   a XXX.hack file has been generated in the same directory as
     *                  the provided XXX.asm file
     *
     * @param	args	-	the supplied command-line arguments
     */
    public static void main(String[] args) {

        String inputFileName, outputFileName;

        //  get input file name from command line or console input
        if(args.length == 1) {
            System.out.println("command line arg = " + args[0]);
            inputFileName = args[0];
        } else {
            Scanner keyboard = new Scanner(System.in);

            System.out.println("Please enter assembly file name you would like to assemble.");
            System.out.println("Don't forget the .asm extension: ");
            inputFileName = keyboard.nextLine();

            keyboard.close();
        }

        outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + ".hack";

        try {
            PrintWriter outputFile = new PrintWriter(new FileOutputStream(outputFileName));

            symbolTable = new SymbolTable();
            firstPass(inputFileName);
            secondPass(inputFileName, outputFile);
            outputFile.close();
            System.out.println("Compilation successful");

        } catch (FileNotFoundException ex) {
            reportError("Could not open output file " + outputFileName,
                        "Run program again, make sure you have write permissions, etc.");
        }
    }

    /**
     * Move through the .asm file a first time to add valid label declarations
     * and their corresponding line to symbolTable.
     *
     * PRECONDITION:	an .asm file name to be opened has been obtained
     * POSTCONDITION:	symbolTable contains all label declarations &
     *					inputFileName is ready for translation OR
     *					an error has been reported to the user and the
     *					program has ended
     * @param	inputFileName	-	the name of the .asm file to read from
     */
    private static void firstPass(String inputFileName) {
        String symbol;
        int romAddress = 0;
        try {
            Parser parser = new Parser(inputFileName);

            while (parser.hasMoreCommands()) {
                parser.advance();
                Parser.Command commandType = parser.getCommandType();
                switch (commandType) {
                    case A_INSTRUCTION:
                    case C_INSTRUCTION:
                        romAddress++;
                        break;
                    case L_INSTRUCTION:
                        symbol = parser.getSymbol();
                        if (!symbolTable.contains(symbol)) {
                            //	If not added, add it, unless invalid -- catch it here!
                            if (!symbolTable.addEntry(symbol, romAddress)) {
                                reportError("In line " + parser.getLineNumber() + ", Invalid symbol", null);
                            }
                        }
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            reportError("Could not open input file " + inputFileName,
                        "Run program again, make sure you type correct file name, etc.");
        }
    }

    /**
     * Move through the .asm source file a second time, processing each
     * line, translating to the binary equivalent, and outputting it to
     * the output .hack file.
     *
     * PRECONDITION:	firstPass() has been completed without errors,
     *					& input and output files have been successfully opened
     * POSTCONDITION:	outputFile contains the translated machine code,
     *					OR the process has halted and an error has been
     *					reported to the user and the program has ended
     *
     * @param	inputFileName	-	the name of the .asm file to read from
     * @param	outputFile		-	the file stream to output to
     */
    private static void secondPass(String inputFileName, PrintWriter outputFile){

        Code code = new Code();
        String comp, dest, jump;	//	Binary holders for error checks
        StringBuilder binaryOut;
        String symbol;
        int ramAddress = 16;

        try {
            Parser parser = new Parser(inputFileName);

            while (parser.hasMoreCommands()) {
                binaryOut = new StringBuilder();
                parser.advance();
                Parser.Command commandType = parser.getCommandType();
                switch (commandType) {
                    case A_INSTRUCTION:
					/*	TODO - 	End of line expected
					 * 		 -	non '@', symbol chars
					 */
                        int num;
                        symbol = parser.getSymbol();
                        try {
                            num = Integer.parseInt(symbol);
                            if (num < 0) {
                                reportError("In line " + parser.getLineNumber() + ", Negative integer not allowed", null);
                            }
                            binaryOut.append(code.decimalToBinary(num));
                        } catch (NumberFormatException e) {
                            //	Symbol added?
                            if (symbolTable.contains(symbol)) {
                                binaryOut.append(code.decimalToBinary(symbolTable.getAddress(symbol)));
                            } else {
                                //	Add symbol unless it's invalid
                                if (!symbolTable.addEntry(symbol, ramAddress)) {
                                    reportError("In line " + parser.getLineNumber() + ", Invalid symbol", null);
                                }
                                binaryOut.append(code.decimalToBinary(ramAddress++));
                            }
                        }
                        outputFile.write(binaryOut.toString() + "\n");
                        break;
                    case C_INSTRUCTION:
                        binaryOut.append("111");
                        comp = code.getComp(parser.getComp());
                        dest = code.getDest(parser.getDest());
                        jump = code.getJump(parser.getJump());
                        //	Valid codes for each part?
                        if (comp == null) {
                            reportError("In line " + parser.getLineNumber() + ", Invalid comp code", null);
                        }
                        if (dest == null) {
                            reportError("In line " + parser.getLineNumber() + ", Invalid dest code", null);
                        }
                        if (jump == null) {
                            reportError("In line " + parser.getLineNumber() + ", Invalid jump code", null);
                        }
                    /*	TODO 	-	End of line expected	-
                     * 		sum of comp/dest/jump mnemonics (if valid)
                     * 		AND '=' and/or ';' == cleanLine.length()?
                     * 	-> error?
                     */
                        binaryOut.append(comp);
                        binaryOut.append(dest);
                        binaryOut.append(jump);
                        outputFile.write(binaryOut.toString() + "\n");
                        break;
                    case L_INSTRUCTION:
					/*	TODO - 	Unexpected end of line [missing ')']
					 *		 -	End of line expected [non '(', ')', symbol chars]
					 */
                        break;
                    case N_INSTRUCTION:
                        //	If not an empty string, catch all for "something's wrong"
                        if (parser.getCleanLine().length() > 0) {
                            reportError("In line " + parser.getLineNumber() + ", Expression expected", null);
                        }
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            reportError("Could not open input file " + inputFileName,
                        "Run program again, make sure you type correct file name, etc.");
        }

    }

    /**
     * Reports a translation error (and guidance/solution, if desired)
     * to the user and ends the program.
     *
     * PRECONDITION:	there is an error to report to the user (error is not null)
     * POSTCONDITION:	the error has been reported to the user & the program has ended
     *
     * @param	error	-	the error message to report to the user (not null)
     * @param	hint	-	a solution or guidance regarding the cause of the error,
     *						if desired
     */
    private static void reportError(String error, String hint) {
        System.err.println(error);
        if (hint != null && !hint.equals("")) {
            System.err.println(hint);
        }
        System.exit(0);
    }
}