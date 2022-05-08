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
import java.util.List;


@RunWith(AndroidJUnit4.class)
public class DirectionButtonsTest {
    Context context;
    ZooNodeDao dao;
    /*
    1. test initial visibility - passed
    2. test alert message pops up on next -
    3. previous appears and disappears properly -
    4. alert message on plan if empty list - passed
     */

<<<<<<< HEAD
    @Rule
    public ActivityScenarioRule directionsRule = new ActivityScenarioRule(DirectionsActivity.class);
=======
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
>>>>>>> e53bded0fb46f6aaf32e4aa898089ea4aa887b14

    /**
     * Test when opening the directions page, the previous button should be invisible and the next
     * button should be visible
     */
    @Test
    public void testInitialButtonVisibility(){
<<<<<<< HEAD
//        //go to directions activity
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        //next button should be visible
        //previous button should not be visible
        scenario.onActivity(activity -> {

            String[] tags = {"mammal"};
            ZooNode animal = new ZooNode("tiger", "exhibit", "name",tags);
            activity.userExhibits.add(animal);

            Button plan = activity.findViewById(R.id.plan_button);
            plan.performClick();

            activity.setContentView(R.layout.activity_directions);

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
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            //planned list starts at 0
            Button plan = activity.findViewById(R.id.plan_button);
            plan.performClick();
            assertEquals(true, activity.alertMessage.isShowing());
        });

=======
        //go to directions activity
//        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
//        scenario.moveToState(Lifecycle.State.CREATED);
//        scenario.moveToState(Lifecycle.State.STARTED);
//        scenario.moveToState(Lifecycle.State.RESUMED);
//
//        //next button should be visible
//        //previous button should not be visible
//        scenario.onActivity(activity -> {
//            activity.userExhibits.add(dao.getById(0));
//
//            Button planButton = activity.findViewById(R.id.plan_button);
//            planButton.performClick();
//
//            Button nextButton = activity.findViewById(R.id.next_button);
//            Button previousButton = activity.findViewById(R.id.previous_button);
//
//            assertEquals(nextButton.getVisibility(), View.VISIBLE);
//            assertEquals(previousButton.getVisibility(), View.INVISIBLE);
//        });
>>>>>>> e53bded0fb46f6aaf32e4aa898089ea4aa887b14
    }

    /**
     * Test when clicking the next button at the end of the route, an alert pops up on screen
     */
    @Test
    public void testAlertMessageOnNextClicked(){

        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        //next button should be visible
        //previous button should not be visible
        scenario.onActivity(activity -> {

            String[] tags = {"lions", "cats","mammal", "africa"};
            ZooNode animal = new ZooNode("lions", "exhibit", "Lions",tags);
            activity.userExhibits.add(animal);

            String[] tags2 = {"elephant", "mammal", "africa"};
            ZooNode animal2 = new ZooNode("elephant_odyssey", "exhibit", "Elephant Odyssey",tags2);
            activity.userExhibits.add(animal2);

            Button plan = activity.findViewById(R.id.plan_button);
            plan.performClick(); //move to directions activity ?

            ActivityScenario<DirectionsActivity> scenario2 = ActivityScenario.launch(DirectionsActivity.class);
            scenario2.moveToState(Lifecycle.State.CREATED);
            scenario2.moveToState(Lifecycle.State.STARTED);
            scenario2.moveToState(Lifecycle.State.RESUMED);

                    scenario2.onActivity(activity2 -> {
                        Button next = activity2.findViewById(R.id.next_button);
                        next.performClick(); //TODO will not work while i can only access methods in Main - cant access onNextClicked
                        next.performClick();
                        assertEquals(true, activity2.alertMessage.isShowing());

                    });
        });
    }

    /**
     * When we reach the first animal on the route, the previous button should disappear again.
     */
//    @Test
//    public void testPreviousButtonDisappears(){
//        //TODO
//        //go to directions activity
//        ActivityScenario<DirectionsActivity> scenario = ActivityScenario.launch(DirectionsActivity.class);
//        scenario.moveToState(Lifecycle.State.CREATED);
//        scenario.moveToState(Lifecycle.State.STARTED);
//        scenario.moveToState(Lifecycle.State.RESUMED);
//
//        //next button should be visible
//        //previous button should not be visible
//        scenario.onActivity(activity -> {
//            Button next = activity.findViewById(R.id.next_button);
//            Button previous = activity.findViewById(R.id.previous_button);
//
//            //move onto the next animal
//            next.performClick();
//
//            //check if previous has appeared
//            assertEquals(View.VISIBLE, previous.getVisibility());
//
//            //move back to first animal
//            previous.performClick();
//
//            //check if previous has disappeared
//            assertEquals(View.INVISIBLE, previous.getVisibility());
//
//        });
 //   }

//    @Test
//    public void useAppContext() {
//        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        assertEquals("com.example.zooapp", appContext.getPackageName());
//    }
}
