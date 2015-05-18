package me.kevingleason.szip;

import me.kevingleason.szip.adt.*;
import me.kevingleason.szip.io.BinaryIn;
import me.kevingleason.szip.io.FileIO;
import me.kevingleason.szip.io.FileIOC;
import me.kevingleason.szip.util.Util;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.DataFormatException;

/**
 * Created by GleasonK on 5/15/15.
 */
public class Search {
    private FileIO io = new FileIOC();
    private BinaryIn input;
    private String fname;
    private String query;
    private String[] encodedQuery;
    private List<String> matches;

    private static final int DECODE_THRESHOLD = 5;

    private SymTable<Integer, TableValue> st = new SymTableC<Integer, TableValue>();

    public Search(String input, String query){
        this.input = io.openBinaryInputFile(input);
        this.fname = input;
        this.query = query;
    }

    private List<String> performSearch() throws IOException, DataFormatException {
        Util.checkMagic(input);
        int tableSize = input.readInt();
        makeQueryTable(tableSize);
        HuffTree tree = Util.makeHuffTree(st);
        if (!checkPossible())
            return new ArrayList<String>();
        encodeQuery();
        String contents = readFile(input, tree.getWeight());
        this.matches = decodeMatches(contents, tree);
        return this.matches;
    }

    private void makeQueryTable(int tableSize){
        for (int i = 0; i < tableSize; i++)
            st.put((int) this.input.readByte(), new TableValueC(this.input.readInt()));
    }

    private boolean checkPossible(){
        for (char c : this.query.toCharArray()){
            if (!st.containsKey((int)c)) // if unseen character then query not in file.
                return false;
        }
        return true;
    }

    private void encodeQuery(){
        StringBuilder sb = new StringBuilder();
        char[] queryChars = this.query.toCharArray();
        for (int i = 0; i < this.query.length(); i++) {
            BitPattern bp = this.st.get((int)queryChars[i]).getBits();
            int bits = bp.getBit();
            String bitVal = "";
            for (int j = 0; j < bp.getLength(); j++) {
                bitVal =  String.valueOf(bits % 2) + bitVal;
                bits >>= 1;
            }
            sb.append(bitVal);
        }
        String boundary = "";
        for (int i = 0; i < st.get((int)' ').getBits().getLength(); i++)
            boundary+="0";
        String encoded = sb.toString();
        this.encodedQuery = new String[2];
        this.encodedQuery[0] = encoded.startsWith(boundary) ? encoded : boundary + encoded; //Word boundary before
        this.encodedQuery[1] = encoded.endsWith(boundary) ? encoded : encoded + boundary;   //Word boundary after
    }

    private List<String> decodeMatches(String contents, HuffTree tree) {
        List<String> matches = new ArrayList<String>();
        int index;
        for (int i = 0; i < 2; i++) {
            while ((index = contents.indexOf(this.encodedQuery[i])) != -1) {
                String match = decode(contents.substring(index + this.encodedQuery[i].length()), tree);
                match = "..." + this.query + (i==0 ? "" : " ") + match + "...";
                matches.add(match);
                contents = contents.replaceFirst(this.encodedQuery[i], "");
            }
        }
        return matches;
    }

    private String decode(String match, HuffTree tree){
        StringBuilder sb = new StringBuilder();
        int words = 0;
        int i=0;
        HuffTree temp = tree;
        char ch;
        while (words < DECODE_THRESHOLD){
            while ((ch = temp.getCh()) == 0){
                if (match.charAt(i)=='0') temp = temp.getLeft();
                else temp = temp.getRight();
                i++;
            }
            if (ch == ' ')
                words++;
            else if (ch == '\n')
                break;
            sb.append(ch);

            temp = tree;
        }
        return sb.toString();
    }

    private String readFile(BinaryIn binFile, int size) throws IOException {
        int c;
        StringBuilder sb = new StringBuilder();
        while (!binFile.isEmpty()) {
            sb.append(String.valueOf(binFile.readInt(1)));
        }
        return sb.toString();
    }

    public static void main(String[] args) throws IOException, DataFormatException {
        /*
        if (args.length < 2) {
            System.err.println("Usage: java Search <file.zip> <search-term>");
            System.exit(0);
        }
        String file = args[0];
        String query = args[1];
        matches = new Search(file, query).performSearch();
        */
        String query = "hi";
        List<String> matches = new Search("src/test.zip", query).performSearch();
        System.out.println("Matches:");
        for (String s : matches)
            System.out.println(s);
    }
}
