package com.example.zooapp.Viewer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.zooapp.Ultility.IdentifiedWeightedEdge;
import com.example.zooapp.Interface.GraphAlgorithm;
import com.example.zooapp.Interface.PlannedAnimalDao;
import com.example.zooapp.Data.PlannedAnimalDatabase;
import com.example.zooapp.R;
import com.example.zooapp.Adapter.RoutePlanSummaryAdapter;
import com.example.zooapp.Ultility.ShortestPathZooAlgorithm;
import com.example.zooapp.Data.ZooNode;

import org.jgrapht.GraphPath;

import java.util.List;

/**
 * This class is for a page to display summary of the route plan.
 * It should display all the exhibits in the route plan with the distance to each.
 */
public class RoutePlanSummaryActivity extends AppCompatActivity {

    public List<ZooNode> userExhibits;

    private List<GraphPath<String, IdentifiedWeightedEdge>> graphPaths;

    /**
     * Method for onCreate of the activity
     *
     * @param savedInstanceState State of activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_plan_summary);

        // Set the Title Bar to "Route Plan Summary"
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Route Plan Summary");

        //Access DAO of planned animals
        PlannedAnimalDao plannedAnimalDao = PlannedAnimalDatabase.getSingleton(this).plannedAnimalDao();

        //Throw exception if there are no exhibits in the planned list
        if(plannedAnimalDao.getAll().size() <= 0) {
            Log.d("null input", "User exhibits was null");
            throw new NullPointerException("UserExhibits was null");
        }

        // Run Route Algorithm
        //TODO refactor
        GraphAlgorithm algorithm = new ShortestPathZooAlgorithm(
                getApplication().getApplicationContext(), plannedAnimalDao.getAll());
        graphPaths = algorithm.runAlgorithm();
        List<ZooNode> userListShortestOrder = algorithm.getUserListShortestOrder();
        List<Double> exhibitDistances = algorithm.getExhibitDistance();

        // remove entrance/exit gate from beginning and end of userListShortestOrder
        userListShortestOrder.remove(0);
        userListShortestOrder.remove(userListShortestOrder.size()-1);
        // remove distance to entrance/exit gate at end of exhibit distances list
        exhibitDistances.remove(exhibitDistances.size()-1);

        // Set up UI of Planned List
        setUpRecyclerView(userListShortestOrder, exhibitDistances);
    }

    /**
     * Sets up the recycler view to display the animals that the user has currently selected
     */
    private void setUpRecyclerView(List<ZooNode> userListShortestOrder,
                                   List<Double> exhibitDistances) {
        RoutePlanSummaryAdapter routePlanSummaryAdapter = new RoutePlanSummaryAdapter();
        routePlanSummaryAdapter.setHasStableIds(true);
        routePlanSummaryAdapter.setAnimalList(userListShortestOrder, exhibitDistances);

        RecyclerView recyclerView = findViewById(R.id.route_plan_summary);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(routePlanSummaryAdapter);
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
        inflater.inflate(R.menu.route_summary_menu, menu);
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
            case R.id.route_settings_button:
                Log.d("Menu Click", "Settings has been clicked");
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Navigate to directions activity
     *
     * @param view The current view
     */
    public void onDirectionsButtonClicked(View view) {
        Intent intent = new Intent(this, DirectionsActivity.class);
        startActivity(intent);
    }

    /**
     * Clear the animals from the planned list and return to home screen
     *
     * @param view The current view
     */
    public void onClearButtonClicked(View view) {
        Log.d("Button Clicked", "Clear Button Clicked");
        PlannedAnimalDatabase.getSingleton(this).plannedAnimalDao().deleteAll();
        finish();
    }
}