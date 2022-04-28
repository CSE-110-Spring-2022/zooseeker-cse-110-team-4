package com.example.zooapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import java.util.Arrays;
import android.util.Log;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final List<String> animalList = Arrays.asList("Bird", "Tiger", "Baboon", "Snake");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
        List<ZooNode> zooNodeList = ZooNode.loadJSON(this, "zoo_node_list.json");
        Log.d("Zoo Node List Activity", zooNodeList.toString());

        SearchView searchBar = findViewById(R.id.searchBar);
        ListView listView = findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, animalList);

        listView.setAdapter(adapter);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            public boolean onQueryTextSubmit(String s)
            {
                searchBar.clearFocus();
                if (animalList.contains(s))
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