package com.example.zooapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class ZooGraphTest {

    // This test checks to see if running the example code with Dijkstra's algorithm will
    // work on our device
    @Test
    public void createSimplePathGraphTest() {
        Context context = ApplicationProvider.getApplicationContext();

        // "source" and "sink" are graph terms for the start and end
        String start = "entrance_exit_gate";
        String goal = "elephant_odyssey";

        // 1. Load the graph...
        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON(context, "sample_zoo_graph.json");
        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(g, start, goal);

        // 2. Load the information about our nodes and edges...
        Map<String, ZooData.VertexInfo> vInfo = ZooData.loadVertexInfoJSON(context, "sample_node_info.json");
        Map<String, ZooData.EdgeInfo> eInfo = ZooData.loadEdgeInfoJSON(context, "sample_edge_info.json");

        //System.out.printf("The shortest path from '%s' to '%s' is:\n", start, goal);
        assertEquals("The shortest path from 'entrance_exit_gate' to 'elephant_odyssey' is:\n",
                String.format("The shortest path from '%s' to '%s' is:\n", start, goal));

        StringBuilder outputString = new StringBuilder();
        int i = 1;
        for (IdentifiedWeightedEdge e : path.getEdgeList()) {
//            System.out.printf("  %d. Walk %.0f meters along %s from '%s' to '%s'.\n",
//                    i,
//                    g.getEdgeWeight(e),
//                    eInfo.get(e.getId()).street,
//                    vInfo.get(g.getEdgeSource(e).toString()).name,
//                    vInfo.get(g.getEdgeTarget(e).toString()).name);
            outputString.append(
                    String.format("  %d. Walk %.0f meters along %s from '%s' to '%s'.\n",
                        i,
                        g.getEdgeWeight(e),
                        eInfo.get(e.getId()).street,
                        vInfo.get(g.getEdgeSource(e).toString()).name,
                        vInfo.get(g.getEdgeTarget(e).toString()).name)
            );
            i++;
        }

        assertEquals(
        "  1. Walk 10 meters along Entrance Way from 'Entrance and Exit Gate' to 'Entrance Plaza'.\n" +
                "  2. Walk 100 meters along Reptile Road from 'Entrance Plaza' to 'Alligators'.\n" +
                "  3. Walk 200 meters along Sharp Teeth Shortcut from 'Alligators' to 'Lions'.\n" +
                "  4. Walk 200 meters along Africa Rocks Street from 'Lions' to 'Elephant Odyssey'.\n",
                outputString.toString()
        );
    }

}
