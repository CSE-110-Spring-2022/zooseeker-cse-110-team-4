package com.example.zooapp;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.robolectric.RuntimeEnvironment.getApplication;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Skip exhibit test
 */
@RunWith(AndroidJUnit4.class)
public class SkipButtonTest {
    Context context;
    ZooNodeDao dao;
    ZooNodeDatabase testDb;
    PlannedAnimalDao planDao;
    PlannedAnimalDatabase testPlanDb;
    List<ZooNode> allExhibits;

    //Set up the database of animals in the zoo
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
    public void testSkipVisibility(){
        planDao.insert(allExhibits.get(0));

        assertEquals(1, planDao.getAll().size());

        //Start in Directions Activity
        ActivityScenario<DirectionsActivity> scenario = ActivityScenario.launch(DirectionsActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {

            assertEquals(1, planDao.getAll().size());

            // Skip should be visible at the first exhibit
            Button skip = activity.findViewById(R.id.skip_button);
            Button previous = activity.findViewById(R.id.previous_button);
            Button next = activity.findViewById(R.id.next_button);
            TextView name = activity.findViewById(R.id.directions_header);

            activity.setLocationToUse(new Location("Mock Location"));
            activity.getLocationToUse().setLatitude(32.73459618734685);
            activity.getLocationToUse().setLongitude(-117.14936);

            assertEquals(0, activity.currIndex);
            assertEquals("Koi Fish", name.getText().toString());
            assertEquals(View.VISIBLE,skip.getVisibility());


            // Skip should be invisible at the entrance/exit node
            next.performClick();
            assertEquals(1, activity.currIndex);
            assertEquals("Entrance and Exit Gate", name.getText().toString());

            assertEquals(View.INVISIBLE, skip.getVisibility());

            // Go to the previous exhibit, Skip button should be visible again
            previous.performClick();
            assertEquals(0, activity.currIndex);
            assertEquals(View.VISIBLE, skip.getVisibility());


        });

    }

    @Test
    public void testSkipCounter(){

        planDao.insert(allExhibits.get(0));
        planDao.insert(allExhibits.get(1));

        //Start DirectionsActivity
        ActivityScenario<DirectionsActivity> scenario2 = ActivityScenario.launch(DirectionsActivity.class);
        scenario2.moveToState(Lifecycle.State.CREATED);
        scenario2.moveToState(Lifecycle.State.STARTED);
        scenario2.moveToState(Lifecycle.State.RESUMED);

        scenario2.onActivity(activity2 -> {
            Button next = activity2.findViewById(R.id.next_button);
            Button skip = activity2.findViewById(R.id.skip_button);
            TextView name = activity2.findViewById(R.id.directions_header);

            activity2.setLocationToUse(new Location("Mock Location"));
            activity2.getLocationToUse().setLatitude(32.73459618734685);
            activity2.getLocationToUse().setLongitude(-117.14936);

            //Two animals in planned list, but path should be length four including entrance and exit
            assertEquals(2, planDao.getAll().size());
            assertEquals(4, activity2.userListShortestOrder.size());

            //currIndex starts at first animal, index at 0
            //Previous should be invisible, Next should be visible
            assertEquals(0, activity2.currIndex);

            //make sure first animal is correct
            assertEquals("Flamingos", name.getText().toString());

            //click Next to move to the second animal, currIndex at 1
            next.performClick();
            assertEquals(1, activity2.currIndex);

            //make sure second animal name is correct
            assertEquals("Koi Fish", name.getText().toString());

            // Click Skip
            // Should remove "Flamingos" and decrease the size of the planned animals by 1
            // The app should display the next node which is "Entrance and Exit Gate"
            skip.performClick();
            // Make sure end name is correct
            assertEquals("Entrance and Exit Gate", name.getText().toString());
            // The size of the planned animals should be 1
            assertEquals(1, planDao.getAll().size());


        });
        scenario2.close();
    }
}
