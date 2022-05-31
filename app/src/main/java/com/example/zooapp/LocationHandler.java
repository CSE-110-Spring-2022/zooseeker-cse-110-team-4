package com.example.zooapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import org.jgrapht.GraphPath;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocationHandler {
    @VisibleForTesting
    private Location mockLocation;
    @VisibleForTesting
    private Location locationToUse;
    private final DirectionsActivity directionsActivity;

    public LocationHandler(DirectionsActivity directionsActivity) {
        this.directionsActivity = directionsActivity;
    }

    @SuppressLint("MissingPermission")
    public void setUpLocationListener() {
        var provider = LocationManager.GPS_PROVIDER;
        var locationManager = (LocationManager) directionsActivity.getSystemService(Context.LOCATION_SERVICE);
        var locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (DirectionsActivity.replanAlertShown) {
                    return;
                }
                locationToUse = (mockLocation == null) ? location : mockLocation;
                Log.d("Location", String.format("Location changed: %s", directionsActivity.getLocationToUse()));
                if (directionsActivity.isBackwards()) {
                    backwardsDirections();
                    return;
                }
                var subListSize = (directionsActivity.getCurrIndex() >= directionsActivity.getUserListShortestOrder().size() - 2) ?
                        directionsActivity.getUserListShortestOrder().size() : directionsActivity.getUserListShortestOrder().size() - 1;
                directionsActivity.getExhibitLocations().setupExhibitLocations(directionsActivity.getUserListShortestOrder()
                        .subList(directionsActivity.getCurrIndex() + 1, subListSize));
                Log.d("Location", "" + directionsActivity.getCurrIndex());
                var nearestZooNode =
                        directionsActivity.getExhibitLocations().getZooNodeClosestToCurrentLocation(directionsActivity.getLocationToUse());
                directionsActivity.getSetDirections().setGraphPath(directionsActivity.getAlgorithm().runPathAlgorithm(nearestZooNode,
                        directionsActivity.getExhibitLocations().exhibitsSubList));
                var closestExhibitId = directionsActivity.getAlgorithm().getClosestExhibitId();

                var displayId = (directionsActivity.getSetDirections().getDisplay().group_id != null) ?
                        directionsActivity.getSetDirections().getDisplay().group_id : directionsActivity.getSetDirections().getDisplay().id;

                if (!closestExhibitId.equals(displayId) && DirectionsActivity.canCheckReplan) {
                    Log.d("Check Location", "New Location: " + closestExhibitId
                            + " / Old Location: " + displayId);
                    if (!DirectionsActivity.recentlyYesReplan)
                        directionsActivity.promptReplan();
                    if (DirectionsActivity.check) {
                        replanRoute(nearestZooNode);
                    }
                } else {
                    directionsActivity.getSetDirections().setGraphPath(directionsActivity.getAlgorithm().runPathAlgorithm(nearestZooNode,
                            directionsActivity.getExhibitLocations().exhibitsSubList.subList(0, 1)));
                    GraphPath<String, IdentifiedWeightedEdge> graphPath = directionsActivity.getSetDirections().getGraphPath();
                    if (DirectionsActivity.directionsDetailedText) {
                        directionsActivity.getSetDirections().setDetailedDirectionsText(graphPath);
                    } else {
                        directionsActivity.getSetDirections().setBriefDirectionsText(graphPath);
                    }
                }
            }
        };

        locationManager.requestLocationUpdates(provider, 0, 0f,
                locationListener);
    }

    public void replanRoute(ZooNode nearestZooNode) {
        int subListSize;
        // Rerun algorithm from current location
        Log.d("Location", directionsActivity.getExhibitLocations().exhibitsSubList.toString());
        Log.d("Check Location", nearestZooNode.toString());
        Log.d("Check Location", "" + directionsActivity.getCurrIndex());

        // Get the new List of graph paths with the remaining exhibits
        var reorderedExhibits = directionsActivity.getAlgorithm()
                .runChangedLocationAlgorithm(nearestZooNode,
                        directionsActivity.getUserListShortestOrder().subList(directionsActivity.getCurrIndex() + 1,
                                directionsActivity.getUserListShortestOrder().size() - 1));
        Log.d("Check Location", "New Graph Path: " + reorderedExhibits.toString());
        var originalVisitedExhibits =
                directionsActivity.getSetDirections().getGraphPaths().subList(0, directionsActivity.getCurrIndex());
        Log.d("Check Location", "Old Graph Path: " + originalVisitedExhibits);
        directionsActivity.getSetDirections().setGraphPaths(Stream.concat(originalVisitedExhibits.stream(),
                reorderedExhibits.stream()).collect(Collectors.toList()));

        // Get the new List of zoo nodes in the new shortest order of the remaining
        // exhibits
        Log.d("Check Location", "Graph Plan Replan: " + directionsActivity.getSetDirections().getGraphPaths().toString());
        var reorderedShortestOrder = directionsActivity.getAlgorithm().getNewUserListShortestOrder();
        Log.d("Check Location", "New Order: " + reorderedShortestOrder.toString());
        var originalVisitedShortestOrder = directionsActivity.getUserListShortestOrder()
                .subList(0, directionsActivity.getCurrIndex() + 1);
        Log.d("Check Location", "Old Beginning: " + originalVisitedShortestOrder.toString());
        directionsActivity.setUserListShortestOrder(Stream.concat(originalVisitedShortestOrder.stream(),
                reorderedShortestOrder.stream()).collect(Collectors.toList()));
        Log.d("Check Location", "Replan Complete: " + directionsActivity.getUserListShortestOrder().toString());
        Log.d("Location", directionsActivity.getUserListShortestOrder().toString());

        // Set up for the exhibitLocations class
        subListSize = (directionsActivity.getCurrIndex() >= directionsActivity.getUserListShortestOrder().size() - 2) ?
                directionsActivity.getUserListShortestOrder().size() : directionsActivity.getUserListShortestOrder().size() - 1;
        directionsActivity.getExhibitLocations().setupExhibitLocations(directionsActivity.getUserListShortestOrder()
                .subList(directionsActivity.getCurrIndex() + 1, subListSize));
        Log.d("Check Location", directionsActivity.getExhibitLocations().exhibitsSubList.toString());
        nearestZooNode =
                directionsActivity.getExhibitLocations().getZooNodeClosestToCurrentLocation(directionsActivity.getLocationToUse());

        // Find the new path to display
        directionsActivity.getSetDirections().setGraphPath(directionsActivity.getAlgorithm().runPathAlgorithm(nearestZooNode,
                directionsActivity.getExhibitLocations().exhibitsSubList));

        GraphPath<String, IdentifiedWeightedEdge> graphPath = directionsActivity.getSetDirections().getGraphPath();
        if (DirectionsActivity.directionsDetailedText) {
            directionsActivity.getSetDirections().setDetailedDirectionsText(graphPath);
        } else {
            directionsActivity.getSetDirections().setBriefDirectionsText(graphPath);
        }
        DirectionsActivity.check = false;
        DirectionsActivity.recentlyYesReplan = false;
    }

    private void backwardsDirections() {
        directionsActivity.getSetDirections().setGraphPath(
                directionsActivity.getAlgorithm().runReversePathAlgorithm(
                        directionsActivity.getExhibitLocations().getZooNodeClosestToCurrentLocation(
                                directionsActivity.getLocationToUse()),
                directionsActivity.getUserListShortestOrder().get(
                        directionsActivity.getCurrIndex() + 1)));

        // Setting the direction text
        GraphPath<String, IdentifiedWeightedEdge> graphPath = directionsActivity.getSetDirections()
                .getGraphPath();
        if (DirectionsActivity.directionsDetailedText) {
            directionsActivity.getSetDirections().setDetailedDirectionsText(graphPath);
        } else {
            directionsActivity.getSetDirections().setBriefDirectionsText(graphPath);
        }
        return;
    }

    void resetMockLocation() {
        directionsActivity.setMockLocation(null);
    }

    public Location getLocationToUse() {
        return locationToUse;
    }

    public void setLocationToUse(Location locationToUse) {
        this.locationToUse = locationToUse;
    }

    public Location getMockLocation() {
        return mockLocation;
    }

    public void setMockLocation(Location mockLocation) {
        this.mockLocation = mockLocation;
    }
}