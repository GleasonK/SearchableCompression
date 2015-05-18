package me.kevingleason.szip.adt;//File: me.kevingleason.szip.adt.HuffTree.java
//Author: Kevin Gleason
//Date: 3/29/14
//Use: The me.kevingleason.szip.adt.HuffTree interface

import me.kevingleason.szip.adt.SymTable;
import me.kevingleason.szip.adt.TableValue;
import me.kevingleason.szip.io.BinaryIn;
import me.kevingleason.szip.io.FileIO;

import java.io.IOException;

public interface HuffTree{ // extends Comparable {

    public int getWeight();
    public void setWeight(int weight);

    public char getCh();
    public char checkLeaf(BinaryIn in);
    public void setLeft(HuffTree node);
    public HuffTree getLeft();
    public HuffTree getRight();
    public void writeText(FileIO io, BinaryIn in) throws IOException;
    public void makeBitPattern(int s, int r, SymTable<Integer, TableValue> st);
    public boolean isLeaf();
}
