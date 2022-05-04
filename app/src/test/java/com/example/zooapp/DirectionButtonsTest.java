package com.example.zooapp;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.util.List;


@RunWith(AndroidJUnit4.class)
public class DirectionButtonsTest {

    /*
    1. test initial visibility
    2. test alert message pops up
    3. previous appears and disappears properly
     */

    /*
    Test when opening the directions page, the previous button should be invisible and the next
    button should be visible
     */
    @Test
    public void testInitialButtonVisibility(){
        //go to directions activity
        ActivityScenario<DirectionsActivity> scenario = ActivityScenario.launch(DirectionsActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        //next button should be visible
        //previous button should not be visible
        scenario.onActivity(activity -> {
            Button next = activity.findViewById(R.id.next_button);
            Button previous = activity.findViewById(R.id.previous_button);

            assertEquals(next.getVisibility(), View.VISIBLE);
            assertEquals(previous.getVisibility(), View.INVISIBLE);

        });
    }

    /*
    Test when clicking the next button at the end of the route, an alert pops up on screen
     */
    @Test
    public void testAlertMessageOnNextClicked(){
        //go to directions activity
        ActivityScenario<DirectionsActivity> scenario = ActivityScenario.launch(DirectionsActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        //next button should be visible
        //when clicked and no more animals are left, show an alert
        scenario.onActivity(activity -> {
            Button next = activity.findViewById(R.id.next_button);

            //set the curr index to the last animal
            activity.currIndex = activity.pathEdgeList.size()-1;

            //check next button is visible
            assertEquals(next.getVisibility(), View.VISIBLE);

            //click next
            next.performClick();
            //check alert message is showing
            assertEquals(true, activity.alertMessage.isShowing());

        });
    }

    /*
    When we reach the first animal on the route, the previous button should disappear again.
     */
    @Test
    public void testPreviousButtonDisappears(){
        //go to directions activity
        ActivityScenario<DirectionsActivity> scenario = ActivityScenario.launch(DirectionsActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        //next button should be visible
        //previous button should not be visible
        scenario.onActivity(activity -> {
            Button next = activity.findViewById(R.id.next_button);
            Button previous = activity.findViewById(R.id.previous_button);

            //move onto the next animal
            next.performClick();

            //check if previous has appeared
            assertEquals(View.VISIBLE, previous.getVisibility());

            //move back to first animal
            previous.performClick();

            //check if previous has disappeared
            assertEquals(View.INVISIBLE, previous.getVisibility());

        });
    }

//    @Test
//    public void useAppContext() {
//        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        assertEquals("com.example.zooapp", appContext.getPackageName());
//    }
}
