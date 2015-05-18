package me.kevingleason.szip.adt;//File: me.kevingleason.szip.adt.HuffTreeC.java
//Author: Kevin Gleason
//Date: 3/29/14
//Use: The me.kevingleason.szip.adt.HuffTree interface

import me.kevingleason.szip.Search;
import me.kevingleason.szip.adt.BitPatternC;
import me.kevingleason.szip.adt.SymTable;
import me.kevingleason.szip.adt.TableValue;
import me.kevingleason.szip.io.BinaryIn;
import me.kevingleason.szip.io.FileIO;

import java.io.FileWriter;
import java.io.IOException;

public class HuffTreeC implements HuffTree, Comparable {
    //Instance Variables. Need weight/frequency/left/right
    private int weight;
    private char ch;
    private HuffTree left;
    private HuffTree right;
    private HuffTree root;

    public HuffTreeC(char ch, int weight, HuffTree left, HuffTree right){
        this.ch = ch;
        this.weight=weight;
        this.left=left;  //Use null or class empty??
        this.right=right; //Use null or class empty??
    }

    public int getWeight(){ return this.weight; }
    public void setWeight(int weight){ this.weight=weight; }
    public char getCh(){ return this.ch; }
    public void setLeft(HuffTree node){ this.left = node; }
    public HuffTree getLeft(){ return this.left; }
    public HuffTree getRight(){ return this.right; }
    public boolean isLeaf(){ return (this.getCh() != 0); }

    //Require a me.kevingleason.szip.adt.SymTable and me.kevingleason.szip.adt.TableValue to handle unchecked type-cast warnings
    public void makeBitPattern(int s, int r, SymTable<Integer, TableValue> st){
        if (getCh() == 0 && getLeft() != null)  { this.getLeft().makeBitPattern(s << 1, r+1,  st); }
        if (getCh() == 0 && getRight() != null) { this.getRight().makeBitPattern(s << 1 | 1,r+1, st ); }
        if (getCh() != 0) {
            char key = getCh();
            TableValue val = st.get((int) key);
            val.setBits(new BitPatternC(s, r));
        }
    }

    //Need to do recursively
    public char checkLeaf(BinaryIn in){
        char tempCh = this.getCh();
        if (tempCh != 0) return tempCh;
        else if (in.readInt(1) == 0) return this.getLeft().checkLeaf(in);
        return this.getRight().checkLeaf(in);
    }

    public void writeText(FileIO io, BinaryIn in) throws IOException {
        FileWriter out = io.openOutputFile();
        //System.out.println("TREE: " + this); //DEBUG
        System.out.println("Writing...");    //DEBUG
        for (int i = 0; i < this.getWeight(); i++) {
            char letter = this.checkLeaf(in);
            //System.out.println("Writing: " + letter); //DEBUG
            out.write(letter);
        }
        out.close();
        System.out.println("Wrote " + this.getWeight() + " characters to output."); //DEBUG
    }

    ///////////////////////////////////////
    //Complete with private static function
    ///////////////////////////////////////

//    public void writeOut(me.kevingleason.szip.io.BinaryIn in, me.kevingleason.szip.io.FileIO io) throws IOException{
//        FileWriter out = io.openOutputFile();
//        System.out.println("TREE CHAR: " + this);
//        for (int i = 0; i < this.getWeight(); i++) {
//            char letter = me.kevingleason.szip.adt.HuffTreeC.checkLeaf(in, this);
//            System.out.println("Writing: " + letter);
//            out.write(letter);
//        }
//        out.close();
//    }
//
//    private static char checkLeaf(me.kevingleason.szip.io.BinaryIn in, me.kevingleason.szip.adt.HuffTree tree){
//        char tempCh = tree.getCh();
//        if (tempCh != 0) return tempCh;
//        else if (in.readInt(1) == 0) return checkLeaf(in, tree.getLeft());
//        return checkLeaf(in, tree.getRight());
//    }


    public int compareTo(Object otherTree) {
        HuffTree other = (HuffTree) otherTree;
        return this.getWeight() - other.getWeight();
    }
    public String toString() {
        String l;
        String r;
        if(this.ch != 0) return this.ch + ":" + this.weight;


        if (this.getLeft() == null) l = "";
        else l = this.getLeft().toString();

        if (this.getRight() == null) r = "";
        else r = this.getRight().toString();

        return "Node(" + this.weight + ", " + l + ", " + r + ")";
    }
}
