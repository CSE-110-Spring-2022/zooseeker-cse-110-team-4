package com.example.zooapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

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

@Entity(tableName = "zoo_node_list")
public class ZooNode {
    @PrimaryKey(autoGenerate = true)
    public long value;

    @NonNull
    public String id;
    public String kind;
    public String name;

    @TypeConverters(TagsConverter.class)
    public String[] tags;

    public ZooNode(@NonNull String id, String kind, String name, String[] tags ) {
        this.id = id;
        this.kind = kind;
        this.name = name;
        this.tags = tags;
    }

    public static List<ZooNode> loadJSON(Context context, String path) {
        try {
            InputStream inputStream = context.getAssets().open(path);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            Type type = new TypeToken<List<ZooNode>>(){}.getType();
            return gson.fromJson(reader, type);
        } catch(IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public String toString() {
        return "ZooNode{" +
                "id='" + id + '\'' +
                ", kind='" + kind + '\'' +
                ", name='" + name + '\'' +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }
}
