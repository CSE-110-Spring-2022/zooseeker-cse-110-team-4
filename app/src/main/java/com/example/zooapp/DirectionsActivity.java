package com.example.zooapp;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is for when the user is now seeing the directions for each exhibit
 */
/**
 * This class is for when the user is now seeing the directions for each exhibit
 */
public class DirectionsActivity extends AppCompatActivity {
    //index that is incremented/decremented by next/back buttons
    //used to traverse through planned exhibits
    int currIndex = 0;
    public static boolean check = false;
    public static boolean replanAlertShown = false;
    public static boolean canCheckReplan = true;
    public static boolean recentlyYesReplan = false;

    @VisibleForTesting
    public Location mockLocation;
    @VisibleForTesting
    public Location locationToUse;

    // Graph Information Files
    final String ZOO_GRAPH_JSON = "sample_zoo_graph.json";
    final String NODE_INFO_JSON = "sample_node_info.json";
    final String EDGE_INFO_JSON = "sample_edge_info.json";

    public List<ZooNode> userExhibits, userListShortestOrder;

    // Variable for the graph and path
    public GraphAlgorithm algorithm;
    public AlertDialog alertMessage;
    public ActionBar actionBar;
    public boolean directionsDetailedText = true;
    public PlannedAnimalDao plannedAnimalDao = PlannedAnimalDatabase.getSingleton(this)
            .plannedAnimalDao();
    public ZooNodeDao zooNodeDao = ZooNodeDatabase.getSingleton(this)
            .ZooNodeDao();
    private ExhibitLocations exhibitLocations = new ExhibitLocations(zooNodeDao);
    private Graph<String, IdentifiedWeightedEdge> graph;
    private GraphPath<String, IdentifiedWeightedEdge> graphPath;
    private TextView header, directions;
    private Map<String, ZooData.VertexInfo> vInfo;
    private Map<String, ZooData.EdgeInfo> eInfo;
    private List<GraphPath<String, IdentifiedWeightedEdge>> graphPaths;
    private ZooNode display;
    private Button previous;
    private boolean backwards = false;
    private ZooNode previousClosestZooNode;

    /**
     * Method for onCreate of the activity
     *
     * @param savedInstanceState State of activity
     */
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        resetMockLocation();

        // Set the Title Bar to Directions
        actionBar = getSupportActionBar();
        actionBar.setTitle("Directions");

        previous = findViewById(R.id.previous_button);

        // Grabbing planned animals from planned list and inputting to new activity
        if( plannedAnimalDao.getAll().size() > 0 ){
            //userExhibits = gson.fromJson(getIntent().getStringExtra("ListOfAnimals"), type);
            userExhibits = plannedAnimalDao.getAll();
            Log.d("Zoo Nodes", userExhibits.toString());

            loadGraph(); // will initialize graph, vInfo, and eInfo variables

            // Our old algorithm
            algorithm = new ShortestPathZooAlgorithm(
                    getApplication().getApplicationContext(), userExhibits);
            graphPaths = algorithm.runAlgorithm();
            userListShortestOrder = algorithm.getUserListShortestOrder();
            display = userListShortestOrder.get(currIndex+1);
            exhibitLocations.setupExhibitLocations(userListShortestOrder
                    .subList(currIndex+1, userListShortestOrder.size()-1));

            // Set text views
            header = findViewById(R.id.directions_header);
            directions = findViewById(R.id.directions_text);

            //setDirectionsText(graphPaths.get(currIndex));
            graphPath = algorithm.runPathAlgorithm(zooNodeDao.getById("entrance_exit_gate"),
                    userListShortestOrder.subList(currIndex+1, userListShortestOrder.size()-1));
            previousClosestZooNode = zooNodeDao.getById("entrance_exit_gate");
            if(directionsDetailedText) {
                setDetailedDirectionsText(graphPath);
            } else {
                setBriefDirectionsText(graphPath);
            }
            mockLocation = new Location("Mock Entrance");
            mockLocation.setLatitude(32.73459618734685);
            mockLocation.setLongitude(-117.14936);
//            mockLocation = new Location("Mock Orangutans");
//            mockLocation.setLatitude(32.735851415117665);
//            mockLocation.setLongitude(-117.16626781198586);
        }
        else{
            Log.d("null input", "User exhibits was null");
            throw new NullPointerException("UserExhibits was null");
        }

        setUpLocationListener();
    }

    @SuppressLint("MissingPermission")
    private void setUpLocationListener() {
        var provider = LocationManager.GPS_PROVIDER;
        var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        var locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if( replanAlertShown || backwards ) {
                    return;
                }
                locationToUse = (mockLocation == null) ? location : mockLocation;
                Log.d("Location", String.format("Location changed: %s", locationToUse));
                var subListSize = (currIndex >= userListShortestOrder.size()-2) ?
                        userListShortestOrder.size() : userListShortestOrder.size()-1;
                exhibitLocations.setupExhibitLocations(userListShortestOrder
                        .subList(currIndex+1, subListSize));
                Log.d("Location", "" + currIndex);
                var nearestZooNode =
                        exhibitLocations.getZooNodeClosestToCurrentLocation(locationToUse);
                graphPath = algorithm.runPathAlgorithm(nearestZooNode,
                        exhibitLocations.exhibitsSubList);
                var closestExhibitId = algorithm.getClosestExhibitId();

                var displayId = (display.group_id != null) ? display.group_id : display.id;

                if( !closestExhibitId.equals(displayId) && canCheckReplan ) {
                    Log.d("Check Location", "New Location: " + closestExhibitId
                            + " / Old Location: " + displayId);
                    if( !recentlyYesReplan )
                        promptReplan();
                    if( check ) {
                        // Rerun algorithm from current location
//                        currIndex = 0;
                        Log.d("Location", exhibitLocations.exhibitsSubList.toString());
                        Log.d("Check Location", nearestZooNode.toString());
                        Log.d("Check Location", ""+currIndex);
                        var reorderedExhibits = algorithm
                                .runChangedLocationAlgorithm(nearestZooNode,
                                userListShortestOrder.subList(currIndex+1,
                                        userListShortestOrder.size()-1));
                        Log.d("Check Location", "New Graph Path: " + reorderedExhibits.toString());
                        var originalVisitedExhibits =
                                graphPaths.subList(0, currIndex);
                        Log.d("Check Location", "Old Graph Path: " + originalVisitedExhibits);
                        graphPaths = Stream.concat(originalVisitedExhibits.stream(),
                                reorderedExhibits.stream()).collect(Collectors.toList());
                        Log.d("Check Location", "Graph Plan Replan: " + graphPaths.toString());
//                        graphPaths = algorithm.runChangedLocationAlgorithm(nearestZooNode,
//                                exhibitLocations.exhibitsSubList.subList(1,
//                                        exhibitLocations.exhibitsSubList.size()));
                        var reorderedShortestOrder = algorithm.getNewUserListShortestOrder();
                        Log.d("Check Location", "New Order: " + reorderedShortestOrder.toString());
                        var originalVisitedShortestOrder = userListShortestOrder
                                .subList(0, currIndex+1);
                        Log.d("Check Location", "Old Beginning: " + originalVisitedShortestOrder.toString());
                        userListShortestOrder = Stream.concat(originalVisitedShortestOrder.stream(),
                                reorderedShortestOrder.stream()).collect(Collectors.toList());
                        Log.d("Check Location", "Replan Complete: " + userListShortestOrder.toString());
//                        userListShortestOrder = algorithm.getNewUserListShortestOrder();
                        Log.d("Location", userListShortestOrder.toString());
                        subListSize = (currIndex >= userListShortestOrder.size() - 2) ?
                                userListShortestOrder.size() : userListShortestOrder.size() - 1;
                        exhibitLocations.setupExhibitLocations(userListShortestOrder
                                .subList(currIndex+1, subListSize));
                        Log.d("Location", exhibitLocations.exhibitsSubList.toString());
                        nearestZooNode =
                                exhibitLocations.getZooNodeClosestToCurrentLocation(locationToUse);
                        graphPath = algorithm.runPathAlgorithm(nearestZooNode,
                                exhibitLocations.exhibitsSubList);

                        if(directionsDetailedText) {
                            setDetailedDirectionsText(graphPath);
                        } else {
                            setBriefDirectionsText(graphPath);
                        }
//                        previous.setVisibility(View.INVISIBLE);
                        check = false;
                        recentlyYesReplan = false;
                    }
                } else {
                    graphPath = algorithm.runPathAlgorithm(nearestZooNode,
                            exhibitLocations.exhibitsSubList.subList(0, 1));
                    if(directionsDetailedText) {
                        setDetailedDirectionsText(graphPath);
                    } else {
                        setBriefDirectionsText(graphPath);
                    }
                }
            }
        };

        locationManager.requestLocationUpdates(provider, 0, 0f,
                locationListener);
    }

    private void resetMockLocation() {
        mockLocation = null;
    }

    public void setMockLocation(Location mockLocation) {
        this.mockLocation = mockLocation;
    }

    public void promptReplan() {
        replanAlertShown = true;
        alertMessage = Utilities.optionalAlert(this,
                    "Would You like to Replan your Route?");
        alertMessage.show();
        return;
    }

    /**
     * Method for when the next button is clicked
     *
     * @param view The current view
     */
    public void onNextButtonClicked(View view) {
        if(locationToUse == null) {
            runOnUiThread(() -> {
                alertMessage = Utilities.showAlert(this,"Please wait until " +
                        "your location has started updating.");
                alertMessage.show();
                //alertMessage.isShowing();
            });
            return;
        }
        //check to see if index is at the end
        if(currIndex >= userListShortestOrder.size()-2){
            runOnUiThread(() -> {
                alertMessage = Utilities.showAlert(this,"The Route is Completed");
                alertMessage.show();
                //alertMessage.isShowing();
            });
            return;
        }
        //else if index is not at end, increment
        if (currIndex < userListShortestOrder.size() - 1){
            currIndex++;
        }
        backwards = false;
        //making previous button visible after 1st exhibit
        previous.setVisibility(View.VISIBLE);

        // set text
        graphPath = (currIndex >= userListShortestOrder.size()-2) ? algorithm.runPathAlgorithm(
                exhibitLocations.getZooNodeClosestToCurrentLocation(locationToUse),
                userListShortestOrder.subList(userListShortestOrder.size()-1,
                        userListShortestOrder.size())) : algorithm.runPathAlgorithm(
                                exhibitLocations.getZooNodeClosestToCurrentLocation(locationToUse),
                userListShortestOrder.subList(currIndex + 1,
                        userListShortestOrder.size() - 1));

        switch(currIndex) {
            case 1:
                mockLocation = new Location("Mock Crocs");
                mockLocation.setLatitude(32.745293428608484);
                mockLocation.setLongitude(-117.16976102878033);
                break;
            case 2:
                mockLocation = new Location("Mock Dove");
                mockLocation.setLatitude(32.73697286273083);
                mockLocation.setLongitude(-117.17319785958958);
                break;
            case 3:
                mockLocation = new Location("Mock Fern");
                mockLocation.setLatitude(32.7337949159672);
                mockLocation.setLongitude(-117.1769866067953);
                break;
            default:
                break;
        }
        if(directionsDetailedText) {
            setDetailedDirectionsText(graphPath);
        } else {
            setBriefDirectionsText(graphPath);
        }

        canCheckReplan = true;
    }

    /**
     * Method to handle when the previous button is clicked
     *
     * @param view The current view
     */
    public void onPreviousButtonClicked(View view) {
        // make previous button invisible if we go back to 1st exhibit
        Button previous = findViewById(R.id.previous_button);
        if (currIndex == 1){
            previous.setVisibility(View.INVISIBLE);
        }
        // else if curr index is not 0, decrement on click
        if (currIndex > 0){
            currIndex--;
        }
        switch(currIndex) {
            case 0:
                mockLocation = new Location("Mock Dove");
                mockLocation.setLatitude(32.73697286273083);
                mockLocation.setLongitude(-117.17319785958958);
                break;
            case 1:
                mockLocation = new Location("Mock Fern");
                mockLocation.setLatitude(32.7337949159672);
                mockLocation.setLongitude(-117.1769866067953);
                break;
            case 2:
                mockLocation = new Location("Mock Entrance");
                mockLocation.setLatitude(32.73459618734685);
                mockLocation.setLongitude(-117.14936);
                break;
            default:
                break;
        }
        backwards = true;
        //set Text
        graphPath = algorithm.runReversePathAlgorithm(exhibitLocations
                        .getZooNodeClosestToCurrentLocation(mockLocation), // Change mockLocation to locationToUse when actually running app
                userListShortestOrder.get(currIndex+1));
        if(directionsDetailedText) {
            setDetailedDirectionsText(graphPath);
        } else {
            setBriefDirectionsText(graphPath);
        }
        canCheckReplan = true;
    }

    /**
     * Sets the text for the directions activity
     */
    @SuppressLint("DefaultLocale")
    private void setDetailedDirectionsText(
            GraphPath<String, IdentifiedWeightedEdge> directionsToExhibit) {
        // Get the needed zoo node information
        var current = userListShortestOrder.get(currIndex);
        display = userListShortestOrder.get(currIndex+1);

        // Set the header to the correct display name
        header.setText(display.name);

        // Set up for getting all the directions
        var i = 1;
        String source, target, correctTarget, start, direction = "";
        start = (current.group_id != null) ? zooNodeDao.getById(current.group_id).name :
                current.name;
        var edgeList = directionsToExhibit.getEdgeList();

        if( edgeList.isEmpty() ) {
            direction += String.format("The %s are nearby", display.name);
        }

        // Testing purposes
        Log.d("Edge Format", start);

        // Get all the directions from current zoo node to the next zoo node
        for(var e: edgeList) {
            Log.d("Edge Format", e.toString());
            source = Objects.requireNonNull(vInfo.get(graph.getEdgeSource(e).toString())).name;
            target = Objects.requireNonNull(vInfo.get(graph.getEdgeTarget(e).toString())).name;
            correctTarget = (source.equals(start)) ? target : source;
            Log.d("Edge Format", correctTarget);

            if( i == edgeList.size() && display.group_id != null ) {
                direction += String.format(" %d. Walk %.0f feet along %s towards the '%s' and " +
                                "find '%s' inside\n",
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
    private void setBriefDirectionsText(
            GraphPath<String, IdentifiedWeightedEdge> directionsToExhibit) {
        var current = userListShortestOrder.get(currIndex);
        display = userListShortestOrder.get(currIndex+1);

        // Set the header to the correct display name
        header.setText(display.name);

        // Set up for getting all the directions
        var directionNumber = 1;
        String source, target, correctTarget, start, direction = "";
        double distance = 0.0;
        start = current.name;
        var edgeList = directionsToExhibit.getEdgeList();

        if( edgeList.isEmpty() ) {
            direction += String.format("The %s are nearby", display.name);
        }

        // Testing purposes
        Log.d("Edge Format", start);

        // Get all the directions from current zoo node to the next zoo node
        for(int j = 0; j < edgeList.size(); j++) {
            var e = edgeList.get(j);
            distance += graph.getEdgeWeight(e);
            if( j != edgeList.size()-1 &&
                    Objects.requireNonNull(eInfo.get(e.getId())).street
                            .equals(Objects.requireNonNull(eInfo.get(edgeList.get(j+1).getId()))
                                    .street))  {
                continue;
            }
            Log.d("Edge Format", e.toString());
            source = Objects.requireNonNull(vInfo.get(graph.getEdgeSource(e).toString())).name;
            target = Objects.requireNonNull(vInfo.get(graph.getEdgeTarget(e).toString())).name;
            correctTarget = (source.equals(start)) ? target : source;
            Log.d("Edge Format", correctTarget);

            if( j == edgeList.size()-1 && display.group_id != null ) {
                direction += String.format(" %d. Walk %.0f meters along %s towards the '%s' and " +
                                "find '%s' inside\n",
                        directionNumber,
                        distance,
                        Objects.requireNonNull(eInfo.get(e.getId())).street,
                        correctTarget,
                        display.name);
            } else {
                // Format directions to proper format
                direction += String.format(" %d. Walk %.0f meters along %s towards the '%s'\n",
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
     * Loads graph information from files. Initializes graph, vInfo, and eInfo instance variables.
     */
    private void loadGraph() {
        // For loading in resources
        var context = getApplication().getApplicationContext();

        // 1. Load the graph...
        graph = ZooData.loadZooGraphJSON(context, ZOO_GRAPH_JSON);

        // 2. Load the information about our nodes and edges...
        vInfo = ZooData.loadVertexInfoJSON(context, NODE_INFO_JSON);
        eInfo = ZooData.loadEdgeInfoJSON(context, EDGE_INFO_JSON);
    }

}