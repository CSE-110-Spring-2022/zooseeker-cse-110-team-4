package com.example.zooapp;

import org.jgrapht.GraphPath;

import java.util.List;

/**
 * This interface is used when needing to run algorithms to get information from our graph of exhibits
 */
public interface GraphAlgorithm {
    List<GraphPath<String, IdentifiedWeightedEdge>> runAlgorithm();
    List<GraphPath<String, IdentifiedWeightedEdge>> runChangedLocationAlgorithm(
            ZooNode newStart, List<ZooNode> newList);
    GraphPath<String, IdentifiedWeightedEdge> runPathAlgorithm(ZooNode closestZooNode,
                                                               List<ZooNode> toVisit);
    GraphPath<String, IdentifiedWeightedEdge> runReversePathAlgorithm(ZooNode closestZooNode,
                                                                      ZooNode previousZooNode);
    String getClosestExhibitId();
    List<ZooNode> getNewUserListShortestOrder();
    List<ZooNode> getUserListShortestOrder();
    List<Double> getExhibitDistance();
}
