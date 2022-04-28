package com.example.zooapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Exhibit> exhibitList = Exhibit.loadJSON(this, "exhibit_list.json");
        Log.d("Exhibit List Activity", exhibitList.toString());

//        List<ZooNode> zooNodeList = ZooNode.loadJSON(this, "zoo_node_list.json");
//        Log.d("Zoo Node List Activity", zooNodeList.toString());
    }
}