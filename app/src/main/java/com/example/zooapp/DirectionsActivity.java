package com.example.zooapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public static boolean directionsDetailedText;

    @VisibleForTesting
    public Location mockLocation;
    @VisibleForTesting
    public Location locationToUse;

    // Graph Information Files
    final String ZOO_GRAPH_JSON = "sample_zoo_graph.json";
    final String NODE_INFO_JSON = "sample_node_info.json";
    final String EDGE_INFO_JSON = "sample_edge_info.json";

    public List<ZooNode> userListShortestOrder;

    // Variable for the graph and path
    public GraphAlgorithm algorithm;
    public ActionBar actionBar;
    public AlertDialog alertMessage;
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
    private Button previous, skip;
    private boolean backwards = false;
    private ZooNode previousClosestZooNode;
    private SharedPreferences preferences;

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

        // Get boolean, default false
        preferences = getSharedPreferences("DIRECTIONS", MODE_PRIVATE);
        directionsDetailedText = preferences.getBoolean("toggled", false);
        Log.d("Boolean Direction", "" + directionsDetailedText);

        // Set the Title Bar to Directions
        actionBar = getSupportActionBar();
        actionBar.setTitle("Directions");

        //Access the previous and skip buttons
        previous = findViewById(R.id.previous_button);
        skip = findViewById(R.id.skip_button);

        // Grabbing planned animals from planned list and inputting to new activity
        if( plannedAnimalDao.getAll().size() > 0 ){
            Log.d("Zoo Nodes", plannedAnimalDao.getAll().toString());
            loadGraph(); // will initialize graph, vInfo, and eInfo variables

            // Our old algorithm
            algorithm = new ShortestPathZooAlgorithm(
                    getApplication().getApplicationContext(), plannedAnimalDao.getAll());
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
            // Used for live testing
            mockLocation = new Location("Mock Entrance");
            mockLocation.setLatitude(32.73459618734685);
            mockLocation.setLongitude(-117.14936);
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
                if( replanAlertShown ) {
                    return;
                }
                locationToUse = (mockLocation == null) ? location : mockLocation;
                Log.d("Location", String.format("Location changed: %s", locationToUse));
                if( backwards ) {
                    graphPath = algorithm.runReversePathAlgorithm(exhibitLocations
                                    .getZooNodeClosestToCurrentLocation(locationToUse),
                            userListShortestOrder.get(currIndex+1));
                    if(directionsDetailedText) {
                        setDetailedDirectionsText(graphPath);
                    } else {
                        setBriefDirectionsText(graphPath);
                    }
                    return;
                }
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
                        Log.d("Location", exhibitLocations.exhibitsSubList.toString());
                        Log.d("Check Location", nearestZooNode.toString());
                        Log.d("Check Location", ""+currIndex);

                        // Get the new List of graph paths with the remaining exhibits
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

                        // Get the new List of zoo nodes in the new shortest order of the remaining
                        // exhibits
                        Log.d("Check Location", "Graph Plan Replan: " + graphPaths.toString());
                        var reorderedShortestOrder = algorithm.getNewUserListShortestOrder();
                        Log.d("Check Location", "New Order: " + reorderedShortestOrder.toString());
                        var originalVisitedShortestOrder = userListShortestOrder
                                .subList(0, currIndex+1);
                        Log.d("Check Location", "Old Beginning: " + originalVisitedShortestOrder.toString());
                        userListShortestOrder = Stream.concat(originalVisitedShortestOrder.stream(),
                                reorderedShortestOrder.stream()).collect(Collectors.toList());
                        Log.d("Check Location", "Replan Complete: " + userListShortestOrder.toString());
                        Log.d("Location", userListShortestOrder.toString());

                        // Set up for the exhibitLocations class
                        subListSize = (currIndex >= userListShortestOrder.size() - 2) ?
                                userListShortestOrder.size() : userListShortestOrder.size() - 1;
                        exhibitLocations.setupExhibitLocations(userListShortestOrder
                                .subList(currIndex+1, subListSize));
                        Log.d("Check Location", exhibitLocations.exhibitsSubList.toString());
                        nearestZooNode =
                                exhibitLocations.getZooNodeClosestToCurrentLocation(locationToUse);

                        // Find the new path to display
                        graphPath = algorithm.runPathAlgorithm(nearestZooNode,
                                exhibitLocations.exhibitsSubList);

                        if(directionsDetailedText) {
                            setDetailedDirectionsText(graphPath);
                        } else {
                            setBriefDirectionsText(graphPath);
                        }
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
     * Creates the custom menu bar
     *
     * @param menu Menu
     * @return True for creating the menu bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.directions_menu, menu);
        return true;
    }

    /**
     * Checks when an item on the menu has been clicked
     *
     * @param item Item that has been clicked
     * @return Result of that item being clicked
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.directions_settings_button:
                Log.d("Menu Click", "Settings has been clicked");
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
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

        skipButtonVisibilityCheck();

        // set text
        graphPath = (currIndex >= userListShortestOrder.size()-2) ? algorithm.runPathAlgorithm(
                exhibitLocations.getZooNodeClosestToCurrentLocation(locationToUse),
                userListShortestOrder.subList(userListShortestOrder.size()-1,
                        userListShortestOrder.size())) : algorithm.runPathAlgorithm(
                                exhibitLocations.getZooNodeClosestToCurrentLocation(locationToUse),
                userListShortestOrder.subList(currIndex + 1,
                        userListShortestOrder.size() - 1));

        // Used for live testing
//        switch(currIndex) {
//            case 1:
//                mockLocation = new Location("Mock Flamingos");
//                mockLocation.setLatitude(32.7440416465169);
//                mockLocation.setLongitude(-117.15952052282296);
//                break;
//            case 2:
//                mockLocation = new Location("Mock Gorillas");
//                mockLocation.setLatitude(32.74711745394194);
//                mockLocation.setLongitude(-117.18047982358976);
//                break;
//            case 3:
//                mockLocation = new Location("Mock Orangutans");
//                mockLocation.setLatitude(32.735851415117665);
//                mockLocation.setLongitude(-117.16626781198586);
//                break;
//            default:
//                break;
//        }
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

        skipButtonVisibilityCheck();

        // Used for live testing
//        switch(currIndex) {
//            case 0:
//                mockLocation = new Location("Mock Gorillas");
//                mockLocation.setLatitude(32.74711745394194);
//                mockLocation.setLongitude(-117.18047982358976);
//                break;
//            case 1:
//                mockLocation = new Location("Mock Orangutans");
//                mockLocation.setLatitude(32.735851415117665);
//                mockLocation.setLongitude(-117.16626781198586);
//                break;
//            case 2:
//                mockLocation = new Location("Mock Entrance");
//                mockLocation.setLatitude(32.73459618734685);
//                mockLocation.setLongitude(-117.14936);
//                break;
//            default:
//                break;
//        }
        backwards = true;
        //set Text
        graphPath = algorithm.runReversePathAlgorithm(exhibitLocations
                        .getZooNodeClosestToCurrentLocation(locationToUse),
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
        var current = (locationToUse == null) ? userListShortestOrder.get(currIndex) :
                exhibitLocations.getZooNodeClosestToCurrentLocation(locationToUse);
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
    private void setBriefDirectionsText(
            GraphPath<String, IdentifiedWeightedEdge> directionsToExhibit) {
        var current = (locationToUse == null) ? userListShortestOrder.get(currIndex) :
                exhibitLocations.getZooNodeClosestToCurrentLocation(locationToUse);
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
            source = Objects.requireNonNull(vInfo.get(graph.getEdgeSource(e).toString())).name;
            target = Objects.requireNonNull(vInfo.get(graph.getEdgeTarget(e).toString())).name;
            Log.d("Directions", "Start: " + start + ", Source: " + source + ", Target: "
                    + target);
            if( j != edgeList.size()-1 &&
                    Objects.requireNonNull(eInfo.get(e.getId())).street
                            .equals(Objects.requireNonNull(eInfo.get(edgeList.get(j+1).getId()))
                                    .street))  {
                start = (source.equals(start)) ? target : source;
                continue;
            }
            Log.d("Edge Format", e.toString());
            correctTarget = (source.equals(start)) ? target : source;
            Log.d("Edge Format", correctTarget);

            if( j == edgeList.size()-1 && display.group_id != null ) {
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

    /**
     * Skips exhibit currently navigating to and moves onto next exhibit
     *
     * @param view
     */
    public void onSkipButtonClicked(View view) {
        if(locationToUse == null) {
            runOnUiThread(() -> {
                alertMessage = Utilities.showAlert(this,"Please wait until " +
                        "your location has started updating.");
                alertMessage.show();
                //alertMessage.isShowing();
            });
            return;
        }

        Log.d("SkipButton", "Skip Button Clicked");
        Log.d("SkipButton", "List planned animal BEFORE: " + plannedAnimalDao.getAll().toString());
        Log.d("SkipButton", "Current view exhibit: " + userListShortestOrder.get(currIndex+1).toString());
        plannedAnimalDao.delete(userListShortestOrder.get(currIndex+1));

        var nearestZooNode =
                exhibitLocations.getZooNodeClosestToCurrentLocation(locationToUse);

        var reorderedExhibits = algorithm
                .runChangedLocationAlgorithm(nearestZooNode,
                        userListShortestOrder.subList(currIndex+2,
                                userListShortestOrder.size()-1));
        Log.d("Check Location", "New Graph Path: " + reorderedExhibits.toString());
        var originalVisitedExhibits =
                graphPaths.subList(0, currIndex);
        Log.d("Check Location", "Old Graph Path: " + originalVisitedExhibits);
        graphPaths = Stream.concat(originalVisitedExhibits.stream(),
                reorderedExhibits.stream()).collect(Collectors.toList());

        // Get the new List of zoo nodes in the new shortest order of the remaining
        // exhibits
        Log.d("Check Location", "Graph Plan Replan: " + graphPaths.toString());
        var reorderedShortestOrder = algorithm.getNewUserListShortestOrder();
        Log.d("Check Location", "New Order: " + reorderedShortestOrder.toString());
        var originalVisitedShortestOrder = userListShortestOrder
                .subList(0, currIndex+1);
        Log.d("Check Location", "Old Beginning: " + originalVisitedShortestOrder.toString());
        userListShortestOrder = Stream.concat(originalVisitedShortestOrder.stream(),
                reorderedShortestOrder.stream()).collect(Collectors.toList());
        Log.d("Check Location", "Replan Complete: " + userListShortestOrder.toString());
        Log.d("Location", userListShortestOrder.toString());

        // Set up for the exhibitLocations class
        var subListSize = (currIndex >= userListShortestOrder.size() - 2) ?
                userListShortestOrder.size() : userListShortestOrder.size() - 1;
        exhibitLocations.setupExhibitLocations(userListShortestOrder
                .subList(currIndex+1, subListSize));
        Log.d("Check Location", exhibitLocations.exhibitsSubList.toString());
        nearestZooNode =
                exhibitLocations.getZooNodeClosestToCurrentLocation(locationToUse);

        // Find the new path to display
        graphPath = algorithm.runPathAlgorithm(nearestZooNode,
                exhibitLocations.exhibitsSubList);

        if(directionsDetailedText) {
            setDetailedDirectionsText(graphPath);
        } else {
            setBriefDirectionsText(graphPath);
        }

        Log.d("SkipButton", "List planned animal AFTER: " + plannedAnimalDao.getAll().toString());
    }


    /**
     * Clears animals from planned list and returns to home screen
     *
     * @param view the current view
     */
    public void onStartOverButtonClicked(View view) {
        Log.d("StartOverButton", "Start Over Button Clicked");

        PlannedAnimalDatabase.getSingleton(this).plannedAnimalDao().deleteAll();
        Log.d("StartOverButton", "Cleared Planned Animal Dao");
        Log.d("StartOverButton", "Heading back to main activity");

        // Go back to main activity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // clear stack of activities
        startActivity(intent);
    }


    private void skipButtonVisibilityCheck() {
        // Check if the currIndex is at "Entrance and Exit" node
        // If so, the skip button is not visible
        // Otherwise, setVisible
        if (currIndex == userListShortestOrder.size() - 2)
            skip.setVisibility(View.INVISIBLE);
        else
            skip.setVisibility(View.VISIBLE);
    }
}