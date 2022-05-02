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

        ZooNodeDao dao = ZooNodeDatabase.getSingleton(this).ZooNodeDao();
        exhibits = dao.getZooNodeKind("exhibit");
        List<String> toSort = new ArrayList<>();

        for( int i = 0; i < exhibits.size(); i++ ) {
            toSort.add(exhibits.get(i).name);
        }
        Collections.sort(toSort);
        exhibits.clear();
        for( int i = 0; i < toSort.size(); i++ ) {
            exhibits.add(dao.getByName(toSort.get(i)));
        }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setUpRecyclerView() {
        adapter = new AnimalListViewAdapter(exhibits, this);
        recyclerView = findViewById(R.id.animalListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        boolean animalExists = false;
        for( ZooNode zooNode: userExhibits ) {
            if( zooNode.name.equals(exhibits.get(position).name) ) {
                animalExists = true;
            }
        }
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