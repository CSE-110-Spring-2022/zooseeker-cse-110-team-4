package com.example.zooapp;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "animal_list")
public class Exhibit {
    // Public Fields
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String animalName;
    public String animalClassification;
    public String animalLocation;

    public Exhibit(@NonNull String animalName, String animalClassification, String animalLocation) {
        this.animalName = animalName;
        this.animalClassification = animalClassification;
        this.animalLocation = animalLocation;
    }
}
