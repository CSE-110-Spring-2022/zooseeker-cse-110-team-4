package com.example.zooapp;

import org.jgrapht.GraphPath;

import java.util.List;

public interface GraphAlgorithm {
    List<GraphPath<String, IdentifiedWeightedEdge>> runAlgorithm();
    List<GraphPath<String, IdentifiedWeightedEdge>> runChangedLocationAlgorithm(
            ZooNode newStart, List<ZooNode> newList);
    GraphPath<String, IdentifiedWeightedEdge> runPathAlgorithm(ZooNode closestZooNode,
                                                               List<ZooNode> toVisit);
    String getClosestExhibitId();
    List<ZooNode> getNewUserListShortestOrder();
    List<ZooNode> getUserListShortestOrder();
    List<Double> getExhibitDistance();
}
