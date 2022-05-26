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
        String goal = "orangutan";

        // 1. Load the graph...
        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON(context, "sample_zoo_graph.json");


        // 2. Load the information about our nodes and edges...
        Map<String, ZooData.VertexInfo> vInfo = ZooData.loadVertexInfoJSON(context, "sample_node_info.json");
        Map<String, ZooData.EdgeInfo> eInfo = ZooData.loadEdgeInfoJSON(context, "sample_edge_info.json");

        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(g, start, goal);

        //System.out.printf("The shortest path from '%s' to '%s' is:\n", start, goal);
        assertEquals("The shortest path from 'entrance_exit_gate' to 'orangutan' is:\n",
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
                    String.format("  %d. Walk %.0f feet along %s from '%s' to '%s'.\n",
                        i,
                        g.getEdgeWeight(e),
                        eInfo.get(e.getId()).street,
                        vInfo.get(g.getEdgeSource(e).toString()).name,
                        vInfo.get(g.getEdgeTarget(e).toString()).name)
            );
            i++;
        }

        assertEquals(
        "  1. Walk 1100 feet along Gate Path from 'Entrance and Exit Gate' to 'Front Street / Treetops Way'.\n" +
                "  2. Walk 1100 feet along Treetops Way from 'Front Street / Treetops Way' to 'Treetops Way / Fern Canyon Trail'.\n" +
                "  3. Walk 1400 feet along Treetops Way from 'Treetops Way / Fern Canyon Trail' to 'Treetops Way / Orangutan Trail'.\n" +
                "  4. Walk 1200 feet along Orangutan Trail from 'Treetops Way / Orangutan Trail' to 'Siamangs'.\n" +
                "  5. Walk 1100 feet along Orangutan Trail from 'Siamangs' to 'Orangutans'.\n",
                outputString.toString()
        );
    }

}
