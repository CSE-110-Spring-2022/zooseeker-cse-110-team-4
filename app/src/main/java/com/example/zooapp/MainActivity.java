package com.example.zooapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final List<String> animalList = Arrays.asList();
    public final SampleAnimal tiger = new SampleAnimal("Tiger");
    public final SampleAnimal bear = new SampleAnimal("Polar Bear");
    public final SampleAnimal bird = new SampleAnimal("Toucan");
    public final SampleAnimal lion = new SampleAnimal("Lion");
    public final SampleAnimal walrus = new SampleAnimal("Walrus");

    public final List<SampleAnimal> samplelist = Arrays.asList(tiger,bear,bird,lion,walrus);
    public RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PlannedAnimalAdapter adaptertest = new PlannedAnimalAdapter();
        adaptertest.setHasStableIds(true);

        recyclerView = findViewById(R.id.planned_animals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptertest);

        adaptertest.setAnimalList(samplelist);

        TextView count = findViewById(R.id.added_counter);
        count.setText("(" +Integer.toString(samplelist.size()) +")");

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