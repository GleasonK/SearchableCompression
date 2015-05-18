package me.kevingleason.szip.adt;//File: me.kevingleason.szip.adt.TableValue.java
//Author: Kevin Gleason
//Date: 3/25/14
//Use: Interface for handling me.kevingleason.szip.adt.SymTable values

import me.kevingleason.szip.adt.BitPattern;

public interface TableValue {
    public BitPattern getBits();
    public int getFrequency();
    public void setBits(BitPattern bit);
    public void setFrequency(int freq);
}
