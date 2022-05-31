package com.example.zooapp;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.zooapp.Data.ZooData;
import com.example.zooapp.Data.ZooNode;
import com.example.zooapp.Data.ZooNodeDatabase;
import com.example.zooapp.Interface.ZooNodeDao;
import com.example.zooapp.Ultility.IdentifiedWeightedEdge;
import com.example.zooapp.Ultility.ShortestPathZooAlgorithm;

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
        expected.add(DijkstraShortestPath.findPathBetween(g, "entrance_exit_gate", "siamang"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "siamang", "koi"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "koi", "entrance_exit_gate"));

        for( int i = 0; i < expected.size(); i++ ) {
            assertEquals(expected.get(i).getWeight(), actual.get(i).getWeight(), 0.0001);
            assertEquals(expected.get(i).getLength(), actual.get(i).getLength());
            assertEquals(expected.get(i).toString(), actual.get(i).toString());
        }

        List<Double> actualTotalWeight = sp.getExhibitDistance();
        List<Double> expectedTotalWeight = new ArrayList<>();
        expectedTotalWeight.add(4800.0);
        expectedTotalWeight.add(13900.0);
        expectedTotalWeight.add(20400.0);


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
        expected.add(DijkstraShortestPath.findPathBetween(g, "flamingo", "gorilla"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "gorilla", "parker_aviary"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "parker_aviary", "orangutan"));
        expected.add(DijkstraShortestPath.findPathBetween(g, "orangutan", "entrance_exit_gate"));

        for( int i = 0; i < expected.size(); i++ ) {
            assertEquals(expected.get(i).getWeight(), actual.get(i).getWeight(), 0.0001);
            assertEquals(expected.get(i).getLength(), actual.get(i).getLength());
            assertEquals(expected.get(i).toString(), actual.get(i).toString());
        }

        List<Double> actualTotalWeight = sp.getExhibitDistance();
        List<Double> expectedTotalWeight = new ArrayList<>();
        expectedTotalWeight.add(5300.0);
        expectedTotalWeight.add(13100.0);
        expectedTotalWeight.add(19400.0);
        expectedTotalWeight.add(20900.0);
        expectedTotalWeight.add(26800.0);

        for( int i = 0; i < expectedTotalWeight.size(); i++ ) {
            assertEquals(expectedTotalWeight.get(i), actualTotalWeight.get(i), 0.0001);
        }
    }
}