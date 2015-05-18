package me.kevingleason.szip;//File: me.kevingleason.szip.Puff.java
//Author: Kevin Gleason (with Andrew Francl)
//Date: 4/5/14
//Use: File decompression.

import me.kevingleason.szip.adt.*;
import me.kevingleason.szip.io.BinaryIn;
import me.kevingleason.szip.io.FileIO;
import me.kevingleason.szip.io.FileIOC;
import me.kevingleason.szip.util.Util;

import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.zip.DataFormatException;


public class Puff {
    //Instance Variables
    private FileIO io = new FileIOC();
    private BinaryIn input;

    private SymTable<Integer, TableValue> st = new SymTableC<Integer, TableValue>();

    public Puff(String input){
        this.input = io.openBinaryInputFile(input);
    }

    //Need to:
    //  Open the File
    //  Check magic number (2 byte)
    //  Read 4byte size of table
    //  Reconstruct me.kevingleason.szip.adt.SymTable > me.kevingleason.szip.adt.HuffTree
    //  Run through tree using binary to write letters

    private void puffAway() throws IOException, DataFormatException {
        //me.kevingleason.szip.io.BinaryIn in = this.openBinFile();
        //assert checkMagic(in);
        Util.checkMagic(input);
        int tableSize = input.readInt();
        System.out.println("Table Size: " + tableSize);
        makePuffTable(tableSize);
        HuffTree tree = Util.makeHuffTree(st);
        //System.out.println(tree.toString());   //DEBUG
        tree.writeText(this.io, this.input);


    }

    private void makePuffTable(int tableSize){
        for (int i = 0; i < tableSize; i++) {
            //System.out.println("Char: " + this.input.readByte() + " Freq " + this.input.readInt());
            st.put((int) this.input.readByte(), new TableValueC(this.input.readInt()));

        }
        st.toStringFreq();
    }

    private HuffTree makeHTree(SymTable<Integer, TableValue> st) {
        Set<Integer> STKeys = st.getKeys();
        PriorityQueue<HuffTree> freqQueue = new PriorityQueue<HuffTree>();
        for (Integer key : STKeys) {
            //Need push everything in to a PQ
            int freq = st.get(key).getFrequency();
            char c = (char) key.intValue();
            if (c != ' ')
                freqQueue.add(new HuffTreeC(c, freq, null, null));
        }
        while (freqQueue.size() > 1) {
            HuffTree p1 = freqQueue.poll();
            HuffTree p2 = freqQueue.poll();
            int nodeWeight = p1.getWeight() + p2.getWeight();
            freqQueue.add(new HuffTreeC((char) 0, nodeWeight, p1, p2));
        }
        HuffTree root = freqQueue.poll();
        root.setWeight(root.getWeight() + st.get((int) ' ').getFrequency());
        root.makeBitPattern(0, 0, this.st); //Make the me.kevingleason.szip.adt.BitPattern on the me.kevingleason.szip.adt.SymTable
        int minUnique = Util.getMinUniqueZero(st, root);
        System.out.println(root);
        Util.modifyHuffTree(root, minUnique - 1);
        System.out.println(root);
        System.out.println(st.getKeys());
        root.makeBitPattern(0, 0, this.st);
        st.toStringCodes();
        return root;
    }


    public static void main(String[] args) throws IOException, DataFormatException {
        //new me.kevingleason.szip.Puff("test.zip").puffAway(); //for use in IntelliJ
//        new Puff(args[0]).puffAway();
        new Puff("src/test.zip").puffAway();
    }
}
