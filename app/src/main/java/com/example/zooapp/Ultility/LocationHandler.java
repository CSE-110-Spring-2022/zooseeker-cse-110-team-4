package com.example.zooapp.Ultility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.example.zooapp.Data.ZooNode;
import com.example.zooapp.Viewer.DirectionsActivity;
import org.jgrapht.GraphPath;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocationHandler {
    private final LocationListenerImplementation locationListenerImplementation;
    private final DirectionsActivity directionsActivity;

    public LocationHandler(DirectionsActivity directionsActivity) {
        this.directionsActivity = directionsActivity;
        locationListenerImplementation = new LocationListenerImplementation(directionsActivity);
    }

    @SuppressLint("MissingPermission")
    public void setUpLocationListener() {
        var provider = LocationManager.GPS_PROVIDER;
        var locationManager = (LocationManager) directionsActivity.getSystemService(Context.LOCATION_SERVICE);
        var locationListener = locationListenerImplementation;

        locationManager.requestLocationUpdates(provider, 0, 0f,
                locationListener);
    }

    public void replanRoute(ZooNode nearestZooNode) {
        locationListenerImplementation.replanRoute(nearestZooNode);
    }

    public void resetMockLocation() {
        directionsActivity.setMockLocation(null);
    }

    public Location getLocationToUse() {
        return locationListenerImplementation.locationToUse;
    }

    public void setLocationToUse(Location locationToUse) {
        this.locationListenerImplementation.locationToUse = locationToUse;
    }

    public Location getMockLocation() {
        return locationListenerImplementation.mockLocation;
    }

    public void setMockLocation(Location mockLocation) {
        this.locationListenerImplementation.mockLocation = mockLocation;
    }

    public DirectionsActivity getDirectionsActivity() {
        return directionsActivity;
    }

    public LocationListenerImplementation getLocationListenerImplementation() {
        return locationListenerImplementation;
    }
}