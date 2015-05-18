package me.kevingleason.szip.util;

import me.kevingleason.szip.Huff;
import me.kevingleason.szip.adt.*;
import me.kevingleason.szip.io.BinaryIn;

import java.util.PriorityQueue;
import java.util.Set;
import java.util.zip.DataFormatException;

/**
 * Created by GleasonK on 5/15/15.
 */
public class Util {

    public static final int MAGIC_NUM = 0x0BC0;

    /**
     * Tells you the minimum number of 0s needed for a unique space character in a file.
     * @param st
     * @return
     */
    public static int getMinUniqueZero(SymTable<Integer, TableValue> st, HuffTree tree){
        Set<Integer> STKeys = st.getKeys();
        int maxDiv = 0;
        int bitLen, val;
        // Get the bottom left, most preceeding zeros
        while(tree.getCh() == 0)
            tree = tree.getLeft();
        BitPattern bottomLeft = st.get((int) tree.getCh()).getBits();

        for (Integer key1 : STKeys){
            if ((char)(int)key1 == ' ') continue;
            BitPattern val2 = st.get(key1).getBits();
            bitLen = bottomLeft.getLength() + val2.getLength();
            val = (val2.getBit() << bottomLeft.getLength()) | bottomLeft.getBit();
            while(maxDiv <= bitLen){
                while(val % (1 << maxDiv) == 0 && maxDiv <= bitLen){
                    maxDiv++;
                }
                bitLen--;
                val >>= 1;
            }
        }
        return maxDiv;
    }

    public static void modifyHuffTree(HuffTree node, int minUniqueZero){
        HuffTree curr = node;
        HuffTree left, leaf;
        while(!curr.getLeft().isLeaf()){
            curr = curr.getLeft();
            minUniqueZero--;
        }
        left = curr.getLeft();
        leaf = new HuffTreeC((char)0, left.getWeight(), null, left);
        curr.setLeft(leaf);
        while(minUniqueZero > 0){
            curr = leaf;
            leaf = new HuffTreeC((char)0, -1, null, null);
            curr.setLeft(leaf);
            minUniqueZero--;
        }
        curr.setLeft(new HuffTreeC(' ', -2, null, null));
    }

    /**
     * Ignore space character from the symbol table, it will be refactored in later.
     * Make the Huffman Tree in the conventional manner until the size of the PQ == 1.
     * Then modify the tree to account for the case of spaces.
     * @return
     */
    public static HuffTree makeHuffTree(SymTable<Integer, TableValue> st) {
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
        root.makeBitPattern(0, 0, st);
        int minUnique = Util.getMinUniqueZero(st, root);
        Util.modifyHuffTree(root, minUnique - 1);
        root.makeBitPattern(0, 0, st);
        return root;
    }

    public static boolean checkMagic(BinaryIn inputFile) throws DataFormatException {
        int mNum = inputFile.readShort(); // 8bit = 1byte
        int a = 0x0BC0;
        if (mNum == MAGIC_NUM) return true;
        else throw new DataFormatException("Magic Number did not match! Data Corrupt.");
    }

}
