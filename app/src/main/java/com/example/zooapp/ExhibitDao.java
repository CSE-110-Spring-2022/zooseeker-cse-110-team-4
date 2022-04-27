package com.example.zooapp;

import java.util.List;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ExhibitDao {
    @Insert
    long insert(Exhibit exhibit);

    @Insert
    List<Long> insertAll(List<Exhibit> exhibitList);

    @Query("SELECT * FROM `exhibit_list` WHERE `id`=:id")
    Exhibit getById(long id);

    @Query("SELECT * FROM `exhibit_list` WHERE `itemType`=:itemType")
    Exhibit getByName(String animalName);

    @Query("SELECT * FROM `exhibit_list` WHERE `tag`=:tag")
    Exhibit getByClassification(String animalClassification);

    //@Query("SELECT * FROM `exhibit_list` ORDER BY `order`")
    //List<Exhibit> getAll();

    @Update
    int update(Exhibit exhibit);

    @Delete
    int delete(Exhibit exhibit);

}
