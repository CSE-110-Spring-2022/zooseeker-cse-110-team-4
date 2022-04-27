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
    List<Long> insertAll(List<Exhibit> zooNodeList);

    @Query("SELECT * FROM `exhibit_list` WHERE `id`=:id")
    Exhibit getbyId(long id);

    @Query("SELECT * FROM `exhibit_list` WHERE `name`=:name")
    Exhibit getByName(String name);

    @Query("SELECT * FROM `exhibit_list` ORDER BY `id`")
    List<Exhibit> getAll();

    @Update
    int update(Exhibit exhibit);

    @Delete
    int delete(Exhibit exhibit);
}
