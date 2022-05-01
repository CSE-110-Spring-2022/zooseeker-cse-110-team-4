package com.example.zooapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;

import java.sql.Array;
import java.util.ArrayList;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    public List<ZooNode> userExhibits;
    private List<ZooNode> exhibits;
    public RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Zoo Seeker");

        userExhibits = new ArrayList<>();

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
        searchIntent.putExtra("exhibitsJSON", gson.toJson(exhibits));
        startActivity(searchIntent);
        return super.onOptionsItemSelected(item);
    }
}