package com.example.zooapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.sql.Array;
import java.util.ArrayList;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This is the main activity, where the app initially loads into
 */
public class MainActivity extends AppCompatActivity{
    // Public fields
    public List<ZooNode> userExhibits;
    public RecyclerView recyclerView;
    public ActionBar actionBar;
    public AlertDialog alertMessage;
    public PlannedAnimalDao plannedAnimalDao;

    // Private fields
    private PlannedAnimalAdapter plannedAnimalAdapter;
    private TextView userExhibitsSize;
    private static final int REQUEST_USER_CHOSEN_ANIMAL = 0;
    private final PermissionChecker permissionChecker = new PermissionChecker(this);


    /**
     * Method for onCreate of the activity
     *
     * @param savedInstanceState State of activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the Title Bar to Zoo Seeker
        actionBar = getSupportActionBar();
        actionBar.setTitle("Zoo Seeker");

        //userExhibits = new ArrayList<>();

        plannedAnimalDao = PlannedAnimalDatabase.getSingleton(this).plannedAnimalDao();
        //plannedAnimalDao.deleteAll();

        setUpRecyclerView();

        if (permissionChecker.ensurePermissions()) return;

        // Added counter for user to see
        userExhibitsSize = findViewById(R.id.added_counter);
        userExhibitsSize.setText("(" + plannedAnimalDao.getAll().size() + ")");
    }

    /**
     * Sets up the recycler view to display the animals that the user has currently selected
     */
    private void setUpRecyclerView() {

        PlannedAnimalDao plannedAnimalDao = PlannedAnimalDatabase.getSingleton(this).plannedAnimalDao();
        List<ZooNode> plannedExhibits = plannedAnimalDao.getAll();

        plannedAnimalAdapter = new PlannedAnimalAdapter();
        plannedAnimalAdapter.setHasStableIds(true);
        plannedAnimalAdapter.setAnimalList(plannedExhibits);

        recyclerView = findViewById(R.id.planned_animals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(plannedAnimalAdapter);
    }

    /**
     * Used to start the search activity, waits for a click result from the search activity
     *
     * @param requestCode Code for when the result is done
     * @param resultCode Code for how the result went
     * @param data The intent of the activity we are returning from
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == REQUEST_USER_CHOSEN_ANIMAL && resultCode == Activity.RESULT_OK ) {
//            Gson gson = new Gson();
//            Type type = new TypeToken<List<ZooNode>>(){}.getType();
//            userExhibits = gson.fromJson(data.getStringExtra("userExhibitsJSONUpdated"),
//                    type);

            PlannedAnimalDao plannedAnimalDao = PlannedAnimalDatabase.getSingleton(this).plannedAnimalDao();
            List<ZooNode> plannedExhibits = plannedAnimalDao.getAll();
            plannedAnimalAdapter.setAnimalList(plannedExhibits);
            updateCount();
        }
    }

    /**
     * Creates the custom menu bar
     *
     * @param menu Menu
     * @return True for creating the menu bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.zoo_node_list_menu, menu);
        return true;
    }

    /**
     * Checks when an item on the menu has been clicked
     *
     * @param item Item that has been clicked
     * @return Result of that item being clicked
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actions_search:
                Log.d("Menu Click", "Search has been clicked");
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivityForResult(searchIntent, REQUEST_USER_CHOSEN_ANIMAL);
            case R.id.settings_button:
                Log.d("Menu Click", "Settings has been clicked");
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method used to update the selected animals list size
     */
    public void updateCount() {
        userExhibitsSize = findViewById(R.id.added_counter); //added this line to fix one of tests
        PlannedAnimalDao plannedAnimalDao = PlannedAnimalDatabase.getSingleton(this).plannedAnimalDao();
        userExhibitsSize.setText("(" + plannedAnimalDao.getAll().size() + ")");
    }

    /**
     * Navigate to Route Plan Summary Activity
     *
     * @param view The current view
     */
    public void onPlanButtonClicked(View view) {
        plannedAnimalDao = PlannedAnimalDatabase.getSingleton(this).plannedAnimalDao();
        if( plannedAnimalDao.getAll().size() == 0 ) {
            alertMessage = Utilities.showAlert(this,
                    "Please Enter at least One Animal");
            alertMessage.show();
            return;
        }
        //Intent intent = new Intent(this, DirectionsActivity.class);
        Intent intent = new Intent(this, RoutePlanSummaryActivity.class);

        Gson gson = new Gson();
        intent.putExtra("ListOfAnimals",gson.toJson(plannedAnimalDao.getAll()));
        startActivity(intent);
    }

    public void onClearButtonClicked(View view) {
        PlannedAnimalDao plannedAnimalDao = PlannedAnimalDatabase.getSingleton(this).plannedAnimalDao();
        plannedAnimalDao.deleteAll();
        updateCount();
        setUpRecyclerView();

    }
}