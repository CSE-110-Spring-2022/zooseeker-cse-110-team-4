package com.example.zooapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements AnimalListViewAdapter.ClickListener{
    private List<ZooNode> exhibits;
    public List<ZooNode> userExhibits;
    private AnimalListViewAdapter adapter;
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Search for an Animal Exhibit");
        actionBar.setDisplayHomeAsUpEnabled(true);

        Gson gson = new Gson();
        Type type = new TypeToken<List<ZooNode>>(){}.getType();
        userExhibits = gson.fromJson(getIntent().getStringExtra("userExhibitsJSON"), type);
        exhibits = gson.fromJson(getIntent().getStringExtra("exhibitsJSON"), type);

        setUpRecyclerView();

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

    private void setUpRecyclerView() {
        adapter = new AnimalListViewAdapter(exhibits, this);
        recyclerView = findViewById(R.id.animalListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        if( !userExhibits.contains(exhibits.get(position)) ) {
            userExhibits.add(exhibits.get(position));
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}