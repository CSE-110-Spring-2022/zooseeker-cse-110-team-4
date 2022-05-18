package com.example.zooapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    private Graph<String, IdentifiedWeightedEdge> graph;
    private TextView header, directions;
    private Map<String, ZooData.VertexInfo> vInfo;
    private Map<String, ZooData.EdgeInfo> eInfo;
    private List<GraphPath<String, IdentifiedWeightedEdge>> graphPaths;
    public AlertDialog alertMessage;
    public ActionBar actionBar;

    /**
     * Method for onCreate of the activity
     *
     * @param savedInstanceState State of activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        // Set the Title Bar to Zoo Seeker
        actionBar = getSupportActionBar();
        actionBar.setTitle("Directions");

        // grabbing planned animals from planned list and inputting to new activity
        Gson gson = new Gson();
        Type type = new TypeToken<List<ZooNode>>(){}.getType();
        if(gson.fromJson(getIntent().getStringExtra("ListOfAnimals"), type) != null){
            userExhibits = gson.fromJson(getIntent().getStringExtra("ListOfAnimals"), type);

            loadGraph(); // will initialize graph, vInfo, and eInfo variables
            // Inputs to algorithm: context, usersList

            // Our algorithm
            GraphAlgorithm algorithm = new ShortestPathZooAlgorithm(
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
        }
    }

    /**
     * Method for when the next button is clicked
     *
     * @param view The current view
     */
    public void onNextButtonClicked(View view) {
        //check to see if index is at the end
        if(currIndex == userListShortestOrder.size()-2){
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
        //making previous button visible after 1st exhbit
        Button previous = findViewById(R.id.previous_button);
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
        // Get the needed zoo node information
        ZooNode current = userListShortestOrder.get(currIndex);
        ZooNode display = userListShortestOrder.get(currIndex+1);

        // Set the header to the correct display name
        header.setText(display.name);

        // Set up for getting all the directions
        int i = 1;
        String source, target, correctTarget, start, direction = "";
        start = current.name;

        // Testing purposes
        Log.d("Edge Format", start);

        // Get all the directions from current zoo node to the next zoo node
        for(IdentifiedWeightedEdge e: directionsToExhibit.getEdgeList()) {
            Log.d("Edge Format", e.toString());
            source = Objects.requireNonNull(vInfo.get(graph.getEdgeSource(e).toString())).name;
            target = Objects.requireNonNull(vInfo.get(graph.getEdgeTarget(e).toString())).name;
            correctTarget = (source.equals(start)) ? target : source;
            Log.d("Edge Format", correctTarget);

            // Format directions to proper format
            direction += String.format(" %d. Walk %.0f meters along %s towards the '%s'\n",
                    i,
                    graph.getEdgeWeight(e),
                    Objects.requireNonNull(eInfo.get(e.getId())).street,
                    correctTarget);
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
        Context context = getApplication().getApplicationContext();

        // 1. Load the graph...
        graph = ZooData.loadZooGraphJSON(context, ZOO_GRAPH_JSON);

        // 2. Load the information about our nodes and edges...
        vInfo = ZooData.loadVertexInfoJSON(context, NODE_INFO_JSON);
        eInfo = ZooData.loadEdgeInfoJSON(context, EDGE_INFO_JSON);
    }
}