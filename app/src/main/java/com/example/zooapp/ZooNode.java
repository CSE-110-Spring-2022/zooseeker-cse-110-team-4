package com.example.zooapp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class represents each individual ZooNode
 */
@Entity(tableName = "zoo_node_list")
public class ZooNode {
    @PrimaryKey(autoGenerate = true)
    public long value;

    @NonNull
    public String id;
    public String group_id;
    public String kind;
    public String name;
    public String lat;
    public String lng;

    @TypeConverters(TagsConverter.class)
    public String[] tags;

    /**
     * Constructor
     *
     * @param id, group_id, kind, name, lat, lng, tags
     */
    public ZooNode(@NonNull String id, String group_id, String kind, String name, String[] tags,
                   String lat, String lng ) {
        this.id = id;
        this.group_id = group_id;
        this.kind = kind;
        this.name = name;
        this.tags = tags;
        this.lat = lat;
        this.lng = lng;
    }

    /**
     * Loads information about ZooNode vertices from a JSON file
     *
     * @param context
     * @param path to information file
     * @return List of ZooNodes
     */
    public static List<ZooNode> loadJSON(Context context, String path) {
        Log.d("Info", "Loading JSON file");
        try {
            InputStream inputStream = context.getAssets().open(path);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            Type type = new TypeToken<List<ZooNode>>(){}.getType();
            List<ZooNode> list = gson.fromJson(reader, type);
            Log.d("Info", list.toString());
            return list;
        } catch(IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Converts information about a ZooNode into a String
     *
     * @return information of the ZooNode
     */
    @Override
    public String toString() {
        String result = String.format("ZooNode{id='%s', kind='%s', name='%s', tags=%s}",
                id,
                kind,
                name,
                Arrays.toString(tags));
        return result;
    }
}
