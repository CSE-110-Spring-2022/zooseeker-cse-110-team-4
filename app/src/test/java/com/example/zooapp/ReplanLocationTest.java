package com.example.zooapp;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.example.zooapp.Data.PlannedAnimalDatabase;
import com.example.zooapp.Data.ZooNode;
import com.example.zooapp.Data.ZooNodeDatabase;
import com.example.zooapp.Interface.PlannedAnimalDao;
import com.example.zooapp.Interface.ZooNodeDao;
import com.example.zooapp.Ultility.LocationListenerImplementation;
import com.example.zooapp.Viewer.DirectionsActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class ReplanLocationTest {
    private DirectionsActivity directionsActivity;
    private Context context;
    private ZooNodeDao dao;
    private ZooNodeDatabase testDb;
    private PlannedAnimalDao planDao;
    private PlannedAnimalDatabase testPlanDb;
    private List<ZooNode> allExhibits;
    private LocationListenerImplementation locationListenerImplementation;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, ZooNodeDatabase.class)
                .allowMainThreadQueries()
                .build();
        ZooNodeDatabase.injectTestDatabase(testDb);

        testPlanDb = Room.inMemoryDatabaseBuilder(context, PlannedAnimalDatabase.class)
                .allowMainThreadQueries()
                .build();
        PlannedAnimalDatabase.injectTestDatabase(testPlanDb);

        List<ZooNode> allZooNodes = ZooNode.loadJSON(context, "sample_node_info.json");
        dao = testDb.ZooNodeDao();
        dao.insertAll(allZooNodes);
        planDao = testPlanDb.plannedAnimalDao();
        allExhibits = dao.getZooNodeKind("exhibit");
        planDao.insert(allExhibits.get(1)); //adding flamingos
        directionsActivity = Robolectric.buildActivity(DirectionsActivity.class).create().get();
        locationListenerImplementation = directionsActivity.getLocationListenerImplementation();
    }

    @Test
    public void replanPromptAppearTest() {

    }
}
