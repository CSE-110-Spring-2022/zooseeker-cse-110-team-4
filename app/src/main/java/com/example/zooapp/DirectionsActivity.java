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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DirectionsActivity extends AppCompatActivity {
    int currIndex = 0;
    String[] sample = {"Polar Bear Street", "Alligator Street", "Tiger Street"};
    String[] sample2 = {"Polar Bears", "Alligators", "Tigers"};
    public final String head = "Directions to ";
    public final String proceed = "Proceed to ";

    // --Created for implementing Dijkstra's path--
    private Graph<String, IdentifiedWeightedEdge> graph;
    private GraphPath<String, IdentifiedWeightedEdge> path;
    private Map<String, ZooData.VertexInfo> vInfo;
    private Map<String, ZooData.EdgeInfo> eInfo;
    private List<IdentifiedWeightedEdge> pathEdgeList;
    public AlertDialog alertMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        TextView header = findViewById(R.id.directions_header);
        TextView directions = findViewById(R.id.directions_text);
        header.setText(head + sample2[currIndex]);



        // --Created for implementing Dijkstra's path--
        Context context = getApplication().getApplicationContext();

        // "source" and "sink" are graph terms for the start and end
        String start = "entrance_exit_gate";
        String goal = "elephant_odyssey";

        // 1. Load the graph...
        graph = ZooData.loadZooGraphJSON(context, "sample_zoo_graph.json");
        path = DijkstraShortestPath.findPathBetween(graph, start, goal);

        // 2. Load the information about our nodes and edges...
        vInfo = ZooData.loadVertexInfoJSON(context, "sample_node_info.json");
        eInfo = ZooData.loadEdgeInfoJSON(context, "sample_edge_info.json");

        // create ArrayList for all edges in path
        pathEdgeList = path.getEdgeList();
        IdentifiedWeightedEdge edge = pathEdgeList.get(currIndex);
        double distanceInMeters = graph.getEdgeWeight(edge);
        String streetName = eInfo.get(edge.getId()).street;
        String fromNode = vInfo.get(graph.getEdgeSource(edge).toString()).name;
        String toNode = vInfo.get(graph.getEdgeTarget(edge).toString()).name;
        String text = String.format("Walk %.0f meters along %s from '%s' to '%s'",
                distanceInMeters,
                streetName,
                fromNode,
                toNode
        );
        directions.setText(text);
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

        // Getting information about current edge from Path
        IdentifiedWeightedEdge edge = pathEdgeList.get(currIndex);
        double distanceInMeters = graph.getEdgeWeight(edge);
        String streetName = eInfo.get(edge.getId()).street;
        String fromNode = vInfo.get(graph.getEdgeSource(edge).toString()).name;
        String toNode = vInfo.get(graph.getEdgeTarget(edge).toString()).name;
        String text = String.format("Walk %.0f meters along %s from '%s' to '%s'",
                distanceInMeters,
                streetName,
                fromNode,
                toNode
        );


        TextView directions = findViewById(R.id.directions_text);
        TextView header = findViewById(R.id.directions_header);
        header.setText(head + sample2[currIndex]);
        directions.setText(text);
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
        // Getting information about current edge from Path
        IdentifiedWeightedEdge edge = pathEdgeList.get(currIndex);
        double distanceInMeters = graph.getEdgeWeight(edge);
        String streetName = eInfo.get(edge.getId()).street;
        String fromNode = vInfo.get(graph.getEdgeSource(edge).toString()).name;
        String toNode = vInfo.get(graph.getEdgeTarget(edge).toString()).name;
        String text = String.format("Walk %.0f meters along %s from '%s' to '%s'",
                distanceInMeters,
                streetName,
                fromNode,
                toNode
        );

        TextView directions = findViewById(R.id.directions_text);
        TextView header = findViewById(R.id.directions_header);
        header.setText(head + sample2[currIndex]);
        directions.setText(text);
    }
}