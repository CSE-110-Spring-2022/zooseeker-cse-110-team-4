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

@RunWith(AndroidJUnit4.class)
public class PlannedListTest {

    ZooNodeDatabase testDb;
    ZooNodeDao zooNodeDao;

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

        List<ZooNode> todos = ZooNode.loadJSON(context, "sample_node_info.json");
        zooNodeDao = testDb.ZooNodeDao();
        zooNodeDao.insertAll(todos);
    }

    @Test
    public void testAddNewAnimal() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {


            List<ZooNode> before = zooNodeDao.getAll();
            String[] tags = {"tiger"};
            zooNodeDao.insert(new ZooNode("tiger", "exhibit", "Tiger", tags));
            List<ZooNode> after = zooNodeDao.getAll();

            String newAnimal = "Tiger";

            assertEquals(before.size()+1, after.size());
            assertEquals(newAnimal, after.get(after.size()-1).name);

        });

    }

    @Test
    public void testDisplayInitialCountAsZero() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {

            TextView count = activity.findViewById(R.id.added_counter);

            assertEquals("(0)", count.getText().toString());

        });

    }

    @Test
    public void testAddedAnimalCounter() {

        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {

            String[] tags = {"tiger"};
            activity.userExhibits.add(new ZooNode("tiger", "exhibit", "Tiger", tags));
            TextView count = activity.findViewById(R.id.added_counter);

            activity.updateCount();

            assertEquals("(1)", count.getText().toString());

        });

    }


}
