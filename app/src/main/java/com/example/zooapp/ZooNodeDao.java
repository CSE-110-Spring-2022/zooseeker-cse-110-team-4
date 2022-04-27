package com.example.zooapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ZooNodeDao {
    @Insert
    long insert(Exhibit exhibit);

    @Insert
    List<Long> insertAll(List<Exhibit> exhibitList);

    @Query("SELECT * FROM `exhibit_list` WHERE `id`=:id")
    Exhibit getById(String id);

    @Query("SELECT * FROM `exhibit_list` ORDER BY `id`")
    List<Exhibit> getAll();

    @Update
    int update(Exhibit exhibit);

    @Delete
    int delete(Exhibit exhibit);
}
