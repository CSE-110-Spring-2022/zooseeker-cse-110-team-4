package com.example.zooapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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

@Entity(tableName = "exhibit_list")
public class Exhibit {
    // Public Fields
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String name;
    public String[] tags;

    public Exhibit(@NonNull String name, String[] tags) {
        this.name = name;
        this.tags = tags;
    }

    public static List<Exhibit> loadJSON(Context context, String path) {
        try {
            InputStream inputStream = context.getAssets().open(path);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            Type type = new TypeToken<List<Exhibit>>(){}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public String toString() {
        return "Exhibit{" +
                "name='" + name + '\'' +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }
}
