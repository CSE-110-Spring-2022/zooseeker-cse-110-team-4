package com.example.zooapp;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

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
public class DirectionButtonsTest {
    Context context;
    ZooNodeDao dao;


    String[] tags = {"lions", "cats","mammal", "africa"};
    ZooNode animal = new ZooNode("lions", "exhibit", "Lions",tags);

    String[] tags2 = {"elephant", "mammal", "africa"};
    ZooNode animal2 = new ZooNode("elephant_odyssey", "exhibit", "Elephant Odyssey",tags2);

    //Set up the database of animals in the zoo
    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
        dao = Room.inMemoryDatabaseBuilder(context, ZooNodeDatabase.class)
                .allowMainThreadQueries()
                .build()
                .ZooNodeDao();
        List<ZooNode> allZooNodes = ZooNode.loadJSON(context, "sample_node_info.json");
        dao.insertAll(allZooNodes);

    }

    /**
     * Test when opening the directions page, the previous button should be invisible and the next
     * button should be visible
     */
    @Test
    public void testInitialButtonVisibility(){

        //Start in Directions Activity
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {

            String[] tags = {"mammal"};
            ZooNode animal = new ZooNode("tiger", "exhibit", "name",tags);
            activity.userExhibits.add(animal);

            assertEquals(1, activity.userExhibits.size());

            //Click Plan button, move to DirectionActivity view
            Button plan = activity.findViewById(R.id.plan_button);
            plan.performClick();
            activity.setContentView(R.layout.activity_directions);

            //Next should be visible, Previous should be invisible
            Button next = activity.findViewById(R.id.next_button);
            Button previous = activity.findViewById(R.id.previous_button);

            assertEquals(next.getVisibility(), View.VISIBLE);
            assertEquals(previous.getVisibility(), View.INVISIBLE);
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

    /**
     * Test Next and Previous with a planned list of two animals
     * On first animal, Previous should be invisible
     * On last animal, clicking Next should cause an alert to appear
     */
    @Test
    public void testButtonsTwoAnimals(){

        //Start in MainActivity
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {

            //Add two animals to planned list
            activity.userExhibits.add(animal);
            activity.userExhibits.add(animal2);
            assertEquals(2, activity.userExhibits.size());

            //Set up intent to move to DirectionsActivity
            Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DirectionsActivity.class);
            Gson gson = new Gson();
            intent.putExtra("ListOfAnimals", gson.toJson(activity.userExhibits));

            //Start DirectionsActivity
            ActivityScenario<DirectionsActivity> scenario2 = ActivityScenario.launch(intent);
            scenario2.moveToState(Lifecycle.State.CREATED);
            scenario2.moveToState(Lifecycle.State.STARTED);
            scenario2.moveToState(Lifecycle.State.RESUMED);

            scenario2.onActivity(activity2 -> {
                Button next = activity2.findViewById(R.id.next_button);
                Button previous = activity2.findViewById(R.id.previous_button);

                //Two animals in planned list, but path should be length four including entrance and exit
                assertEquals(2, activity2.userExhibits.size());
                assertEquals(4, activity2.userListShortestOrder.size());

                //currIndex starts at first animal, index at 0
                //Previous should be invisible, Next should be visible
                assertEquals(0, activity2.currIndex);
                assertEquals(View.INVISIBLE, previous.getVisibility());
                assertEquals(View.VISIBLE, next.getVisibility());

                //click Next to move to the second animal, currIndex at 1
                next.performClick();
                assertEquals(1, activity2.currIndex);
                assertEquals(View.VISIBLE, previous.getVisibility());
                assertEquals(View.VISIBLE, next.getVisibility());

                //click Previous to return to first animal, Previous button is invisible
                previous.performClick();
                assertEquals(View.INVISIBLE, previous.getVisibility());
                assertEquals(View.VISIBLE, next.getVisibility());

                //click Next to move to second animal, currIndex at 1
                next.performClick();
                assertEquals(1, activity2.currIndex);

                //click Next to move to exit, currIndex at 2
                next.performClick();
                assertEquals(2, activity2.currIndex);

                //click Next to get an alert that the route is complete.
                next.performClick();
                assertEquals(true, activity2.alertMessage.isShowing());
                assertEquals(View.VISIBLE, next.getVisibility());



            });

        });


    }

//    @Test
//    public void useAppContext() {
//        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        assertEquals("com.example.zooapp", appContext.getPackageName());
//    }
}
