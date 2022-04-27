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

    @Query("SELECT * FROM Exhibit WHERE `id`=:id")
    Exhibit get(long id);

    @Query("SELECT * FROM Exhibit ORDER BY `animalName`")
    List<Exhibit> getAll();

    @Update
    int update(Exhibit todoListItem);

    @Delete
    int delete(Exhibit todoListItem);

}
