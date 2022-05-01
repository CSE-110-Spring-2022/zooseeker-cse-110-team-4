package com.example.zooapp;


import static org.junit.Assert.assertNotNull;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class PlannedListTest {

    //@Before reset database?

    @Test
    public void displayTotalAnimalCountTest() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.recyclerView;
            RecyclerView.ViewHolder firstVH = recyclerView.findViewHolderForAdapterPosition(0);
            assertNotNull(firstVH);
            long id = firstVH.getItemId();

            //get list from database
            //set list of animals
            //save count of list - originalSize
            //add one animal to the list
            //assertequals(current size, size displayed on the counter text )

        });

    }

    @Test
    public void addAnimalUITest() {
        String newText = "Cheetah";

        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
           // List<ToDoListItem> beforeToDoList = todoItemDao.getAll();
            //^^ replace sampleanimal and the database stuff

            TextView newAnimalText = activity.findViewById(R.id.planned_animal_text);
            Button planAnimalButton = activity.findViewById(R.id.plan_button);

            newAnimalText.setText(newText);
            planAnimalButton.performClick();

            //get the list
            //assertEquals(old list size + 1, new list size );
            //assertEquals(newText, afterList.get(afterList.size()-1).text); - check the added animal was displayed


        });


    }




}
