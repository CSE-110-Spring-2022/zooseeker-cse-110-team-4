package com.example.zooapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
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

public class MainActivity extends AppCompatActivity{
    public List<ZooNode> userExhibits;
    public RecyclerView recyclerView;
    private PlannedAnimalAdapter plannedAnimalAdapter;
    private TextView userExhibitsSize;
    public ActionBar actionBar;
    private static final int REQUEST_USER_CHOSEN_ANIMAL = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Zoo Seeker");

        userExhibits = new ArrayList<>();

        setUpRecyclerView();

        userExhibitsSize = findViewById(R.id.added_counter);
        userExhibitsSize.setText("(" + userExhibits.size() + ")");
    }

    private void setUpRecyclerView() {
        plannedAnimalAdapter = new PlannedAnimalAdapter();
        plannedAnimalAdapter.setAnimalList(userExhibits);
        recyclerView = findViewById(R.id.planned_animals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(plannedAnimalAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == REQUEST_USER_CHOSEN_ANIMAL && resultCode == Activity.RESULT_OK ) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<ZooNode>>(){}.getType();
            userExhibits = gson.fromJson(data.getStringExtra("userExhibitsJSONUpdated"), type);
            plannedAnimalAdapter.setAnimalList(userExhibits);
            updateCount();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.zoo_node_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d("Menu Click", "Search has been clicked");
        Gson gson = new Gson();
        Intent searchIntent = new Intent(this, SearchActivity.class);
        searchIntent.putExtra("userExhibitsJSON", gson.toJson(userExhibits));
        startActivityForResult(searchIntent, REQUEST_USER_CHOSEN_ANIMAL);
        return super.onOptionsItemSelected(item);
    }

    public void updateCount() {
        userExhibitsSize.setText("(" + userExhibits.size() + ")");
    }

    public void onPlanButtonClicked(View view) {
        Intent intent = new Intent(this, DirectionsActivity.class);
        Gson gson = new Gson();
        intent.putExtra("ListOfAnimals",gson.toJson(userExhibits));
        startActivity(intent);
    }
}