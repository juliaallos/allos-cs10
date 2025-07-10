import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/* Julia Allos
* CS10 AQL
* Problem Set 3
* May 2 2024
 */

public class Compression implements Huffman{
    @Override
    public Map<Character, Long> countFrequencies(String pathName) throws IOException {
        BufferedReader br;
        Map<Character, Long> result = new HashMap<>(); //going to return
        try { //open file
            br = new BufferedReader(new FileReader(pathName));
        } catch (Exception e) {
            System.out.println(e);
            return result;
        }
        try {
            char s;
            while((s = (char)br.read()) != (char)-1) { //if key is not in map, add it
                if(!result.containsKey(s)) {
                    result.put(s, 1L);
                }
                else { //if key is in map, just adjust the count
                    Long count = result.get(s);
                    result.put(s,count+1L);
                }
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }
        finally { //closing file
            try {
                br.close(); }
            catch (Exception e) {
                System.out.println(e);
            }
            return result;
        }
    }

    @Override
    public BinaryTree<CodeTreeElement> makeCodeTree(Map<Character, Long> frequencies) {
        PriorityQueue<BinaryTree<CodeTreeElement>> result = makeIndividualTrees(frequencies);
        while(result.size() > 1) {
            BinaryTree<CodeTreeElement> s1 = result.remove();
            BinaryTree<CodeTreeElement> s2 = result.remove();
            CodeTreeElement currElement = new CodeTreeElement(s1.getData().getFrequency() + s2.getData().getFrequency(), null); //making new element
            BinaryTree<CodeTreeElement> newTree = new BinaryTree<>(currElement); //making new tree
            newTree.setLeft(s1);
            newTree.setRight(s2);
            result.add(newTree); //adding new tree to priority queue
        }
        if(result.isEmpty()) { //accounting for if file is empty
            return null;
        }
        return result.remove();
    }

    //helper method
    public PriorityQueue<BinaryTree<CodeTreeElement>> makeIndividualTrees(Map<Character, Long> frequencies) {
        PriorityQueue<BinaryTree<CodeTreeElement>> result = new PriorityQueue<>((BinaryTree<CodeTreeElement> f1, BinaryTree<CodeTreeElement> f2) -> (int)(f1.getData().getFrequency()-f2.getData().getFrequency())); //anonymous function
        Set<Character> keys = frequencies.keySet();
        for(Character c: keys) { //traversing through each key to make new tree
            CodeTreeElement currElement = new CodeTreeElement(frequencies.get(c), c);
            BinaryTree<CodeTreeElement> curr = new BinaryTree<>(currElement);
            result.add(curr);
        }
        return result;
    }

    @Override
    public Map<Character, String> computeCodes(BinaryTree<CodeTreeElement> codeTree) {
        Map<Character, String> result = new HashMap<>();
        if(codeTree == null) { //accounting for if tree is empty
            return null;
        }
        if(codeTree.size() == 1) { //accounting for if there's only one character or all the same character
            result.put(codeTree.getData().getChar(), "1");
            return result;
        }
        String s = "";
        addStrings(codeTree, s, result);
        return result;
    }

    public void addStrings(BinaryTree<CodeTreeElement> codeTree, String code, Map<Character, String> result) { //recursive function to find paths
        if(codeTree.isLeaf()) {
            result.put(codeTree.getData().getChar(), code);
        }
        else {
            if(codeTree.hasLeft()) {
                addStrings(codeTree.getLeft(), code+"0", result);
            }
            if(codeTree.hasRight()) {
                addStrings(codeTree.getRight(), code+"1", result);
            }
        }
    }
    @Override
    public void compressFile(Map<Character, String> codeMap, String pathName, String compressedPathName) throws IOException {
        if(codeMap == null) return; //if file is empty
        BufferedReader input = null;
        BufferedBitWriter output = null;
        try {
            input = new BufferedReader(new FileReader(pathName));
            output = new BufferedBitWriter(compressedPathName);
            int s;
            while ((s = input.read()) != -1) {
                char c = (char)s;
                if(codeMap.containsKey(c)) {
                    String sequence = codeMap.get(c);
                    for(int i=0; i<sequence.length(); i++) { //going through each bit in a string
                        if(sequence.charAt(i) == '0') output.writeBit(false);
                        else output.writeBit(true);
                    }
                }
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }
        finally {
            try { output.close();
                input.close(); }
            catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    @Override
    public void decompressFile(String compressedPathName, String decompressedPathName, BinaryTree<CodeTreeElement> codeTree) throws IOException {
        BufferedBitReader input = null;
        BufferedWriter output = null;
        BinaryTree<CodeTreeElement> curr = codeTree;
        try {
            input = new BufferedBitReader(compressedPathName);
            output = new BufferedWriter(new FileWriter(decompressedPathName));
            if(codeTree == null) return; //accounting for empty file
            while(input.hasNext()) {
                boolean bit = input.readBit();
                if(bit && curr.hasRight()) curr = curr.getRight();
                if(!bit && curr.hasLeft()) curr = curr.getLeft();
                if(curr.isLeaf()) {
                    output.write(curr.getData().getChar());
                    curr = codeTree;
                }
            }
        }
        finally { //closing file
            try {
                input.close();
                output.close();
            }
            catch(Exception e) {
                System.out.println(e);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Compression c = new Compression();
        //test cases
        //hello test
        String HelloPathname = "inputs/hello.txt";
        Map<Character, Long> res = c.countFrequencies(HelloPathname);
        BinaryTree<CodeTreeElement> res2 = c.makeCodeTree(res);
        Map<Character, String> result = c.computeCodes(res2);
        c.compressFile(result, HelloPathname, "inputs/hello_compressed.txt");
        c.decompressFile("inputs/hello_compressed.txt", "inputs/hello_decompressed.txt", res2);

        //all same letter test
        String hPathName = "inputs/h.txt";
        res = c.countFrequencies(hPathName);
        res2 = c.makeCodeTree(res);
        result = c.computeCodes(res2);
        c.compressFile(result, hPathName, "inputs/h_compressed.txt");
        c.decompressFile("inputs/h_compressed.txt", "inputs/h_decompressed.txt", res2);

        //empty file test
        String EmptyPathName = "inputs/empty.txt";
        res = c.countFrequencies(EmptyPathName);
        res2 = c.makeCodeTree(res);
        result = c.computeCodes(res2);
        c.compressFile(result, EmptyPathName, "inputs/empty_compressed.txt");
        c.decompressFile("inputs/empty_compressed.txt", "inputs/empty_decompressed.txt", res2);


        //US Constitution compression
        String USPathname = "inputs/USConstitution.txt";
        res = c.countFrequencies(USPathname);
        res2 = c.makeCodeTree(res);
        result = c.computeCodes(res2);
        c.compressFile(result, USPathname, "inputs/USConstitution_compressed.txt");
        c.decompressFile("inputs/USConstitution_compressed.txt", "inputs/USConstitution_decompressed.txt", res2);


        //War and Peace compression
        String WarAndPeacename = "inputs/WarAndPeace.txt";
        res = c.countFrequencies(WarAndPeacename);
        res2 = c.makeCodeTree(res);
        result = c.computeCodes(res2);
        c.compressFile(result, WarAndPeacename, "inputs/WarAndPeace_compressed.txt");
        c.decompressFile("inputs/WarAndPeace_compressed.txt", "inputs/WarAndPeace_decompressed.txt", res2);



    }

}
