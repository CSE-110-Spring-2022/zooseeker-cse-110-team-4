package com.example.zooapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Arrays;
import android.util.Log;

import java.util.Hashtable;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final List<String> animalList = Arrays.asList("Bird", "Tiger", "Baboon", "Snake");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
        ZooNodeDao zooNodeDao = ZooNodeDatabase.getSingleton(this).ZooNodeDao();
        List<ZooNode> exhibits = zooNodeDao.getZooNodeKind("exhibit");
        List<String> animalNames = new ArrayList<>();
        Hashtable<String, List<String>> searchAnimals = new Hashtable<>();
        for( ZooNode zooNode: exhibits ) {
            animalNames.add(zooNode.name);
            for( String tag: zooNode.tags ) {
                if( !searchAnimals.contains(tag) ) {
                    searchAnimals.put(tag, new ArrayList<String>(){});
                }
                List<String> animals = searchAnimals.get(tag);
                animals.add(zooNode.name);
                searchAnimals.put(tag, animals);
            }
        }


        // Search bar
        SearchView searchBar = findViewById(R.id.searchBar);
        ListView listView = findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, animalNames);

        listView.setAdapter(adapter);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            public boolean onQueryTextSubmit(String s)
            {
                searchBar.clearFocus();
                if (animalNames.contains(s))
                    adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }

    /*public boolean onCreatedOptionMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItems = menu.findItem(R.id.searchBar);

        return super.onCreateOptionsMenu(menu);
    } */
}