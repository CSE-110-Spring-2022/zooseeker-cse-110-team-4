package com.example.zooapp;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.TextView;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SetDirections {
    private final DirectionsActivity directionsActivity;
    public Graph<String, IdentifiedWeightedEdge> graph;
    public GraphPath<String, IdentifiedWeightedEdge> graphPath;
    public Map<String, ZooData.VertexInfo> vInfo;
    public Map<String, ZooData.EdgeInfo> eInfo;
    public List<GraphPath<String, IdentifiedWeightedEdge>> graphPaths;
    public ZooNode display;

    private TextView header, directions;

    public SetDirections(DirectionsActivity directionsActivity) {
        this.directionsActivity = directionsActivity;
    }

    /**
     * Sets the text for the directions activity
     */
    @SuppressLint("DefaultLocale")
    public void setDetailedDirectionsText(
            GraphPath<String, IdentifiedWeightedEdge> directionsToExhibit) {

        // Get the needed zoo node information
        var current = (directionsActivity.getLocationToUse() == null) ?
                directionsActivity.getUserListShortestOrder()
                        .get(directionsActivity.getCurrIndex()) : directionsActivity
                .getExhibitLocations().getZooNodeClosestToCurrentLocation(directionsActivity
                        .getLocationToUse());
        display = directionsActivity.getUserListShortestOrder().get(directionsActivity
                .getCurrIndex() + 1);

        // Set the header to the correct display name
        header.setText(display.name);

        // Set up for getting all the directions
        var i = 1;
        String source, target, correctTarget, start, direction = "";
        start = (current.group_id != null) ? directionsActivity.getZooNodeDao()
                .getById(current.group_id).name : current.name;
        var edgeList = directionsToExhibit.getEdgeList();

        if (edgeList.isEmpty()) {
            direction += String.format("The %s are nearby", display.name);
        }

        // Testing purposes
        Log.d("Edge Format", start);

        // Get all the directions from current zoo node to the next zoo node
        for (var e : edgeList) {
            Log.d("Edge Format", e.toString());
            source = Objects.requireNonNull(vInfo.get(graph.getEdgeSource(e).toString())).name;
            target = Objects.requireNonNull(vInfo.get(graph.getEdgeTarget(e).toString())).name;
            correctTarget = (source.equals(start)) ? target : source;
            Log.d("Edge Format", correctTarget);

            if (i == edgeList.size() && display.group_id != null) {
                direction += String.format(" %d. Walk %.0f feet along %s towards the '%s' and " +
                                "find '%s' inside",
                        i,
                        graph.getEdgeWeight(e),
                        Objects.requireNonNull(eInfo.get(e.getId())).street,
                        correctTarget,
                        display.name);
            } else {
                // Format directions to proper format
                direction += String.format(" %d. Walk %.0f feet along %s towards the '%s'\n",
                        i,
                        graph.getEdgeWeight(e),
                        Objects.requireNonNull(eInfo.get(e.getId())).street,
                        correctTarget);
            }
            start = correctTarget;
            i++;
        }

        // Set the directions text
        directions.setText(direction);
    }

    @SuppressLint("DefaultLocale")
    public void setBriefDirectionsText(
            GraphPath<String, IdentifiedWeightedEdge> directionsToExhibit) {
        var current = (directionsActivity.getLocationToUse() == null) ?
                directionsActivity.getUserListShortestOrder()
                        .get(directionsActivity.getCurrIndex()) : directionsActivity
                .getExhibitLocations().getZooNodeClosestToCurrentLocation(directionsActivity
                        .getLocationToUse());
        display = directionsActivity.getUserListShortestOrder()
                .get(directionsActivity.getCurrIndex() + 1);

        // Set the header to the correct display name
        header.setText(display.name);

        // Set up for getting all the directions
        var directionNumber = 1;
        String source, target, correctTarget, start, direction = "";
        double distance = 0.0;
        start = current.name;
        var edgeList = directionsToExhibit.getEdgeList();

        if (edgeList.isEmpty()) {
            direction += String.format("The %s are nearby", display.name);
        }

        // Testing purposes
        Log.d("Edge Format", start);

        // Get all the directions from current zoo node to the next zoo node
        for (int j = 0; j < edgeList.size(); j++) {
            var e = edgeList.get(j);
            distance += graph.getEdgeWeight(e);
            source = Objects.requireNonNull(vInfo.get(graph.getEdgeSource(e).toString())).name;
            target = Objects.requireNonNull(vInfo.get(graph.getEdgeTarget(e).toString())).name;
            Log.d("Directions", "Start: " + start + ", Source: " + source + ", Target: "
                    + target);
            if (j != edgeList.size() - 1 &&
                    Objects.requireNonNull(eInfo.get(e.getId())).street
                            .equals(Objects.requireNonNull(eInfo.get(edgeList.get(j + 1).getId()))
                                    .street)) {
                start = (source.equals(start)) ? target : source;
                continue;
            }
            Log.d("Edge Format", e.toString());
            correctTarget = (source.equals(start)) ? target : source;
            Log.d("Edge Format", correctTarget);

            if (j == edgeList.size() - 1 && display.group_id != null) {
                direction += String.format(" %d. Walk %.0f feet along %s towards the '%s' and " +
                                "find '%s' inside",
                        directionNumber,
                        distance,
                        Objects.requireNonNull(eInfo.get(e.getId())).street,
                        correctTarget,
                        display.name);
            } else {
                // Format directions to proper format
                direction += String.format(" %d. Walk %.0f feet along %s towards the '%s'\n",
                        directionNumber,
                        distance,
                        Objects.requireNonNull(eInfo.get(e.getId())).street,
                        correctTarget);
            }
            start = correctTarget;
            distance = 0.0;
            directionNumber++;
        }

        // Set the directions text
        directions.setText(direction);
    }

    public void setHeader(TextView header) {
        this.header = header;
    }

    public void setDirections(TextView directions) {
        this.directions = directions;
    }
}