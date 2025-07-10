import java.util.*;

/* Julia Allos
* CS10- AQL
* PS4
 */

public class GraphLibrary {
    public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source) {
        Graph<V,E> pathtree = new AdjacencyMapGraph<>();
        pathtree.insertVertex(source);
        Set<V> visited = new HashSet<V>(); //accounts for which vertices in graph have been visited
        Queue<V> queue = new LinkedList<V>(); //queue to implement BFS
        queue.add(source);
        visited.add(source);
        while(!queue.isEmpty()) {
            V temp = queue.remove();
            for(V v: g.outNeighbors(temp)) {
                if(!visited.contains(v)) {
                    visited.add(v);
                    queue.add(v);
                    pathtree.insertVertex(v);
                    pathtree.insertDirected(v,temp,g.getLabel(v, temp));
                }
            }
        }
        return pathtree;
    }
    public static <V,E> List<V> getPath(Graph<V,E> tree, V v) {
        V curr = v;
        List<V> path = new ArrayList<>();
        path.add(v);
        while(tree.outDegree(curr) != 0) {
            for(V vertex: tree.outNeighbors(curr)) {
                path.add(vertex);
                curr = vertex;
            }
        }
        return path;
    }

    public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph) {
        Set<V> result = new HashSet<>();
        for (V v: graph.vertices()) {
            if(!subgraph.hasVertex(v)) {
                result.add(v);
            }
        }
        return result;

    }
    public static <V,E> double averageSeparation(Graph<V,E> tree, V root) {
        double result = totalSeparation(tree, root, 0)/(double)(tree.numVertices());
        return result;
    }

    private static<V,E> int totalSeparation(Graph<V,E> tree, V v, int sep) {
        int total = sep;
        for(V u: tree.inNeighbors(v)) {
            total += totalSeparation(tree, u, sep + 1);
        }
        return total;
    }

}
