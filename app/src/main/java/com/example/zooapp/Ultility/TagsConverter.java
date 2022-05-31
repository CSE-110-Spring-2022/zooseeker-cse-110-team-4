package com.example.zooapp.Ultility;

import androidx.room.TypeConverter;

/**
 * This class is used to convert the tags of each exhibit into one string
 */
public class TagsConverter {

    /**
     * This method takes an array of tags and converts it into a single string
     *
     * @param String[] the tags of an exhibit
     * @return a String made up of all of the tags
     */
    @TypeConverter
    public String tagsToStoredString(String[] tags) {
        String value = "";

        for( String tag: tags ) {
            value += tag + ",";
        }

        return value;
    }

    /**
     * This method takes a string and converts it to an array of tags
     *
     * @param String the tags of an exhibit given in a string
     * @return String[] of the seprated tags
     */
    @TypeConverter
    public String[] stringToTagsArray(String tagString) {
        String[] result = tagString.split(",");
        return result;
    }
}
