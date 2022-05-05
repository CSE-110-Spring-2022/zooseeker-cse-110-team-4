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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This activity is for when we search for an animal to add into our list
 */
public class SearchActivity extends AppCompatActivity implements AnimalListViewAdapter.ClickListener{
    // Public fields
    public List<ZooNode> userExhibits;
    public RecyclerView recyclerView;

    // Private fields
    private AnimalListViewAdapter adapter;
    private List<ZooNode> exhibits;

    /**
     * Method for onCreate of the activity
     *
     * @param savedInstanceState State of activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Sets up the title of the menu bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Search for an Animal Exhibit");
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Gather information from the main activity
        Gson gson = new Gson();
        Type type = new TypeToken<List<ZooNode>>(){}.getType();
        userExhibits = gson.fromJson(getIntent().getStringExtra("userExhibitsJSON"), type);

        // Get all the animals available in the zoo, exhibits
        ZooNodeDao dao = ZooNodeDatabase.getSingleton(this).ZooNodeDao();
        exhibits = dao.getZooNodeKind("exhibit");
        List<String> toSort = new ArrayList<>();

        // Sort the animals in alphabetical order
        for( int i = 0; i < exhibits.size(); i++ ) {
            toSort.add(exhibits.get(i).name);
        }
        Collections.sort(toSort);
        exhibits.clear();
        for( int i = 0; i < toSort.size(); i++ ) {
            exhibits.add(dao.getByName(toSort.get(i)));
        }

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
     * Sets up the recycler view
     */
    private void setUpRecyclerView() {
        adapter = new AnimalListViewAdapter(exhibits, this);
        recyclerView = findViewById(R.id.animalListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    /**
     * For when an item is clicked on the recycler view
     *
     * @param position Position of the view holder that is clicked
     */
    @Override
    public void onItemClick(int position) {
        boolean animalExists = false;
        // Checks if the zoo node has already been added
        for( ZooNode zooNode: userExhibits ) {
            if( zooNode.name.equals(exhibits.get(position).name) ) {
                animalExists = true;
            }
        }
        // Only add if the animal hasn't been added
        if( !animalExists ) {
            userExhibits.add(exhibits.get(position));
            Log.d("Added Animal", "Unique animal added");
        }
        Gson gson = new Gson();
        Intent refresh = new Intent(this, MainActivity.class);
        refresh.putExtra("userExhibitsJSONUpdated", gson.toJson(userExhibits));
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
        Gson gson = new Gson();
        Intent refresh = new Intent(this, MainActivity.class);
        refresh.putExtra("userExhibitsJSONUpdated", gson.toJson(userExhibits));
        setResult(RESULT_OK, refresh);
        finish();
        return super.onOptionsItemSelected(item);
    }
}