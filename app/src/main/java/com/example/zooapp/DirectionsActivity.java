package com.example.zooapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
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
    int currIndex = 0;
    String[] sample = {"Polar Bear Street", "Alligator Street", "Tiger Street"};
    String[] sample2 = {"Polar Bears", "Alligators", "Tigers"};
    public final String head = "Directions to ";
    public final String proceed = "Proceed to ";

    // Variable for the graph and path
    private Graph<String, IdentifiedWeightedEdge> graph;
    //private GraphPath<String, IdentifiedWeightedEdge> path;
    private Map<String, ZooData.VertexInfo> vInfo;
    private Map<String, ZooData.EdgeInfo> eInfo;
    private List<IdentifiedWeightedEdge> pathEdgeList;
    public AlertDialog alertMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        loadGraph(); // will initialize graph, vInfo, and eInfo variables
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
        if(currIndex == sample.length -1){
            runOnUiThread(() -> {
                alertMessage = Utilities.showAlert(this,"The Route is Completed");
                alertMessage.show();
                //alertMessage.isShowing();
            });
        }
        if (currIndex < sample.length - 1){
            currIndex++;
        }
        Button previous = findViewById(R.id.previous_button);
        previous.setVisibility(View.VISIBLE);

        // Get string of directions at edge of currIndex from pathEdgeList
        String directionsText = getDirectionsAtEdge(currIndex);
        String nextNode = nextNodeNameAtEdge(currIndex);

        TextView directions = findViewById(R.id.directions_text);
        TextView header = findViewById(R.id.directions_header);
        header.setText(head + nextNode);
        directions.setText(directionsText);
        //directions.setText(proceed +sample[currIndex]);
    }

    public void onPreviousButtonClicked(View view) {
        Button previous = findViewById(R.id.previous_button);
        if(currIndex == 0){
            runOnUiThread(() -> {
                Utilities.showAlert(this,"Can't go back!");
            });

        }
        if (currIndex == 1){
            previous.setVisibility(View.INVISIBLE);
        }
        if (currIndex > 0){
            currIndex--;
        }

        // Get string of directions at edge of currIndex from pathEdgeList
        String directionsText = getDirectionsAtEdge(currIndex);
        String nextNode = nextNodeNameAtEdge(currIndex);

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
        graph = ZooData.loadZooGraphJSON(context, "sample_zoo_graph.json");

        // 2. Load the information about our nodes and edges...
        vInfo = ZooData.loadVertexInfoJSON(context, "sample_node_info.json");
        eInfo = ZooData.loadEdgeInfoJSON(context, "sample_edge_info.json");
    }

    private GraphPath<String, IdentifiedWeightedEdge> getDijkstraExamplePath(Graph<String, IdentifiedWeightedEdge> graph) {
        // "source" and "sink" are graph terms for the start and end
        String start = "entrance_exit_gate";
        String goal = "elephant_odyssey";

        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(graph, start, goal);
        return path;
    }

    /**
     * Get's a formatted string of directions
     * NOTE: Requires graph, eInfo, vInfo, and pathEdgeList to be class variables
     * @param currIndex the index of the edge in the path you want directions for
     * @return String of the directions at this path
     */
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
        String toNode = Objects.requireNonNull(vInfo.get(graph.getEdgeTarget(edge).toString())).name;
        return toNode;
    }
}