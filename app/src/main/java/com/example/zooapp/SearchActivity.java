package com.example.zooapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * This activity is for when we search for an animal to add into our list
 */
public class SearchActivity extends AppCompatActivity implements AnimalListViewAdapter.ClickListener{
    // Public fields
    public RecyclerView recyclerView;
    public ExhibitsSetup exhibitsSetup = new ExhibitsSetup(this);

    // Private fields
    private AnimalListViewAdapter adapter;

    /**
     * Method for onCreate of the activity
     *
     * @param savedInstanceState State of activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Set up the title of the menu bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Search for an Animal Exhibit");
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Set up information about the zoo exhibits
        exhibitsSetup.getExhibitInformation();

        //Set up view for the list of exhibits
        setUpRecyclerView();

        // Set up for the search view
        SearchView searchView = findViewById(R.id.searchAnimalBar);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }

    /**
     * Sets up the recycler view when the activity is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        setUpRecyclerView();
    }


    /**
     * Sets up the recycler view
     */
    private void setUpRecyclerView() {
        adapter = new AnimalListViewAdapter(exhibitsSetup.getTotalExhibits(), this);
        recyclerView = findViewById(R.id.animalListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        Log.d("Search View", "View has been set up");
    }

    /**
     * For when an item is clicked on the recycler view
     *
     * @param position Position of the view holder that is clicked
     */
    @Override
    public void onItemClick(int position) {
        exhibitsSetup.addAnimalPlannedList(position);

        Gson gson = new Gson();
        Intent refresh = new Intent(this, MainActivity.class);
        refresh.putExtra("userExhibitsJSONUpdated", gson.toJson(exhibitsSetup.getUserExhibits()));
        setResult(RESULT_OK, refresh);
        finish();
    }

    /**
     * When the options are created
     *
     * @param item Item that is created
     * @return Not used
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d("Search View", "Back button has been clicked");
        finish();
        return super.onOptionsItemSelected(item);
    }
}