package com.example.zooapp;

import android.location.Location;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class ExhibitLocations {
    public List<Location> exhibitLocations, allLocations;
    public List<ZooNode> exhibitsSubList, allZooNodes;

    private ZooNodeDao dao;

    public ExhibitLocations(ZooNodeDao dao) {
        exhibitLocations = new ArrayList<>();
        exhibitsSubList = new ArrayList<>();
        this.dao = dao;
        allZooNodes = this.dao.getAll();
        allLocations = new ArrayList<>();
        for(var zooNode: allZooNodes) {
            ZooNode updateZooNode = (zooNode.group_id == null) ? zooNode :
                    dao.getById(zooNode.group_id);
            Location zooNodeLocation = new Location(updateZooNode.id);
            zooNodeLocation.setLatitude(Double.parseDouble(updateZooNode.lat));
            zooNodeLocation.setLongitude(Double.parseDouble(updateZooNode.lng));
            allLocations.add(zooNodeLocation);
        }
    }

    public void setupExhibitLocations(List<ZooNode> exhibits) {
        if( exhibits == null )
            return;
        exhibitLocations.clear();
        exhibitsSubList = new ArrayList<>(exhibits);
        for(var zooNode: exhibits) {
            ZooNode updateZooNode = (zooNode.group_id == null) ? zooNode :
                    dao.getById(zooNode.group_id);
            Location zooNodeLocation = new Location(updateZooNode.id);
            zooNodeLocation.setLatitude(Double.parseDouble(updateZooNode.lat));
            zooNodeLocation.setLongitude(Double.parseDouble(updateZooNode.lng));
            exhibitLocations.add(zooNodeLocation);
        }
    }

    public ZooNode getZooNodeClosestToCurrentLocation(Location currentLocation) {
        double minDistance = Double.MAX_VALUE;
        Location minLocation = currentLocation;
        for(Location zooNodeLocation: allLocations) {
            double distance = currentLocation.distanceTo(zooNodeLocation);
            if( minDistance > distance ) {
                minLocation = zooNodeLocation;
                minDistance = distance;
            }
        }
        ZooNode result = null;
        for(ZooNode zooNode: allZooNodes) {
            if( minLocation.getProvider().equals(zooNode.id) ) {
                result = zooNode;
                break;
            }
        }
        return result;
    }
}
