package com.example.zooapp;
import android.content.Context;
import android.content.Intent;
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
public class PreviousButtonTest {
    Context context;
    ZooNodeDao dao;
    ZooNodeDatabase testDb;

    String[] tags = {"lions", "cats","mammal", "africa"};
    ZooNode animal = new ZooNode("lions", null, "exhibit", "Lions",tags, "0.0", "0.0");

    String[] tags2 = {"elephant", "mammal", "africa"};
    ZooNode animal2 = new ZooNode("elephant_odyssey", null, "exhibit", "Elephant Odyssey",tags2, "0.0", "0.0");

    //Set up the database of animals in the zoo
    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, ZooNodeDatabase.class)
                .allowMainThreadQueries()
                .build();
        ZooNodeDatabase.injectTestDatabase(testDb);

        List<ZooNode> allZooNodes = ZooNode.loadJSON(context, "sample_node_info.json");
        dao = testDb.ZooNodeDao();
        dao.insertAll(allZooNodes);
    }

    @Test
    public void testPreviousInvisibility(){

        //Start in Directions Activity
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {

            activity.userExhibits.add(animal);
            //Click Plan button, move to DirectionActivity view
            Button plan = activity.findViewById(R.id.plan_button);
            plan.performClick();
            activity.setContentView(R.layout.activity_directions);

            //Previous should be invisible on first set of directions
            Button previous = activity.findViewById(R.id.previous_button);

            assertEquals(previous.getVisibility(), View.INVISIBLE);
        });

    }

    @Test
    public void testPreviousCounter(){

        List<ZooNode> userExhibits = new ArrayList<>();
        userExhibits.add(animal);
        userExhibits.add(animal2);
        assertEquals(2, userExhibits.size());

        //Set up intent to move to DirectionsActivity
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DirectionsActivity.class);
        Gson gson = new Gson();
        intent.putExtra("ListOfAnimals", gson.toJson(userExhibits));

        //Start DirectionsActivity
        ActivityScenario<DirectionsActivity> scenario2 = ActivityScenario.launch(intent);
        scenario2.moveToState(Lifecycle.State.CREATED);
        scenario2.moveToState(Lifecycle.State.STARTED);
        scenario2.moveToState(Lifecycle.State.RESUMED);

        scenario2.onActivity(activity2 -> {
            Button next = activity2.findViewById(R.id.next_button);
            Button previous = activity2.findViewById(R.id.previous_button);
            TextView name = activity2.findViewById(R.id.directions_header);

            //Two animals in planned list, but path should be length four including entrance and exit
            assertEquals(2, activity2.userExhibits.size());
            assertEquals(4, activity2.userListShortestOrder.size());

            //currIndex starts at first animal, index at 0
            //Previous should be invisible, Next should be visible
            assertEquals(0, activity2.currIndex);

            //make sure first animal is correct
            assertEquals(name.getText().toString(), "Lions");

            //click Next to move to the second animal, currIndex at 1
            next.performClick();
            assertEquals(1, activity2.currIndex);
            assertEquals(View.VISIBLE, previous.getVisibility());

            //make sure second animal name is correct
            assertEquals(name.getText().toString(), "Elephant Odyssey");

            //click Next to move to the third animal, currIndex at 2
            next.performClick();
            assertEquals(2, activity2.currIndex);
            assertEquals(View.VISIBLE, previous.getVisibility());
            //make sure end name is correct
            assertEquals(name.getText().toString(), "Entrance and Exit Gate");

            //click Previous to return to third animal, Previous button is invisible
            previous.performClick();
            assertEquals(View.VISIBLE, previous.getVisibility());
            assertEquals(1, activity2.currIndex);
            //make sure second animal name is correct
            assertEquals(name.getText().toString(), "Elephant Odyssey");

            //click Previous to return to first animal, Previous button is invisible
            previous.performClick();
            assertEquals(View.INVISIBLE, previous.getVisibility());
            assertEquals(0, activity2.currIndex);
            //make sure first animal is correct
            assertEquals(name.getText().toString(), "Lions");

        });
        scenario2.close();
    }
}
