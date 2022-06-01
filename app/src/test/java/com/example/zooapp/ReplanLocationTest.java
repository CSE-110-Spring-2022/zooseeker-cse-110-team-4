package com.example.zooapp;

import static android.location.LocationManager.NETWORK_PROVIDER;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.widget.Button;
import android.widget.TextView;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.example.zooapp.Data.PlannedAnimalDatabase;
import com.example.zooapp.Data.ZooNode;
import com.example.zooapp.Data.ZooNodeDatabase;
import com.example.zooapp.Interface.PlannedAnimalDao;
import com.example.zooapp.Interface.ZooNodeDao;
import com.example.zooapp.Ultility.LocationListenerImplementation;
import com.example.zooapp.Viewer.DirectionsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class ReplanLocationTest {
    private DirectionsActivity directionsActivity;
    private Context context;
    private ZooNodeDao dao;
    private ZooNodeDatabase testDb;
    private PlannedAnimalDao planDao;
    private PlannedAnimalDatabase testPlanDb;
    private List<ZooNode> allExhibits;
    private LocationListenerImplementation locationListenerImplementation;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, ZooNodeDatabase.class)
                .allowMainThreadQueries()
                .build();
        ZooNodeDatabase.injectTestDatabase(testDb);

        testPlanDb = Room.inMemoryDatabaseBuilder(context, PlannedAnimalDatabase.class)
                .allowMainThreadQueries()
                .build();
        PlannedAnimalDatabase.injectTestDatabase(testPlanDb);

        List<ZooNode> allZooNodes = ZooNode.loadJSON(context, "sample_node_info.json");
        dao = testDb.ZooNodeDao();
        dao.insertAll(allZooNodes);
        planDao = testPlanDb.plannedAnimalDao();
        allExhibits = dao.getZooNodeKind("exhibit");
        planDao.insert(allExhibits.get(1)); //adding flamingos
        planDao.insert(allExhibits.get(3)); //adding gorillas
        directionsActivity = Robolectric.buildActivity(DirectionsActivity.class).create().get();
        locationListenerImplementation = directionsActivity.getLocationListenerImplementation();
    }

    @After
    public void reset() {
        directionsActivity.replanAlertShown = false;
        directionsActivity.canCheckReplan = true;
        directionsActivity.recentlyYesReplan = false;
    }

    private Location location(String provider, double lat, double lng) {
        Location location = new Location(provider);
        location.setLatitude(lat);
        location.setLongitude(lng);
        location.setTime(System.currentTimeMillis());
        return location;
    }

    @Test
    public void replanPromptAppearTest() {
        Location expectedLocation = location("Mock Benchly", 32.74476120197887,
                -117.18369973246877);

        locationListenerImplementation.onLocationChanged(expectedLocation);
        assertEquals(true, directionsActivity.alertMessage.isShowing());
    }

//    @Test
//    public void replanCompleteTest() {
//        Location expectedLocation = location("Mock Benchly", 32.74476120197887,
//                -117.18369973246877);
//
//        locationListenerImplementation.onLocationChanged(expectedLocation);
//        assertEquals(true, directionsActivity.alertMessage.isShowing());
//
//        Button yesButton = directionsActivity.alertMessage.getButton(DialogInterface.BUTTON_POSITIVE);
//        yesButton.performClick();
//        assertEquals(true, directionsActivity.check);
//        assertEquals(true, directionsActivity.canCheckReplan);
//        assertEquals(false, directionsActivity.replanAlertShown);
//        assertEquals(true, directionsActivity.recentlyYesReplan);
//        locationListenerImplementation.onLocationChanged(expectedLocation);
//        TextView directions = directionsActivity.findViewById(R.id.directions_text);
//
//        assertEquals(expectedLocation, directionsActivity.getLocationToUse());
//
//        String newDirections = " 1. Walk 1400 feet along Monkey Trail towards the 'Gorillas'\n";
//        assertEquals(newDirections, directions.getText().toString());
//    }

    @Test
    public void replanButtonTest() {
        Location expectedLocation = location("Mock Benchly", 32.74476120197887,
                -117.18369973246877);

        locationListenerImplementation.onLocationChanged(expectedLocation);
        assertEquals(true, directionsActivity.alertMessage.isShowing());

        Button replanButton = directionsActivity.findViewById(R.id.replan_button);
        replanButton.performClick();
        TextView directions = directionsActivity.findViewById(R.id.directions_text);

        assertEquals(expectedLocation, directionsActivity.getLocationToUse());

        String newDirections = " 1. Walk 1400 feet along Monkey Trail towards the 'Gorillas'\n";
        assertEquals(newDirections, directions.getText().toString());
    }
}
