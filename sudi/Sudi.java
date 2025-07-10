import java.io.*;
import java.util.*;

/* Julia Allos
* CS10- AQL
* Problem set 5
* May 20, 2024
 */

public class Sudi {
    //instance variables
    Map<String, Map<String, Double>> transitions;
    Map<String, Map<String, Double>> observations;
    int unseen;


    //constructor
    public Sudi() {
        transitions = new HashMap<>();
        observations = new HashMap<>();
        unseen = -100;
    }

    //Viterbi method
    public String[] generatePath(String[] words) {
        List<Map<String, String>> backTracker = new ArrayList<>(); //creates backtracker
        Map<String, Double> currScores = new HashMap<>();
        currScores.put("#",0.0);
        for(int i=0; i<words.length; i++){
            List<String> nextStates = new ArrayList<>();
            Map<String, Double> nextScores = new HashMap<>();
            Map<String, String> temp = new HashMap<>();
            for(String currState: currScores.keySet()) {
                if(!transitions.containsKey(currState)) continue;
                for(String nextState: transitions.get(currState).keySet()) {
                    if(transitions.get(currState).get(nextState) != null) {
                        nextStates.add(nextState);
                        Double nextScore = currScores.get(currState) + transitions.get(currState).get(nextState);
                        if(observations.get(nextState).get(words[i]) == null) {
                            nextScore = nextScore + unseen;
                        }
                        else nextScore += observations.get(nextState).get(words[i]);
                        if(!nextScores.containsKey(nextState) || nextScore > nextScores.get(nextState)) {
                            nextScores.put(nextState, nextScore);
                            temp.put(nextState, currState);
                        }
                    }
                }
            }
            currScores = nextScores;
            backTracker.add(temp);
        }

        Double score = 1.0;
        String current = "";
        for(String currState: currScores.keySet()) {
           if(score > 0 || currScores.get(currState) > score) {
               current = currState;
               score = currScores.get(currState);
           }
        }
        String[] finalResult = new String[words.length];
        for(int i = words.length-1; i >= 0; i--) {
            finalResult[i] = current;
            current = backTracker.get(i).get(current);
        }
        return finalResult;
    }

    /*trains Sudi
    * parses through files
    * changes observations + transitions table to get values
    * once done parsing through file, alters transitions + observations maps to find all log probabilities
     */
    public void train(String filename, String filenameTest) throws IOException {
        List<String[]> reading = listLines(filename);
        List<String[]> testing = listLines(filenameTest);
        for(int i=0; i< reading.size(); i++) {
            if(reading.get(i).length != 0) hmm(reading.get(i), testing.get(i));
        }

        //calculating transition log probabilities
        double total = 0.0;
        for(String s: transitions.keySet()) {
            total = 0;
            for(String st: transitions.get(s).keySet()) {
                if(transitions.get(s).get(st) != null) total = total + transitions.get(s).get(st);
            }
            for(String in: transitions.get(s).keySet()) {
                if(transitions.get(s).get(in) != null) transitions.get(s).put(in, Math.log(transitions.get(s).get(in)/total));
            }
        }
        //calculating observation log probabilities
        for(String s: observations.keySet()) {
            total = 0;
            for(String st: observations.get(s).keySet()) {
                if(observations.get(s).get(st) != null) total = total + observations.get(s).get(st);
            }
            for(String in: observations.get(s).keySet()) {
                if(observations.get(s).get(in) != null) observations.get(s).put(in, Math.log(observations.get(s).get(in) / total));
            }
        }
    }
    //takes array of words and array of strings + alters instance variable maps
    public void hmm(String[] words, String[] tags) {
        //transitions
        //checking to add in new values to the table if needed
        if(!transitions.containsKey("#")) {
            setTables(words, tags);
        }
        for(String tag: tags) {
            if(!transitions.containsKey(tag)) {
                Map<String, Double> t = new HashMap<>();
                for(String s:transitions.keySet()) {
                    if(!s.equals("#")) t.put(s, null);
                }
                transitions.put(tag, t);
                for(String s: transitions.keySet()) {
                    t = transitions.get(s);
                    t.put(tag, null);
                    transitions.put(s, t);
                }
            }
        }

        if(tags.length >= 1) {
            if(transitions.get("#").get(tags[0]) == null) {
                transitions.get("#").put(tags[0], 1.0);
            }
            else {
                transitions.get("#").put(tags[0], transitions.get("#").get(tags[0]) +1);
            }
        }
        for(int i=0; i<tags.length-1; i++) {
            if(transitions.get(tags[i]).get(tags[i+1]) == null) {
                transitions.get(tags[i]).put(tags[i+1], 1.0);
            }
            else {
                transitions.get(tags[i]).put(tags[i+1], transitions.get(tags[i]).get(tags[i+1]) + 1);
            }
        }

        //checking if have all tags
        String tester = null;
        for(String s: observations.keySet()) {
            tester = s;
            break;
        }
        for(String tag: tags) {
            if(!observations.containsKey(tag)) {
                Map<String, Double> th = new HashMap<>();
                for(String word: observations.get(tester).keySet()) {
                    th.put(word, null);
                }
                observations.put(tag, th);
            }
        }
        //checking if have all words
        Map<String, Double> test = new HashMap<>();
        for(String word: words) {
            if(!observations.get(tags[0]).containsKey(word)) {
                for(String tag: tags) {
                    test = observations.get(tag);
                    test.put(word, null);
                    observations.put(tag, test);
                }
            }
        }

        //observations
        for(int i=0; i<tags.length; i++) {
            if(observations.get(tags[i]).get(words[i]) == null) {
                observations.get(tags[i]).put(words[i], 1.0);
            }
            else {
                observations.get(tags[i]).put(words[i], observations.get(tags[i]).get(words[i]) + 1);
            }
        }
    }

    //helper method to initialize methods on first run
    public void setTables(String[] words, String[]tags) {
        //transitions
        Map<String, Double> extra = new HashMap<>();
        for(String tag: tags) {
            extra.put(tag, null);
        }
        transitions.put("#", extra);

        for(String tag: tags) {
            Map<String, Double> second = new HashMap<>();
            for(String tag2: tags) {
                second.put(tag2, null);
            }
            transitions.put(tag, second);
        }

        //observations
        for(String tag: tags) {
            Map<String, Double> temp = new HashMap<>();
            for(String word: words) {
                temp.put(word, null);
            }
            observations.put(tag, temp);
        }
    }

    //gives tags to input line - is this right? Am I just supposed to be doing this
    public void consoleTest() {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter a sentence to test your Viterbi method: ");
        String line;
        line = s.nextLine();
        String[] tokens = line.split(" ");
        tokens = generatePath(tokens);
        for(String sh: tokens) {
            System.out.println(sh + " ");
        }
    }
    public List<String[]> listLines(String filename) throws IOException {
        BufferedReader reading = null;
        reading = new BufferedReader(new FileReader(new File(filename)));
        String[] words;
        String line;
        List<String[]> result = new ArrayList<>();
        while((line = reading.readLine()) != null) {
            words = line.toLowerCase().split(" ");
            result.add(words);
        }
        return result;
    }


    //evaluates performance on file
    public void fileBasedTest(String filename, String filenameTest) throws IOException {
        List<String[]> fileLines = listLines(filename);
        List<String[]> testLines = listLines(filenameTest);
        int right = 0;
        int wrong = 0;

        for(int i=0; i<testLines.size(); i++) {
            String[] testline = testLines.get(i);
            String[] fileline = fileLines.get(i);
            String[] resultingtag = generatePath(fileline);
            for(int j=0; j < resultingtag.length; j++) {
                if(testline[j].equals(resultingtag[j])) {
                    right++;
                }
                else {
                    wrong++;
                }
            }
        }
        System.out.println("Number of correct tags: " + right + "\nNumber of wrong tags: " + wrong + "\nUnseen word penalty: " + unseen);
    }

    //main method
    public static void main(String[] args) throws IOException {
        Sudi sud = new Sudi();
        sud.train("PS5/brown-train-sentences.txt", "PS5/brown-train-tags.txt");
        //sud.consoleTest(); -uncomment if you want to do a console test
        sud.fileBasedTest("PS5/brown-test-sentences.txt", "PS5/brown-test-tags.txt");
    }

}
