package com.example.zooapp;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ShortestPathTest {
    Graph<String, IdentifiedWeightedEdge> g;
    Context context;
    List<ZooNode> shortList, longList, allExhibits;
    ZooNodeDao dao;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
        dao = Room.inMemoryDatabaseBuilder(context, ZooNodeDatabase.class)
                .allowMainThreadQueries()
                .build()
                .ZooNodeDao();
        List<ZooNode> allZooNodes = ZooNode.loadJSON(context, "sample_node_info.json");
        dao.insertAll(allZooNodes);
        g = ZooData.loadZooGraphJSON(context, "sample_zoo_graph.json");

        allExhibits = dao.getZooNodeKind("exhibit");
        shortList = new ArrayList<>();
        shortList.add(allExhibits.get(0));
        shortList.add(allExhibits.get(4));
        longList = new ArrayList<>();
        longList.add(allExhibits.get(0));
        longList.add(allExhibits.get(2));
        longList.add(allExhibits.get(1));
        longList.add(allExhibits.get(3));
    }

    @Test
    public void runShortAlg() {
        ShortestPathZooAlgorithm sp = new ShortestPathZooAlgorithm(context, shortList);
        List<GraphPath<String, IdentifiedWeightedEdge>> actual = sp.runAlgorithm();
        List<GraphPath<String, IdentifiedWeightedEdge>> expected = new ArrayList<>();
        expected.add(DijkstraShortestPath.findPathBetween(g, "entrance_exit_gate", "gorillas"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "gorillas", "arctic_foxes"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "arctic_foxes", "entrance_exit_gate"));

        for( int i = 0; i < expected.size(); i++ ) {
            assertEquals(expected.get(i).getWeight(), actual.get(i).getWeight(), 0.0001);
            assertEquals(expected.get(i).getLength(), actual.get(i).getLength());
            assertEquals(expected.get(i).toString(), actual.get(i).toString());
        }
    }

    @Test
    public void runLongAlg() {
        ShortestPathZooAlgorithm sp = new ShortestPathZooAlgorithm(context, longList);
        List<GraphPath<String, IdentifiedWeightedEdge>> actual = sp.runAlgorithm();
        List<GraphPath<String, IdentifiedWeightedEdge>> expected = new ArrayList<>();
        expected.add(DijkstraShortestPath.findPathBetween(g, "entrance_exit_gate", "gators"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "gators", "lions"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "lions", "gorillas"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "gorillas", "elephant_odyssey"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "elephant_odyssey", "entrance_exit_gate"));

        for( int i = 0; i < expected.size(); i++ ) {
            assertEquals(expected.get(i).getWeight(), actual.get(i).getWeight(), 0.0001);
            assertEquals(expected.get(i).getLength(), actual.get(i).getLength());
            assertEquals(expected.get(i).toString(), actual.get(i).toString());
        }
    }

    @Test
    public void runAllAnimalsAlg() {
        ShortestPathZooAlgorithm sp = new ShortestPathZooAlgorithm(context, allExhibits);
        List<GraphPath<String, IdentifiedWeightedEdge>> actual = sp.runAlgorithm();
        List<GraphPath<String, IdentifiedWeightedEdge>> expected = new ArrayList<>();
        expected.add(DijkstraShortestPath.findPathBetween(g, "entrance_exit_gate", "gators"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "gators", "lions"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "lions", "gorillas"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "gorillas", "elephant_odyssey"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "elephant_odyssey", "arctic_foxes"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "arctic_foxes", "entrance_exit_gate"));

        for( int i = 0; i < expected.size(); i++ ) {
            assertEquals(expected.get(i).getWeight(), actual.get(i).getWeight(), 0.0001);
            assertEquals(expected.get(i).getLength(), actual.get(i).getLength());
            assertEquals(expected.get(i).toString(), actual.get(i).toString());
        }
    }
}
