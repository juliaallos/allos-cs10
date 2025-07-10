import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


/*Julia Allos
* CS10- AQL
* Bacon Game
* 10 May, 2024
 */
public class BaconGame implements Game<String, String> {
    //instance variables
    String actorName;
    BufferedReader actors;
    BufferedReader movies;
    BufferedReader edges;

    Graph<String, Set<String>> g; //graph of all actors + movies

    Graph<String, Set<String>> pathtree; //graph for specific path from specific actor
    int numConnectedActors;
    double averageSeparation;

    public BaconGame(String name) {
        try {
            actorName = name;
            //loading files
            actors = new BufferedReader(new FileReader("PS4/actors.txt"));
            movies = new BufferedReader(new FileReader("PS4/movies.txt"));
            edges = new BufferedReader(new FileReader("PS4/movie-actors.txt"));
            g = new AdjacencyMapGraph<>();
            makeGraph(); //makes graph of all actors + movies
            pathtree = GraphLibrary.bfs(g, actorName); //makes path from (initially) kevin bacon
            numConnectedActors = pathtree.numVertices()-1; //-1 for person him/herself
            averageSeparation = GraphLibrary.averageSeparation(pathtree, actorName);
            System.out.println("Kevin Bacon is now the center of the acting universe, connected to + " + numConnectedActors + "/9235 actors with average separation " + averageSeparation);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void makeGraph() throws IOException {
        String line;
        //making map of actor ids to their names
        Map<Integer, String> actorIDs = new HashMap<>();
        while ((line = actors.readLine()) != null) {
            actorIDs.put(Integer.parseInt(line.split("\\|")[0]), line.split("\\|")[1]);
        }

        //making map of movie ids to their names
        Map<Integer, String> movieIDs = new HashMap<>();
        while ((line = movies.readLine()) != null) {
            movieIDs.put(Integer.parseInt(line.split("\\|")[0]), line.split("\\|")[1]);
        }

        //making map of movie ids to actor ids
        Map<Integer, ArrayList<Integer>> movieToActors = new HashMap<>();
        while ((line = edges.readLine()) != null) {
            String[] tokens = line.split("\\|");
            int movID = Integer.parseInt(tokens[0]);
            int actID = Integer.parseInt(tokens[1]);
            if (!movieToActors.containsKey(movID)) {
                List<Integer> temp = new ArrayList<>();
                temp.add(actID);
                movieToActors.put(movID, (ArrayList<Integer>) temp);
            } else {
                movieToActors.get(movID).add(actID);
            }
        }
        //making graph using maps
        for (Integer actorID : actorIDs.keySet()) {
            g.insertVertex(actorIDs.get(actorID));
        }
        for(int actorID: actorIDs.keySet()) {
            for(int movie: movieToActors.keySet()) {
                if(movieToActors.get(movie).contains(actorID)) {
                    for(int actor2: movieToActors.get(movie)) {
                        String actor2Name = actorIDs.get(actor2);
                        String actorIDName = actorIDs.get(actorID);
                        if(actor2 !=actorID) {
                            if(!g.hasEdge(actor2Name, actorIDName)) {
                                Set<String> result = new HashSet<>();
                                result.add(movieIDs.get(movie));
                                g.insertUndirected(actor2Name, actorIDName, result);
                            }
                            else {
                                g.getLabel(actor2Name, actorIDName).add(movieIDs.get(movie));
                            }
                        }
                    }
                }
            }
        }
        try{
            actors.close();
            movies.close();
            edges.close();
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void keyC(int num) {
        if(Math.abs(num) > pathtree.numVertices()) {
            System.out.println("Cannot list top centers because there are not enough vertices. Please enter a different number");
            return;
        }
        int count = num;
        if(num > 0) {
            PriorityQueue<String> result = new PriorityQueue<>((String s1, String s2) -> g.inDegree(s2)-g.inDegree(s1));
            for(String s: pathtree.vertices()) {
                result.add(s);
            }
            while(count > 0) {
                System.out.println(result.remove());
                count--;
            }
        }
        if(num < 0) {
            PriorityQueue<String> result = new PriorityQueue<>((String s1, String s2) -> g.inDegree(s1) - g.inDegree(s2));
            for(String s: pathtree.vertices()) {
                result.add(s);
            }
            while(count < 0) {
                System.out.println(result.remove());
                count++;
            }
        }
    }

    @Override
    public void keyD(int low, int high) {
        PriorityQueue<String> result = new PriorityQueue<>((String s1, String s2) -> g.inDegree(s1) - g.inDegree(s2));
        for(String s: pathtree.vertices()) {
            result.add(s);
        }
        for(String s: result) {
            if(g.inDegree(s) < low) continue;
            if(g.inDegree(s) > high) break;
            System.out.println(s);
        }
    }

    @Override
    public void keyI() {
        Set<String> result = GraphLibrary.missingVertices(g, pathtree);
        for(String s: result) {
            System.out.println(s);
        }
    }

    @Override
    public void keyP(String name) {
        if(!pathtree.hasVertex(name) && !g.hasVertex(name)) {
            System.out.println("Name was not included in data file. Please try a different actor");
            return;
        }
        else if(!pathtree.hasVertex(name) && g.hasVertex(name)) {
            System.out.println("Name is not in the path from " + actorName + ". They are infinitely separated. Please try a different actor");
            return;
        }
        List<String> result = GraphLibrary.getPath(pathtree, name);
        System.out.println(name + "'s number is " + (result.size()-1));
        if(result.size() == 1) {
            System.out.println("Silly goose! This is the " + actorName + " game! Try another actor.");
        }
        for(int i=0; i<result.size()-1; i++) {
            System.out.println(result.get(i) + " appeared in " + g.getLabel(result.get(i), result.get(i+1)) + " with " + result.get(i+1));
        }
    }

    @Override
    public void keyS(int low, int high) {
        List<String> result;
        for(String s: pathtree.vertices()) {
            result = GraphLibrary.getPath(pathtree, s);
            if(result.size() > low && result.size() < high) {
                System.out.println(s);
            }
        }
    }

    @Override
    public void keyU(String name) {
        if(!g.hasVertex(name)) {
            System.out.println("Name was not included in data file. Cannot name " + name + " the center of the universe.");
            return;
        }
        actorName = name;
        pathtree = GraphLibrary.bfs(g, actorName);
        numConnectedActors = pathtree.numVertices()-1;
        averageSeparation = GraphLibrary.averageSeparation(pathtree, actorName);
        System.out.println(actorName + " is now the center of the acting universe, connected to + " + numConnectedActors + "/9235 actors with average separation " + averageSeparation);
    }


    public static void main(String[] args) {
        //reading files
        BaconGame newGame = new BaconGame("Kevin Bacon");
        Scanner game = new Scanner(System.in);
        String line;
        while (true) {
            System.out.println("Commands:\n" +
                    "c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation\n" +
                    "d <low> <high>: list actors sorted by degree, with degree between low and high\n" +
                    "i: list actors with infinite separation from the current center\n" +
                    "p <name>: find path from <name> to current center of the universe\n" +
                    "s <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high\n" +
                    "u <name>: make <name> the center of the universe\n" +
                    "q: quit game");
            line = game.nextLine();

            if(line.charAt(0) == 'c') {
                try {
                    String[] tokens = line.split(" ");
                    newGame.keyC(Integer.parseInt(tokens[1]));
                }
                catch(Exception e) {
                    System.out.println("Invalid. You must enter a number if pressing c");
                }

            }
            else if(line.charAt(0) == 'd') {
                try {
                    String[] tokens = line.split(" ");
                    newGame.keyD(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
                }
                catch(Exception e) {
                    System.out.println("Invalid. You must enter 2 numbers if pressing d");
                }

            }
            else if(line.charAt(0) == 'i') {
                newGame.keyI();
            }
            else if(line.charAt(0) == 'p') {
                String[] tokens = line.split(" ");
                String name = "";
                for(int i=1; i<tokens.length;i++) name+=tokens[i] + " ";
                name = name.substring(0,name.length()-1);
                newGame.keyP(name);
            }
            else if(line.charAt(0) == 's') {
                try {
                    String[] tokens = line.split(" ");
                    newGame.keyS(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
                }
                catch(Exception e) {
                    System.out.println("Invalid. You must enter 2 numbers if pressing s");
                }

            }

            else if(line.charAt(0) == 'u') {
                String[] tokens = line.split(" ");
                String name = "";
                for(int i=1; i<tokens.length;i++) name+=tokens[i] + " ";
                name = name.substring(0,name.length()-1);
                newGame.keyU(name);
            }
            else if(line.charAt(0) == 'q') {
                break;
            }
            else{
                System.out.println("Invalid Character. Please try again");
            }
        }
    }
}
