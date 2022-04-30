package com.example.zooapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Arrays;
import android.util.Log;
import android.widget.Toolbar;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AnimalListViewAdapter.ClickListener {
    private List<ZooNode> exhibits;
    private AnimalListViewAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
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

    }

    private void setUpRecyclerView() {
        adapter = new AnimalListViewAdapter(exhibits, this);

        recyclerView = findViewById(R.id.animalListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.zoo_node_list_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.actions_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

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
        return true;
    }

    @Override
    public void onItemClick(int position) {
        Log.d("Item Click", "Item has been clicked at position = " + position);
    }
}