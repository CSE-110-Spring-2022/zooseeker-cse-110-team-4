package com.example.zooapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    // Graph Information Files
    final String ZOO_GRAPH_JSON = "sample_zoo_graph.json";
    final String NODE_INFO_JSON = "sample_node_info.json";
    final String EDGE_INFO_JSON = "sample_edge_info.json";

    public List<ZooNode> userExhibits, userListShortestOrder;

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
    private TextView header, directions;
    private Map<String, ZooData.VertexInfo> vInfo;
    private Map<String, ZooData.EdgeInfo> eInfo;
    private List<GraphPath<String, IdentifiedWeightedEdge>> graphPaths;
    private ZooNode display;
    private Button previous;
    private Button skip;


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

        // Set the Title Bar to Directions
        actionBar = getSupportActionBar();
        actionBar.setTitle("Directions");

        previous = findViewById(R.id.previous_button);
        skip = findViewById(R.id.skip_button);

        // Grabbing planned animals from planned list and inputting to new activity
        var gson = new Gson();
        var type = new TypeToken<List<ZooNode>>(){}.getType();
        //old if block code: gson.fromJson(getIntent().getStringExtra("ListOfAnimals"), type) != null

        if(plannedAnimalDao.getAll().size() > 0){
            //userExhibits = gson.fromJson(getIntent().getStringExtra("ListOfAnimals"), type);
            userExhibits = plannedAnimalDao.getAll();
            Log.d("Zoo Nodes", userExhibits.toString());

            loadGraph(); // will initialize graph, vInfo, and eInfo variables

            // Our algorithm
            algorithm = new ShortestPathZooAlgorithm(
                    getApplication().getApplicationContext(), userExhibits);
            graphPaths = algorithm.runAlgorithm();
            userListShortestOrder = algorithm.getUserListShortestOrder();

            // Set text views
            header = findViewById(R.id.directions_header);
            directions = findViewById(R.id.directions_text);

            setDirectionsText(graphPaths.get(currIndex));
        }
        else{
            Log.d("null input", "User exhibits was null");
            throw new NullPointerException("UserExhibits was null");
        }


        var provider = LocationManager.GPS_PROVIDER;
        var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        var locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d("Location", String.format("Location changed: %s", location));
                exhibitLocations.setupExhibitLocations(userListShortestOrder
                        .subList(currIndex+1, userListShortestOrder.size()-1));
                Location currentExhibit = exhibitLocations.getZooNodeLocation(display),
                        minExhibit = currentExhibit;
                if( currentExhibit == null )
                    return;

                Log.d("Location", String.format("Current Exhibit Location: %s", currentExhibit));
                double minDistance = location.distanceTo(currentExhibit);
                Log.d("Location", String.format("Calculated Distance: %.2f", minDistance));
                for(Location exhibitLocation: exhibitLocations.exhibitLocations) {
                    if( location.distanceTo(exhibitLocation) < minDistance ) {
                        minDistance = location.distanceTo(exhibitLocation);
                        minExhibit = exhibitLocation;
                    }
                }
                Log.d("Location", String.format("Min Exhibit Location: %s", minExhibit));

                if( !minExhibit.getProvider().equals(currentExhibit.getProvider()) ) {
                    promptReplan();
                    Log.d("Location", "New Location: " + minExhibit.getProvider()
                            + " / Old Location: " + currentExhibit.getProvider());
                    // Rerun algorithm from current location
//                    graphPaths = algorithm.runChangedLocationAlgorithm(zooNodeDao.getById(
//                            minExhibit.getProvider()), exhibitLocations.exhibitsSubList);
//                    userListShortestOrder = algorithm.getNewUserListShortestOrder();
//                    currIndex = 0;
//                    setDirectionsText(graphPaths.get(currIndex));
//                    previous.setVisibility(View.INVISIBLE);
                }
            }
        };

        locationManager.requestLocationUpdates(provider, 0, 0f,
                locationListener);
    }

    public void promptReplan() {

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

        //check to see if index is at the end
        if(currIndex == userListShortestOrder.size()-2){
            alertMessage = Utilities.showAlert(this,"The Route is Completed");
            alertMessage.show();
            return;
        }
        //else if index is not at end, increment
        if (currIndex < userListShortestOrder.size() - 1){
            currIndex++;
        }
        //making previous button visible after 1st exhibit
        previous.setVisibility(View.VISIBLE);

        // set text
        setDirectionsText(graphPaths.get(currIndex));
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

        //set Text
        setDirectionsText(graphPaths.get(currIndex));
    }

    /**
     * Sets the text for the directions activity
     */
    @SuppressLint("DefaultLocale")
    private void setDirectionsText(GraphPath<String, IdentifiedWeightedEdge> directionsToExhibit) {

        // Check if the currIndex is at the end of the list
        // If so, the skip button is not visible
        // Otherwise, setVisible
        if (currIndex == userListShortestOrder.size() - 2)
            skip.setVisibility(View.INVISIBLE);
        else
            skip.setVisibility(View.VISIBLE);

        // Get the needed zoo node information
        var current = userListShortestOrder.get(currIndex);
        display = userListShortestOrder.get(currIndex+1);

        // Set the header to the correct display name
        header.setText(display.name);

        // Set up for getting all the directions
        var i = 1;
        String source, target, correctTarget, start, direction = "";
        start = current.name;
        var edgeList = directionsToExhibit.getEdgeList();

        // Testing purposes
        Log.d("Edge Format", start);

        // Get all the directions from current zoo node to the next zoo node
        for(var e: edgeList) {
            Log.d("Edge Format", e.toString());
            source = Objects.requireNonNull(vInfo.get(graph.getEdgeSource(e).toString())).name;
            target = Objects.requireNonNull(vInfo.get(graph.getEdgeTarget(e).toString())).name;
            correctTarget = (source.equals(start)) ? target : source;
            Log.d("Edge Format", correctTarget);

            if( i == edgeList.size() && display.parent_id != null ) {
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
     * @param view
     */
    public void onSkipButtonClicked(View view) {


        Log.d("SkipButton", "Skip Button Clicked");
        Log.d("SkipButton", "List planned animal BEFORE: " + userExhibits.toString());
        Log.d("SkipButton", "Current view exhibit: " + userListShortestOrder.get(currIndex+1).toString());
        plannedAnimalDao.delete(userListShortestOrder.get(currIndex+1));

        userExhibits = plannedAnimalDao.getAll();
        // Our algorithm
        algorithm = new ShortestPathZooAlgorithm(
                getApplication().getApplicationContext(), userExhibits);
        graphPaths = algorithm.runAlgorithm();
        userListShortestOrder = algorithm.getUserListShortestOrder();


        // set text
        setDirectionsText(graphPaths.get(currIndex));

        Log.d("SkipButton", "List planned animal AFTER: " + userExhibits.toString());
    }

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
}