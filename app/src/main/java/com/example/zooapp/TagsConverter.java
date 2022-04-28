package com.example.zooapp;

import androidx.room.TypeConverter;

public class TagsConverter {
    @TypeConverter
    public String tagsToStoredString(String[] tags) {
        String value = "";

        for( String tag: tags ) {
            value += tag + ",";
        }

        return value;
    }

    @TypeConverter
    public String[] stringToTagsArray(String tagString) {
        String[] result = tagString.split(",");
        return result;
    }
}
