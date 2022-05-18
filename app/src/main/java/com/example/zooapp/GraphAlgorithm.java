package com.example.zooapp;

import org.jgrapht.GraphPath;

import java.util.List;

public interface GraphAlgorithm {
    List<GraphPath<String, IdentifiedWeightedEdge>> runAlgorithm();
    List<ZooNode> getUserListShortestOrder();
    List<Double> getExhibitDistance();
}
