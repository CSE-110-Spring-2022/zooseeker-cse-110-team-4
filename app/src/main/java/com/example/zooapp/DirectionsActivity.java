package com.example.zooapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DirectionsActivity extends AppCompatActivity {
    //index that is incremented/decremented by next/back buttons
    //used to traverse through planned exhibits
    int currIndex = 0;
    //string used to format the directions
    public final String head = "Directions to ";

    // Graph Information Files
    final String ZOO_GRAPH_JSON = "sample_zoo_graph.json";
    final String NODE_INFO_JSON = "sample_node_info.json";
    final String EDGE_INFO_JSON = "sample_edge_info.json";

    private List<ZooNode> userExhibits;

    // Variable for the graph and path
    private Graph<String, IdentifiedWeightedEdge> graph;
    //private GraphPath<String, IdentifiedWeightedEdge> path;
    private Map<String, ZooData.VertexInfo> vInfo;
    private Map<String, ZooData.EdgeInfo> eInfo;
    public List<IdentifiedWeightedEdge> pathEdgeList;
    public AlertDialog alertMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        Bundle extras = getIntent().getExtras();
        List<ZooNode> userExhibits = (List<ZooNode>) getIntent().getSerializableExtra("userExhibits");

        loadGraph(); // will initialize graph, vInfo, and eInfo variables
        // Inputs to algorith: context, usersList

        GraphPath<String, IdentifiedWeightedEdge> path = getDijkstraExamplePath(graph); // Can replace this with our algorithm
        pathEdgeList = path.getEdgeList();

        // Get string of directions at edge of currIndex from PathEdgeList
        String directionsText = getDirectionsAtEdge(currIndex);
        String nextNode = nextNodeNameAtEdge(currIndex);

        // Set Text
        TextView header = findViewById(R.id.directions_header);
        TextView directions = findViewById(R.id.directions_text);
        header.setText(head + nextNode);
        directions.setText(directionsText);
    }


    public void onNextButtonClicked(View view) {
        //check to see if index is at the end
        if(currIndex == pathEdgeList.size() -1){
            runOnUiThread(() -> {
                alertMessage = Utilities.showAlert(this,"The Route is Completed");
                alertMessage.show();
                //alertMessage.isShowing();
            });
        }
        //else if index is not at end, increment
        if (currIndex < pathEdgeList.size()  - 1){
            currIndex++;
        }
        //making previous button visible after 1st exhbit
        Button previous = findViewById(R.id.previous_button);
        previous.setVisibility(View.VISIBLE);

        // Get string of directions at edge of currIndex from pathEdgeList
        String directionsText = getDirectionsAtEdge(currIndex);
        String nextNode = nextNodeNameAtEdge(currIndex);

        // set text
        TextView directions = findViewById(R.id.directions_text);
        TextView header = findViewById(R.id.directions_header);
        header.setText(head + nextNode);
        directions.setText(directionsText);
    }

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

        // Get string of directions at edge of currIndex from pathEdgeList
        String directionsText = getDirectionsAtEdge(currIndex);
        String nextNode = nextNodeNameAtEdge(currIndex);

        //set Text
        TextView directions = findViewById(R.id.directions_text);
        TextView header = findViewById(R.id.directions_header);
        header.setText(head + nextNode);
        directions.setText(directionsText);
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

    private GraphPath<String, IdentifiedWeightedEdge> getDijkstraExamplePath(Graph<String, IdentifiedWeightedEdge> graph) {
        // "source" and "sink" are graph terms for the start and end
        String start = "entrance_exit_gate";
        String goal = "elephant_odyssey";
        return DijkstraShortestPath.findPathBetween(graph, start, goal);
    }

    /**
     * Get's a formatted string of directions
     * NOTE: Requires graph, eInfo, vInfo, and pathEdgeList to be class variables
     * @param currIndex the index of the edge in the path you want directions for
     * @return String of the directions at this path
     */
    @SuppressLint("DefaultLocale")
    private String getDirectionsAtEdge(int currIndex) {
        IdentifiedWeightedEdge edge = pathEdgeList.get(currIndex);

        double distanceInMeters = graph.getEdgeWeight(edge);
        String streetName = Objects.requireNonNull(eInfo.get(edge.getId())).street;
        String fromNode = Objects.requireNonNull(vInfo.get(graph.getEdgeSource(edge).toString())).name;
        String toNode = Objects.requireNonNull(vInfo.get(graph.getEdgeTarget(edge).toString())).name;

        return String.format("Walk %.0f meters along %s from '%s' to '%s'",
                distanceInMeters,
                streetName,
                fromNode,
                toNode
        );
    }

    /**
     * NOTE: Requires graph, vInfo, and pathEdgeList to be class variables
     * @param currIndex the index of the edge in the path you want directions for
     * @return String of the toNode (next Exhibit) along the edge
     */
    private String nextNodeNameAtEdge(int currIndex) {
        IdentifiedWeightedEdge edge = pathEdgeList.get(currIndex);
        return Objects.requireNonNull(vInfo.get(graph.getEdgeTarget(edge).toString())).name;
    }
}