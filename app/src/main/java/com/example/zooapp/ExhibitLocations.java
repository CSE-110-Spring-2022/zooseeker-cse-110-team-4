package com.example.zooapp;

import android.location.Location;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class ExhibitLocations {
    public List<Location> exhibitLocations;
    public List<ZooNode> exhibitsSubList;

    private ZooNodeDao dao;

    public ExhibitLocations(ZooNodeDao dao) {
        exhibitLocations = new ArrayList<>();
        exhibitsSubList = new ArrayList<>();
        this.dao = dao;
    }

    public void setupExhibitLocations(List<ZooNode> exhibits) {
        if( exhibits == null )
            return;
        exhibitLocations.clear();
        exhibitsSubList = new ArrayList<>(exhibits);
        for(var zooNode: exhibits) {
            ZooNode updateZooNode = (zooNode.parent_id == null) ? zooNode :
                    dao.getById(zooNode.parent_id);
            Location zooNodeLocation = new Location(updateZooNode.id);
            zooNodeLocation.setLatitude(Double.parseDouble(updateZooNode.lat));
            zooNodeLocation.setLongitude(Double.parseDouble(updateZooNode.lng));
            exhibitLocations.add(zooNodeLocation);
        }
    }

    public Location getZooNodeLocation(ZooNode zooNode) {
        if( zooNode == null ) {
            return null;
        }
        ZooNode updateZooNode = (zooNode.parent_id == null) ? zooNode :
                dao.getById(zooNode.parent_id);
        Location result = null;
        for(Location zooNodeLocation: exhibitLocations) {
            if( updateZooNode.id.equals(zooNodeLocation.getProvider()) ) {
                result = zooNodeLocation;
                break;
            }
        }
        return result;
    }
}
