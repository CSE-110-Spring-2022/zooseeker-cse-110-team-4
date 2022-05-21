package com.example.zooapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jgrapht.GraphPath;

import java.lang.reflect.Type;
import java.util.List;

/**
 * This class is for a page to display summary of the route plan.
 * It should display all the exhibits in the route plan with the distance to each.
 */
public class RoutePlanSummaryActivity extends AppCompatActivity {

    public List<ZooNode> userExhibits;

    private List<GraphPath<String, IdentifiedWeightedEdge>> graphPaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_plan_summary);

        // Set the Title Bar to "Route Plan Summary"
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Route Plan Summary");

        // Grabbing planned animals from planned list and inputting to new activity
//        Gson gson = new Gson();
//        Type type = new TypeToken<List<ZooNode>>(){}.getType();
        PlannedAnimalDao plannedAnimalDao = PlannedAnimalDatabase.getSingleton(this).plannedAnimalDao();

        if(plannedAnimalDao.getAll().size() > 0) {
            userExhibits = plannedAnimalDao.getAll();
        }
        else {
            Log.d("null input", "User exhibits was null");
            throw new NullPointerException("UserExhibits was null");
        }

        // Run Route Algorithm
        GraphAlgorithm algorithm = new ShortestPathZooAlgorithm(
                getApplication().getApplicationContext(), userExhibits);
        graphPaths = algorithm.runAlgorithm();
        List<ZooNode> userListShortestOrder = algorithm.getUserListShortestOrder();
        List<Double> exhibitDistances = algorithm.getExhibitDistance();

        // remove last exhibit (exit gate) from list
        userListShortestOrder.remove(userListShortestOrder.size()-1);

        // Set up UI of Planned List
        setUpRecyclerView(userListShortestOrder, exhibitDistances);
    }

    /**
     * Sets up the recycler view to display the animals that the user has currently selected
     */
    private void setUpRecyclerView(List<ZooNode> userListShortestOrder,
                                   List<Double> exhibitDistances) {
        PlannedAnimalAdapter plannedAnimalAdapter = new PlannedAnimalAdapter();
        plannedAnimalAdapter.setAnimalList(userListShortestOrder);
        RecyclerView recyclerView = findViewById(R.id.planned_animals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(plannedAnimalAdapter);
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