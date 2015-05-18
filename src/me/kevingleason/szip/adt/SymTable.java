package me.kevingleason.szip.adt;//File: me.kevingleason.szip.adt.SymTable.java
//Author: Kevin Gleason
//Date: 3/30/14
//Use: The me.kevingleason.szip.adt.SymTable interface

import java.util.Set;

public interface SymTable<Key, Value> {
    public int size();
    public Value get(Key k);
    public Set<Key> getKeys();
    public void put(Key k, Value v);
    public boolean containsKey(Key k);
    public void toStringCodes();
    public void toStringFreq();
}
