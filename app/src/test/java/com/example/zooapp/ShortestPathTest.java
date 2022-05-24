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
        //adding siamang
        shortList.add(allExhibits.get(5));
        //adding koi
        shortList.add(allExhibits.get(0));
        //long list creation
        longList = new ArrayList<>();
        longList.add(allExhibits.get(1)); //adding flamingos
        longList.add(allExhibits.get(4)); //adding orangutans
        longList.add(allExhibits.get(3)); //adding gorillas
        longList.add(allExhibits.get(7)); //adding toucan


    }

    @Test
    public void runShortAlg() {
        ShortestPathZooAlgorithm sp = new ShortestPathZooAlgorithm(context, shortList);
        List<GraphPath<String, IdentifiedWeightedEdge>> actual = sp.runAlgorithm();
        List<GraphPath<String, IdentifiedWeightedEdge>> expected = new ArrayList<>();
        expected.add(DijkstraShortestPath.findPathBetween(g, "entrance_exit_gate", "koi"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "koi", "siamang"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "siamang", "entrance_exit_gate"));

        for( int i = 0; i < expected.size(); i++ ) {
            assertEquals(expected.get(i).getWeight(), actual.get(i).getWeight(), 0.0001);
            assertEquals(expected.get(i).getLength(), actual.get(i).getLength());
            assertEquals(expected.get(i).toString(), actual.get(i).toString());
        }

        List<Double> actualTotalWeight = sp.getExhibitDistance();
        List<Double> expectedTotalWeight = new ArrayList<>();
        expectedTotalWeight.add(60.0);
        expectedTotalWeight.add(230.0);
        expectedTotalWeight.add(360.0);


        for( int i = 0; i < expectedTotalWeight.size(); i++ ) {
            assertEquals(expectedTotalWeight.get(i), actualTotalWeight.get(i), 0.0001);
        }
    }

    @Test
    public void runLongAlg() {
        ShortestPathZooAlgorithm sp = new ShortestPathZooAlgorithm(context, longList);
        List<GraphPath<String, IdentifiedWeightedEdge>> actual = sp.runAlgorithm();
        List<GraphPath<String, IdentifiedWeightedEdge>> expected = new ArrayList<>();
        expected.add(DijkstraShortestPath.findPathBetween(g, "entrance_exit_gate", "flamingo"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "flamingo", "orangutan"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "orangutan", "parker_aviary"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "parker_aviary", "gorilla"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "gorilla", "entrance_exit_gate"));

        for( int i = 0; i < expected.size(); i++ ) {
            assertEquals(expected.get(i).getWeight(), actual.get(i).getWeight(), 0.0001);
            assertEquals(expected.get(i).getLength(), actual.get(i).getLength());
            assertEquals(expected.get(i).toString(), actual.get(i).toString());
        }

        List<Double> actualTotalWeight = sp.getExhibitDistance();
        List<Double> expectedTotalWeight = new ArrayList<>();
        expectedTotalWeight.add(90.0);
        expectedTotalWeight.add(295.0);
        expectedTotalWeight.add(345.0);
        expectedTotalWeight.add(475.0);
        expectedTotalWeight.add(785.0);

        for( int i = 0; i < expectedTotalWeight.size(); i++ ) {
            assertEquals(expectedTotalWeight.get(i), actualTotalWeight.get(i), 0.0001);
        }
    }

    @Test
    public void runAllAnimalsAlg() {
        ShortestPathZooAlgorithm sp = new ShortestPathZooAlgorithm(context, allExhibits);
        List<GraphPath<String, IdentifiedWeightedEdge>> actual = sp.runAlgorithm();
        List<GraphPath<String, IdentifiedWeightedEdge>> expected = new ArrayList<>();
        expected.add(DijkstraShortestPath.findPathBetween(g, "entrance_exit_gate", "koi"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "koi", "flamingo"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "flamingo", "capuchin"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "capuchin", "scripps_aviary"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "scripps_aviary", "crocodile"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "crocodile", "hippo"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "hippo", "parker_aviary"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "parker_aviary", "parker_aviary"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "parker_aviary", "orangutan"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "orangutan", "siamang"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "siamang", "owens_aviary"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "owens_aviary", "owens_aviary"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "owens_aviary", "fern_canyon"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "fern_canyon", "gorilla"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "gorilla", "entrance_exit_gate"));

        for( int i = 0; i < expected.size(); i++ ) {
            assertEquals(expected.get(i).getWeight(), actual.get(i).getWeight(), 0.0001);
            assertEquals(expected.get(i).getLength(), actual.get(i).getLength());
            assertEquals(expected.get(i).toString(), actual.get(i).toString());
        }

        List<Double> actualTotalWeight = sp.getExhibitDistance();
        List<Double> expectedTotalWeight = new ArrayList<>();
        expectedTotalWeight.add(60.0);
        expectedTotalWeight.add(190.0);
        expectedTotalWeight.add(340.0);
        expectedTotalWeight.add(420.0);
        expectedTotalWeight.add(480.0);
        expectedTotalWeight.add(490.0);
        expectedTotalWeight.add(580.0);
        expectedTotalWeight.add(580.0);
        expectedTotalWeight.add(630.0);
        expectedTotalWeight.add(635.0);
        expectedTotalWeight.add(740.0);
        expectedTotalWeight.add(740.0);
        expectedTotalWeight.add(770.0);
        expectedTotalWeight.add(980.0);
        expectedTotalWeight.add(1290.0);


        for( int i = 0; i < expectedTotalWeight.size(); i++ ) {
            assertEquals(expectedTotalWeight.get(i), actualTotalWeight.get(i), 0.0001);
        }
    }
}