package com.example.zooapp;

import android.content.Context;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.List;

public class ShortestPathZooAlgorithm {
    private List<ZooNode> userListExhibits;
    private Context context;

    public ShortestPathZooAlgorithm(Context context, List<ZooNode> userListExhibits) {
        this.context = context;
        this.userListExhibits = new ArrayList<>(userListExhibits);
    }

    public List<GraphPath<String, IdentifiedWeightedEdge>> runAlgorithm() {
        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON(context, "sample_zoo_graph.json");
        return runAlgorithm(g);
    }

    public List<GraphPath<String, IdentifiedWeightedEdge>> runAlgorithm(Graph<String, IdentifiedWeightedEdge> g) {
        List<GraphPath<String, IdentifiedWeightedEdge>> resultPath = new ArrayList<>();
        GraphPath<String, IdentifiedWeightedEdge> minDistPath = null;
        String entranceExitGate = "entrance_exit_gate";
        String start = entranceExitGate;
        double minDistance = Double.POSITIVE_INFINITY;
        ZooNode shortestZooNodeStart = null;

        while( !userListExhibits.isEmpty() ) {
            for( ZooNode zooNode: userListExhibits ) {
                GraphPath<String, IdentifiedWeightedEdge> tempPath = DijkstraShortestPath.findPathBetween(g,start, zooNode.id);
                if( tempPath.getWeight() < minDistance ) {
                    shortestZooNodeStart = zooNode;
                    minDistance = tempPath.getWeight();
                    minDistPath = tempPath;
                }
            }
            resultPath.add(minDistPath);
            start = shortestZooNodeStart.id;
            userListExhibits.remove(shortestZooNodeStart);
            minDistance = Double.POSITIVE_INFINITY;
        }
        resultPath.add(DijkstraShortestPath.findPathBetween(g, start, entranceExitGate));
        return resultPath;
    }
}
