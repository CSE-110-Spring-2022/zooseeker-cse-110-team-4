package com.example.zooapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * This class is for a page to display summary of the route plan.
 * It should display all the exhibits in the route plan with the distance to each.
 */
public class RoutePlanSummaryActivity extends AppCompatActivity {

    public List<ZooNode> userExhibits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_plan_summary);

        // Set the Title Bar to "Route Plan Summary"
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Route Plan Summary");

        // Grabbing planned animals from planned list and inputting to new activity
        Gson gson = new Gson();
        Type type = new TypeToken<List<ZooNode>>(){}.getType();
        if(gson.fromJson(getIntent().getStringExtra("ListOfAnimals"), type) != null) {
            userExhibits = gson.fromJson(getIntent().getStringExtra("ListOfAnimals"), type);
        }
        else {
            Log.d("null input", "User exhibits was null");
            throw new NullPointerException("UserExhibits was null");
        }

        // Set up UI of Planned List
    }

    /**
     * Navigate to directions activity
     *
     * @param view The current view
     */
    public void onDirectionsButtonClicked(View view) {
        Intent intent = new Intent(this, DirectionsActivity.class);
        Gson gson = new Gson();
        intent.putExtra("ListOfAnimals",gson.toJson(userExhibits));
        startActivity(intent);
    }
}