package me.kevingleason.szip.adt;//File: me.kevingleason.szip.adt.BitPatternC.java
//Author: Kevin Gleason
//Date: 3/29/14
//Use: implementation of me.kevingleason.szip.adt.BitPattern interface

public class BitPatternC implements BitPattern {
    
    private int bits;
    private int size;
    
    public BitPatternC(int bits, int size) {
        this.bits = bits;
        this.size = size;
    }
    
    public int getLength(){
        return size; 
    }

    public int getBit(){ return this.bits; }

    @Override
    public String toString() {
        return "bits=" + this.bits + " len=" +  this.size;
    }
}