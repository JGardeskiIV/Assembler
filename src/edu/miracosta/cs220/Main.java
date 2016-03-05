package edu.miracosta.cs220;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {
        Parser parser = null;
        try {
            parser = new Parser("Test.asm");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Code code = new Code();
        SymbolTable symbolTable = new SymbolTable();

        while(parser.hasMoreCommands()) {
            System.out.println("lineNumber before advancing: " + parser.getLineNumber());
            parser.advance();
            System.out.println("rawLine: " + parser.getRawLine());
            System.out.println("cleanLine: " + parser.getCleanLine());
            Parser.Command type = parser.getCommandType();
            System.out.println("commandType: " + parser.getCommandTypeString());
            switch(type) {
                case A_INSTRUCTION:
                case L_INSTRUCTION:
                    System.out.println("symbol: " + parser.getSymbol());
                    break;
                case C_INSTRUCTION:
                    System.out.println("compMnemonic: " + parser.getComp());
                    System.out.println("destMnemonic: " + parser.getDest());
                    System.out.println("jumpMnemonic: " + parser.getJump());
                    break;
                case N_INSTRUCTION:
                    break;
            }
            System.out.println("lineNumber after advancing: " + parser.getLineNumber());
            System.out.println();
        }
    }
}
