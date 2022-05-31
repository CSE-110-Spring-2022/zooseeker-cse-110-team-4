package com.example.zooapp;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.widget.Button;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Mock location test
 */
@RunWith(AndroidJUnit4.class)
public class MockLocationTest {
    Context context;
    ZooNodeDao dao;
    ZooNodeDatabase testDb;
    PlannedAnimalDao planDao;
    PlannedAnimalDatabase testPlanDb;
    List<ZooNode> allExhibits;

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
    }

    @Test
    public void setMockLocation() {
        planDao.insert(allExhibits.get(0));

        assertEquals(1, planDao.getAll().size());

        //Start in Directions Activity
        ActivityScenario<DirectionsActivity> scenario = ActivityScenario.launch(DirectionsActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            Location mockLocation = new Location("Mock Location");
            mockLocation.setLongitude(-117.17319785958953);
            mockLocation.setLatitude(32.73697286263083);
            activity.setMockLocation(mockLocation);
            assertEquals(1, planDao.getAll().size());

            activity.setContentView(R.layout.activity_directions);

            //Next should be visible, Previous should be invisible
            assertEquals(mockLocation.getLatitude(), activity.getMockLocation().getLatitude(), 0.0001);
            assertEquals(mockLocation.getLongitude(), activity.getMockLocation().getLongitude(), 0.0001);
        });
    }
}
