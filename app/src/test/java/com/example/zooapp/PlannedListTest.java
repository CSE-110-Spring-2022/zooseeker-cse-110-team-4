package com.example.zooapp;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests on MainActivity adding animals to the planned list and corresponding count
 */
@RunWith(AndroidJUnit4.class)
public class PlannedListTest {

    ZooNodeDatabase testDb;
    ZooNodeDao zooNodeDao;
    PlannedAnimalDao planDao;
    PlannedAnimalDatabase testPlanDb;

    private static void forceLayout(RecyclerView recyclerView) {
        recyclerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        recyclerView.layout(0,0,1080,2280);
    }

    @Before
    public void resetDatabase(){
        Context context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, ZooNodeDatabase.class)
                .allowMainThreadQueries()
                .build();
        ZooNodeDatabase.injectTestDatabase(testDb);

        testPlanDb = Room.inMemoryDatabaseBuilder(context, PlannedAnimalDatabase.class)
                .allowMainThreadQueries()
                .build();
        PlannedAnimalDatabase.injectTestDatabase(testPlanDb);

        List<ZooNode> todos = ZooNode.loadJSON(context, "sample_node_info.json");
        zooNodeDao = testDb.ZooNodeDao();
        zooNodeDao.insertAll(todos);

        planDao = testPlanDb.plannedAnimalDao();
    }

    @Test
    public void testAddNewAnimal() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {

            List<ZooNode> before = planDao.getAll();
            String[] tags = {"tiger"};
            planDao.insert(new ZooNode("tiger", null, "exhibit", "Tiger", tags, "0.0", "0.0"));
            List<ZooNode> after = planDao.getAll();

            String newAnimal = "Tiger";

            assertEquals(before.size()+1, after.size());
            assertEquals(newAnimal, planDao.getByName(newAnimal).name);

        });

    }

    @Test
    public void testDisplayInitialCountAsSavedList() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {

            TextView count = activity.findViewById(R.id.added_counter);

            assertEquals("(" + planDao.getAll().size() + ")", count.getText().toString());

        });

    }

    @Test
    public void testAddOneAnimalCounter() {

        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {

            String[] tags = {"tiger"};
            planDao.insert(new ZooNode("tiger", null, "exhibit", "Tiger", tags, "0.0", "0.0"));
            TextView count = activity.findViewById(R.id.added_counter);

            activity.updateCount();

            assertEquals("(1)", count.getText().toString());

        });

    }

    /**
     * Test when clicking the plan button with an empty planned list, an alert pops up on screen
     */
    @Test
    public void testPlanClickedEmptyList() {
        //Start in MainActivity
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            //No animals have been added to the planned list
            Button plan = activity.findViewById(R.id.plan_button);
            plan.performClick();
            assertEquals(true, activity.alertMessage.isShowing());
        });
        scenario.close();
    }
}
