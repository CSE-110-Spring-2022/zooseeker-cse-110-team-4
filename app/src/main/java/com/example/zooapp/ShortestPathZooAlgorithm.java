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
    private List<ZooNode> userListExhibits, userListShortestOrder;
    private List<Double> exhibitDistanceFromStart;
    private Context context;
    private ZooNodeDao dao;

    /**
     * Constructor
     *
     * @param context Context of the app
     * @param userListExhibits List of the exhibits the user wants to visit
     */
    public ShortestPathZooAlgorithm(Context context, List<ZooNode> userListExhibits) {
        this.context = context;
        this.userListExhibits = (userListExhibits == null) ? new ArrayList<>() :
                new ArrayList<>(userListExhibits);
        this.userListShortestOrder = new ArrayList<>();
        this.dao = ZooNodeDatabase.getSingleton(context).ZooNodeDao();
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
            exhibitDistanceFromStart.add(minDistance);
            start = shortestZooNodeStart.id;
            userListExhibits.remove(shortestZooNodeStart);
            userListShortestOrder.add(shortestZooNodeStart);
            minDistance = Double.POSITIVE_INFINITY;
        }
        resultPath.add(DijkstraShortestPath.findPathBetween(g, start, entranceExitGate));
        // Return a list of all shortest paths to complete cycle
        return resultPath;
    }

    /**
     * Gives the zoo nodes in order of the algorithm result for the user to follow
     *
     * @return List of zoo nodes in the approximate shortest path
     */
    public List<ZooNode> getUserListShortestOrder() {
        ZooNode entrance = dao.getByName("Entrance and Exit Gate");
        userListShortestOrder.add(0, entrance);
        userListShortestOrder.add(userListShortestOrder.size(), entrance);
        return userListShortestOrder;
    }

    /**
     * Get the distance of each exhibit from the start along the path generated
     *
     * @return List of distances
     */
    public List<Double> getExhibitDistance() {
        correctExhibitDistanceList();
        return exhibitDistanceFromStart;
    }

    /**
     * Used to correct the distance list
     */
    private void correctExhibitDistanceList() {
        double total = 0;
        int i = 0;
        for( Double distance: exhibitDistanceFromStart ) {
            total += distance;
            exhibitDistanceFromStart.set(i, total);
            i++;
        }
    }
}
