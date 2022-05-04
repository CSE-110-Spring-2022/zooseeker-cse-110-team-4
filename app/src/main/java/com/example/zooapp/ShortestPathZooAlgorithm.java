package com.example.zooapp;

import android.content.Context;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.List;

/**
 * This class if for the algorithm to find the shortest path to visit certain nodes
 */
public class ShortestPathZooAlgorithm {
    // Private data fields
    private List<ZooNode> userListExhibits;
    private Context context;

    /**
     * Constructor
     *
     * @param context Context of the app
     * @param userListExhibits List of the exhibits the user wants to visit
     */
    public ShortestPathZooAlgorithm(Context context, List<ZooNode> userListExhibits) {
        this.context = context;
        this.userListExhibits = new ArrayList<>(userListExhibits);
    }

    /**
     * Test algorithm used in the test file.
     * Uses the sample graph given
     *
     * @return List of all the shortest paths for a cycle
     */
    public List<GraphPath<String, IdentifiedWeightedEdge>> runAlgorithm() {
        // Testing purposes with the original graph
        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON(context,
                "sample_zoo_graph.json");
        return runAlgorithm(g);
    }

    /**
     * Algorithm to find the approximate shortest path cycle for when at the zoo
     *
     * @param g Graph of nodes at the zoo
     * @return List of all the shortest paths for a cycle
     */
    public List<GraphPath<String, IdentifiedWeightedEdge>> runAlgorithm(Graph<String,
            IdentifiedWeightedEdge> g) {
        // Setup all necessary parts for algorithm
        List<GraphPath<String, IdentifiedWeightedEdge>> resultPath = new ArrayList<>();
        GraphPath<String, IdentifiedWeightedEdge> minDistPath = null;
        String entranceExitGate = "entrance_exit_gate";
        String start = entranceExitGate;
        double minDistance = Double.POSITIVE_INFINITY;
        ZooNode shortestZooNodeStart = null;

        // Finding all shortest paths
        while( !userListExhibits.isEmpty() ) {
            // Find shortest path for each zooNode available from current node
            for( ZooNode zooNode: userListExhibits ) {
                GraphPath<String, IdentifiedWeightedEdge> tempPath =
                        DijkstraShortestPath.findPathBetween(g,start, zooNode.id);
                // Setting the shortest path
                if( tempPath.getWeight() < minDistance ) {
                    shortestZooNodeStart = zooNode;
                    minDistance = tempPath.getWeight();
                    minDistPath = tempPath;
                }
            }
            // Finalize shortest path and add to result
            resultPath.add(minDistPath);
            start = shortestZooNodeStart.id;
            userListExhibits.remove(shortestZooNodeStart);
            minDistance = Double.POSITIVE_INFINITY;
        }
        resultPath.add(DijkstraShortestPath.findPathBetween(g, start, entranceExitGate));
        // Return a list of all shortest paths to complete cycle
        return resultPath;
    }
}
