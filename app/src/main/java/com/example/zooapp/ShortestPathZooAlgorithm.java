package com.example.zooapp;

import android.content.Context;
import android.util.Log;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.List;

/**
 * This class if for the algorithm to find the shortest path to visit certain nodes
 */
public class ShortestPathZooAlgorithm implements GraphAlgorithm {
    // Private data fields
    private List<ZooNode> userListExhibits, userListShortestOrder, newUserListShortestOrder;
    private List<Double> exhibitDistanceFromStart;
    private ZooNode newStart;
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
        this.newUserListShortestOrder = new ArrayList<>();
        this.exhibitDistanceFromStart = new ArrayList<>();
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
        var g = ZooData.loadZooGraphJSON(context,
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
        var entranceExitGate = "entrance_exit_gate";
        var start = entranceExitGate;
        var minDistance = Double.POSITIVE_INFINITY;
        ZooNode shortestZooNodeStart = null;

        // Finding all shortest paths
        while( !userListExhibits.isEmpty() ) {
            // Find shortest path for each zooNode available from current node
            for(var zooNode: userListExhibits ) {
                var zooNodeName = (zooNode.parent_id != null) ? zooNode.parent_id : zooNode.id;
                var tempPath =
                        DijkstraShortestPath.findPathBetween(g,start, zooNodeName);
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
            start = (shortestZooNodeStart.parent_id != null) ? shortestZooNodeStart.parent_id :
                    shortestZooNodeStart.id;
            userListExhibits.remove(shortestZooNodeStart);
            userListShortestOrder.add(shortestZooNodeStart);
            minDistance = Double.POSITIVE_INFINITY;
        }
        var finalPath =
                DijkstraShortestPath.findPathBetween(g, start, entranceExitGate);
        resultPath.add(finalPath);
        exhibitDistanceFromStart.add(finalPath.getWeight());
        // Return a list of all shortest paths to complete cycle
        return resultPath;
    }

    public List<GraphPath<String, IdentifiedWeightedEdge>> runChangedLocationAlgorithm(
            ZooNode newStart, List<ZooNode> newList) {
        var g = ZooData.loadZooGraphJSON(context,
                "sample_zoo_graph.json");
        return runChangedLocationAlgorithm(g, newStart, newList);
    }

    private List<GraphPath<String, IdentifiedWeightedEdge>> runChangedLocationAlgorithm(Graph<String,
            IdentifiedWeightedEdge> g, ZooNode newStart, List<ZooNode> newList) {
        // Setup all necessary parts for algorithm
        List<GraphPath<String, IdentifiedWeightedEdge>> resultPath = new ArrayList<>();
        GraphPath<String, IdentifiedWeightedEdge> minDistPath = null;
        var entranceExitGate = "entrance_exit_gate";
        var start = newStart.id;
        var minDistance = Double.POSITIVE_INFINITY;
        this.newStart = newStart;
        ZooNode shortestZooNodeStart = newStart;

        // Finding all shortest paths
        while( !newList.isEmpty() ) {
            // Find shortest path for each zooNode available from current node
            for(var zooNode: newList ) {
                var zooNodeName = (zooNode.parent_id != null) ? zooNode.parent_id : zooNode.id;
                var tempPath =
                        DijkstraShortestPath.findPathBetween(g,start, zooNodeName);
                // Setting the shortest path
                if( tempPath.getWeight() < minDistance ) {
                    shortestZooNodeStart = zooNode;
                    minDistance = tempPath.getWeight();
                    minDistPath = tempPath;
                }
            }
            // Finalize shortest path and add to result
            resultPath.add(minDistPath);
            start = (shortestZooNodeStart.parent_id != null) ? shortestZooNodeStart.parent_id :
                    shortestZooNodeStart.id;
            newList.remove(shortestZooNodeStart);
            newUserListShortestOrder.add(shortestZooNodeStart);
            minDistance = Double.POSITIVE_INFINITY;
        }
        var finalPath =
                DijkstraShortestPath.findPathBetween(g, start, entranceExitGate);
        resultPath.add(finalPath);
        // Return a list of all shortest paths to complete cycle
        return resultPath;
    }

    /**
     * Gives the zoo nodes in order of the algorithm result for the user to follow
     *
     * @return List of zoo nodes in the approximate shortest path
     */
    public List<ZooNode> getUserListShortestOrder() {
        var entrance = dao.getByName("Entrance and Exit Gate");
        userListShortestOrder.add(0, entrance);
        userListShortestOrder.add(userListShortestOrder.size(), entrance);
        return userListShortestOrder;
    }

    public List<ZooNode> getNewUserListShortestOrder() {
        var entrance = dao.getByName("Entrance and Exit Gate");
        newUserListShortestOrder.add(0, newStart);
        newUserListShortestOrder.add(newUserListShortestOrder.size(), entrance);
        return newUserListShortestOrder;
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
        var i = 0;
        for(var distance: exhibitDistanceFromStart ) {
            total += distance;
            exhibitDistanceFromStart.set(i++, total);
        }
    }
}