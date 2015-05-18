package me.kevingleason.szip;//File: me.kevingleason.szip.Huff.java
//Author: Kevin Gleason
//Date: 3/30/14
//Use: The me.kevingleason.szip.Huff program main class for compressing files

import me.kevingleason.szip.adt.*;
import me.kevingleason.szip.io.BinaryOut;
import me.kevingleason.szip.io.FileIO;
import me.kevingleason.szip.io.FileIOC;
import me.kevingleason.szip.util.Util;

import java.io.FileReader;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Set;

public class Huff {
    //Instance Variables
    public static boolean DEBUG = false;
    private final String input;

    //Magic Number

    private FileIO io = new FileIOC();


    private SymTable<Integer, TableValue> st = new SymTableC<Integer, TableValue>();
    private PriorityQueue<HuffTree> HuffPQ;

    public Huff(String input) {
        this.input = input;
    }

    //Executes all the code
    private void huffAway() throws IOException {
        this.makeTable();
        HuffTree tree = Util.makeHuffTree(st);
        System.out.println(tree);
        makeBinary(tree);   //Void function, updates the symbol table
        st.toStringCodes();  //DEBUG codes to string
    }

    private void makeTable() throws IOException {
        FileReader inputFile = io.openInputFile(this.input);
        int temp, freq;
        while ((temp = inputFile.read()) != -1) {
            if (st.containsKey(temp)) {
                freq = st.get(temp).getFrequency();
                st.get(temp).setFrequency(freq + 1);
            } else {
                st.put(temp, new TableValueC(1));
            }
        }
        inputFile.close();
    }

    /**
     * Make the characters bit patterns.
     * Write the Symbol table to the output file first.
     * @param tree
     * @throws IOException
     */
    private void makeBinary(HuffTree tree) throws IOException {
        BinaryOut outFile = io.openBinaryOutputFile();
        outFile.write(Util.MAGIC_NUM,16);
        Set<Integer> STKeys = this.st.getKeys();
        outFile.write(STKeys.size());
        for (Integer i : STKeys) {
            TableValue tv = this.st.get(i);
            char c = (char)((int) i);
            int freqW = tv.getFrequency();
            outFile.write(c);
            outFile.write(freqW);
        }

        FileReader inFile = io.openInputFile(this.input);
        int temp;
        while ((temp = inFile.read()) != -1) {
            TableValue bits = st.get(temp);
            BitPattern bp = bits.getBits();
//            System.out.println((char)temp + " " + bp.toString());
            outFile.write(bp.getBit(), bp.getLength());
        }
        outFile.close();
        inFile.close();

    }

    public static void main(String[] args) throws IOException {
        //Execute compression from Terminal with an argument
//        new me.kevingleason.szip.Huff(args[0]).huffAway();
        new Huff("src/test.txt").huffAway();
    }
}