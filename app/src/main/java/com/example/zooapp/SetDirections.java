package com.example.zooapp;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.TextView;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class handles setting the correct set of directions onto the screen
 */
public class SetDirections {


    //Private fields
    private final DirectionsActivity directionsActivity;
    private ZooNode display;
    private GraphPath<String, IdentifiedWeightedEdge> graphPath;
    private List<GraphPath<String, IdentifiedWeightedEdge>> graphPaths;
    private Map<String, ZooData.VertexInfo> vInfo;
    private Map<String, ZooData.EdgeInfo> eInfo;
    private Graph<String, IdentifiedWeightedEdge> graph;
    private TextView header, directions;

    public SetDirections(DirectionsActivity directionsActivity) {
        this.directionsActivity = directionsActivity;
    }

    /**
     * Creates the detailed directions text for the directions activity and stores it in the variable directions
     *
     * @param GraphPath
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

    /**
     * Creates the brief directions text for the directions activity and stores it in the variable
     * directions
     *
     * @param GraphPath
     */
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


    /**
     * Sets the correct animal header that matches the directions
     *
     * @param TextView header
     */
    public void setDirectionsText(boolean directionsDetailedText) {
        if(directionsDetailedText) {
            setDetailedDirectionsText(graphPath);
        } else {
            setBriefDirectionsText(graphPath);
        }
    }

    public void setHeader(TextView header) {
        this.header = header;
    }

    /**
     * Sets the directions TextView to the correct type of directions
     *
     * @param TextView directions
     */
    public void setDirections(TextView directions) {
        this.directions = directions;
    }

    public void setGraph(Graph<String, IdentifiedWeightedEdge> graph) {
        this.graph = graph;
    }

    public void setvInfo(Map<String, ZooData.VertexInfo> vInfo) {
        this.vInfo = vInfo;
    }

    public void seteInfo(Map<String, ZooData.EdgeInfo> eInfo) {
        this.eInfo = eInfo;
    }

    public void setGraphPath(GraphPath<String, IdentifiedWeightedEdge> graphPath) {
        this.graphPath = graphPath;
    }

    public GraphPath<String, IdentifiedWeightedEdge> getGraphPath() {
        return graphPath;
    }

    public void setGraphPaths(List<GraphPath<String, IdentifiedWeightedEdge>> graphPaths) {
        this.graphPaths = graphPaths;
    }

    public List<GraphPath<String, IdentifiedWeightedEdge>> getGraphPaths() {
        return graphPaths;
    }

    public ZooNode getDisplay() {
        return display;
    }

    public void setDisplay(ZooNode display) {
        this.display = display;
    }

    public void skipNewDirections() {
        Log.d("SkipButton", "Skip Button Clicked");
        Log.d("SkipButton", "List planned animal BEFORE: " +
                directionsActivity.plannedAnimalDao.getAll().toString());
        Log.d("SkipButton", "Current view exhibit: " + directionsActivity
                .userListShortestOrder.get(directionsActivity.currIndex+1).toString());
        directionsActivity.plannedAnimalDao.delete(
                directionsActivity.userListShortestOrder.get(directionsActivity.currIndex+1));

        var nearestZooNode =
                directionsActivity.getExhibitLocations()
                        .getZooNodeClosestToCurrentLocation(directionsActivity.getLocationToUse());

        var reorderedExhibits = directionsActivity.algorithm
                .runChangedLocationAlgorithm(nearestZooNode,
                        directionsActivity.userListShortestOrder.subList(
                                directionsActivity.currIndex+2,
                                directionsActivity.userListShortestOrder.size()-1));
        Log.d("Check Location", "New Graph Path: " + reorderedExhibits.toString());
        var originalVisitedExhibits =
                getGraphPaths().subList(0, directionsActivity.currIndex);
        Log.d("Check Location", "Old Graph Path: " + originalVisitedExhibits);
        setGraphPaths(Stream.concat(originalVisitedExhibits.stream(),
                reorderedExhibits.stream()).collect(Collectors.toList()));

        // Get the new List of zoo nodes in the new shortest order of the remaining
        // exhibits
        Log.d("Check Location", "Graph Plan Replan: " + getGraphPaths().toString());
        var reorderedShortestOrder = directionsActivity.algorithm
                .getNewUserListShortestOrder();
        Log.d("Check Location", "New Order: " + reorderedShortestOrder.toString());
        var originalVisitedShortestOrder = directionsActivity.userListShortestOrder
                .subList(0, directionsActivity.currIndex+1);
        Log.d("Check Location", "Old Beginning: " +
                originalVisitedShortestOrder.toString());
        directionsActivity.userListShortestOrder = Stream.concat(originalVisitedShortestOrder
                        .stream(), reorderedShortestOrder.stream()).collect(Collectors.toList());
        Log.d("Check Location", "Replan Complete: " +
                directionsActivity.userListShortestOrder.toString());
        Log.d("Location", directionsActivity.userListShortestOrder.toString());

        // Set up for the exhibitLocations class
        var subListSize = (directionsActivity.currIndex >=
                directionsActivity.userListShortestOrder.size() - 2) ?
                directionsActivity.userListShortestOrder.size() :
                directionsActivity.userListShortestOrder.size() - 1;
        directionsActivity.getExhibitLocations().setupExhibitLocations(
                directionsActivity.userListShortestOrder.subList(directionsActivity.currIndex+1,
                        subListSize));
        Log.d("Check Location", directionsActivity.getExhibitLocations()
                .exhibitsSubList.toString());
        nearestZooNode =
                directionsActivity.getExhibitLocations().getZooNodeClosestToCurrentLocation(
                        directionsActivity.getLocationToUse());

        // Find the new path to display
        setGraphPath(directionsActivity.algorithm.runPathAlgorithm(nearestZooNode,
                directionsActivity.getExhibitLocations().exhibitsSubList));
    }
}