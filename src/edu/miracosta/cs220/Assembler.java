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

import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.PrintWriter;

public class Assembler {

    private static SymbolTable symbolTable;

    /*  ALGORITHM:
     *  get input file name
     *  create output file name and stream
     *
     *  create symbol table
     *  do first pass to build symbol table (no output yet!)
     *  do second pass to output translated ASM to HACK code
     *
     *  print out "done" message to user
     *  close output file stream
     */
    public static void main(String[] args) {

        String inputFileName, outputFileName;
        PrintWriter outputFile = null;  //  keep compiler happy
        SymbolTable symbolTable = new SymbolTable();
        int romAddress, ramAddress;

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
            outputFile = new PrintWriter(new FileOutputStream(outputFileName));
        } catch (FileNotFoundException ex) {
            System.err.println("Could not open output file " + outputFileName);
            System.err.println("Run program again, make sure you have write permissions, etc.");
            System.exit(0);
        }

        symbolTable = new SymbolTable();
        firstPass(inputFileName);
        secondPass(inputFileName, outputFile);
        outputFile.close();
        System.out.println("Translation completed successfully.");
    }

    //  TODO: march through the source code without generating any code
    /*  for each label declaration (LABEL) that appears in the source code,
     *  add the pair <LABEL, n> to the symbol table
     *  n = romAddress which you should keep track of as you go through each line
     */
    //  HINT: when should rom address increase? What kind of commands?
    private static void firstPass(String inputFileName) {
        Parser parser = null;
        try {
            parser = new Parser(inputFileName);
        } catch (FileNotFoundException e) {
            System.err.println("Could not open input file " + inputFileName);
            System.err.println("Run program again, make sure you type correct file name, etc.");
            System.exit(0);
        }

        String symbol = "";
        int romAddress = 0;

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
                        symbolTable.addEntry(symbol, romAddress);
                    }
                    break;
            }
        }
    }

    //  TODO: march again through the source code and process each line:
    /*  if the line is a c-instruction, simple (translate)
     *  if the line is @xxx where xxx is a number, simple (translate)
     *  if the line is @xxx and xxx is a symbol, look it up in the symbol table and proceed as follows:
     *      If the symbol is found, replace it with its numeric value and complete the commands translation
     *      If the symbol is not found, then it must represent a new variable:
     *          add the pair <xxx, n> to the symbol table, where n is the next available RAM address,
     *          and then complete the command's translation
     */
    //  HINT:   when should rom address increase? What should ram address start at?
    //          When should it increase? What do you do with L commands and No commands?
    private static void secondPass(String inputFileName, PrintWriter outputFile) {
        Parser parser = null;
        try {
            parser = new Parser(inputFileName);
        } catch (FileNotFoundException e) {
            System.err.println("Could not open input file " + inputFileName);
            System.err.println("Run program again, make sure you type correct file name, etc.");
            System.exit(0);
        }

        Code code = new Code();
        StringBuilder binaryOut = new StringBuilder();
        String symbol = "";
        int ramAddress = 16;

        while (parser.hasMoreCommands()) {
            binaryOut = new StringBuilder();
            parser.advance();
            Parser.Command commandType = parser.getCommandType();
            switch (commandType) {
                case A_INSTRUCTION:
                    int num;
                    symbol = parser.getSymbol();
                    try {
                        num = Integer.parseInt(symbol);
                        binaryOut.append(code.decimalToBinary(num));
                    } catch (NumberFormatException e) {
                        if (symbolTable.contains(symbol)) {
                            binaryOut.append(code.decimalToBinary(symbolTable.getAddress(symbol)));
                        } else {
                            symbolTable.addEntry(symbol, ramAddress);
                            System.out.println("Added " + symbol + ", " + ramAddress + " to the table.");
                            binaryOut.append(code.decimalToBinary(ramAddress++));
                        }
                    }
                    outputFile.write(binaryOut.toString() + "\n");
                    break;
                case C_INSTRUCTION:
                    binaryOut.append("111");
                    binaryOut.append(code.getComp(parser.getComp()));
                    binaryOut.append(code.getDest(parser.getDest()));
                    binaryOut.append(code.getJump(parser.getJump()));
                    outputFile.write(binaryOut.toString() + "\n");
                    break;
                case L_INSTRUCTION:
                case N_INSTRUCTION:
                    break;
            }
        }
    }
}
