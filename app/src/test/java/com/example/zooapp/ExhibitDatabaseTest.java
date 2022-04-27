package com.example.zooapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ExhibitDatabaseTest {
    private ExhibitDao dao;
    private ExhibitDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.ExhibitDao();
    }

    @Test
    public void testInsert() {
        Exhibit item1 = new Exhibit("gorilla_exhibit", new String[] {"gorilla, ape"});
        Exhibit item2 = new Exhibit("fish_exhibit", new String[] {"fish, ocean"});

        long id1 = dao.insert(item1);
        long id2 = dao.insert(item2);

        assertNotEquals(id1, id2);
    }

    @Test
    public void testGet() {
        Exhibit insertedItem = new Exhibit("gorilla_exhibit", new String[] {"gorilla, ape"});
        long id = dao.insert(insertedItem);

        Exhibit item = dao.getbyId(id);
        assertEquals(id, item.id);
        assertEquals(insertedItem.name, item.name);
        for( int i = 0; i < insertedItem.tags.length; i++ ) {
            assertEquals(insertedItem.tags[i], item.tags[i]);
        }
    }

    @Test
    public void testUpdate() {
        Exhibit insertItem = new Exhibit("gorilla_exhibit", new String[] {"gorilla, ape"});
        long id = dao.insert(insertItem);

        Exhibit item = dao.getbyId(id);
        item.name = "fish_exhibit";
        int itemsUpdated = dao.update(item);
        assertEquals(1, itemsUpdated);

        item = dao.getbyId(id);
        assertNotNull(item);
        assertEquals("fish_exhibit", item.name);
    }

    @Test
    public void testDelete() {
        Exhibit insertItem = new Exhibit("gorilla_exhibit", new String[] {"gorilla, ape"});
        long id = dao.insert(insertItem);

        Exhibit item = dao.getbyId(id);
        int itemsDeleted = dao.delete(item);
        assertEquals(1, itemsDeleted);
        assertNull(dao.getbyId(id));
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }
}
