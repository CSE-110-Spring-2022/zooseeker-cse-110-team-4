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

@RunWith(AndroidJUnit4.class)
public class BackwardsDirectionsTest {
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
    public void prevDirectionsOneAnimal(){
        planDao.insert(allExhibits.get(8)); //adding blue capped motmot

        assertEquals(1, planDao.getAll().size());

        //Start in Directions Activity
        ActivityScenario<DirectionsActivity> scenario = ActivityScenario.launch(DirectionsActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            Button next = activity.findViewById(R.id.next_button);
            Button previous = activity.findViewById(R.id.previous_button);
            TextView name = activity.findViewById(R.id.directions_text);

            assertEquals(1, activity.plannedAnimalDao.getAll().size());
            assertEquals(3, activity.userListShortestOrder.size());
            activity.locationToUse = new Location("Mock Location");
            activity.locationToUse.setLatitude(32.73459618734685);
            activity.locationToUse.setLongitude(-117.14936);
            assertEquals(1, planDao.getAll().size());

            //Next should be visible, Previous should be invisible
            next.performClick();
            previous.performClick();
            name = activity.findViewById(R.id.directions_text);
            String expected = " 1. Walk 1100 feet along Gate Path towards the 'Front Street / " +
                    "Treetops Way'\n 2. Walk 2500 feet along Treetops Way towards the 'Treetops " +
                    "Way / Orangutan Trail'\n 3. Walk 3800 feet along Orangutan Trail towards the " +
                    "'Parker Aviary' and find 'Blue Capped Motmot' inside";
            assertEquals(expected, name.getText().toString());

        });
        scenario.close();
    }

//    @Test
//    public void prevDirectionsThreeAnimals(){
//        planDao.insert(allExhibits.get(2)); //adding capuchin monkeys
//        planDao.insert(allExhibits.get(9)); //adding spoonbill
//        planDao.insert(allExhibits.get(10)); //adding hippos
//
//        //Start DirectionsActivity
//        ActivityScenario<DirectionsActivity> scenario2 = ActivityScenario.launch(DirectionsActivity.class);
//        scenario2.moveToState(Lifecycle.State.CREATED);
//        scenario2.moveToState(Lifecycle.State.STARTED);
//        scenario2.moveToState(Lifecycle.State.RESUMED);
//
//        scenario2.onActivity(activity2 -> {
//            Button next = activity2.findViewById(R.id.next_button);
//            Button previous = activity2.findViewById(R.id.previous_button);
//            TextView name = activity2.findViewById(R.id.directions_header);
//
//            //Two animals in planned list, but path should be length four including entrance and exit
//            assertEquals(2, activity2.userExhibits.size());
//            assertEquals(4, activity2.userListShortestOrder.size());
//            activity2.locationToUse = new Location("Mock Location");
//            activity2.locationToUse.setLatitude(32.73459618734685);
//            activity2.locationToUse.setLongitude(-117.14936);
//
//            //currIndex starts at first animal, index at 0
//            //Previous should be invisible, Next should be visible
//            assertEquals(0, activity2.currIndex);
//
//            //make sure first animal is correct
//            assertEquals(name.getText().toString(), "Flamingos");
//
//            //click Next to move to the second animal, currIndex at 1
//            next.performClick();
//            assertEquals(1, activity2.currIndex);
//            assertEquals(View.VISIBLE, previous.getVisibility());
//
//            //make sure second animal name is correct
//            assertEquals(name.getText().toString(), "Koi Fish");
//
//            //click Next to move to the third animal, currIndex at 2
//            next.performClick();
//            assertEquals(2, activity2.currIndex);
//            assertEquals(View.VISIBLE, previous.getVisibility());
//            //make sure end name is correct
//            assertEquals(name.getText().toString(), "Entrance and Exit Gate");
//
//            //click Previous to return to third animal, Previous button is invisible
//            previous.performClick();
//            assertEquals(View.VISIBLE, previous.getVisibility());
//            assertEquals(1, activity2.currIndex);
//            //make sure second animal name is correct
//            assertEquals(name.getText().toString(), "Koi Fish");
//
//            //click Previous to return to first animal, Previous button is invisible
//            previous.performClick();
//            assertEquals(View.INVISIBLE, previous.getVisibility());
//            assertEquals(0, activity2.currIndex);
//            //make sure first animal is correct
//            assertEquals(name.getText().toString(), "Flamingos");
//
//        });
//        scenario2.close();
//    }
}
