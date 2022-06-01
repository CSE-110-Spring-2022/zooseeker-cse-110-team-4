package com.example.zooapp.Ultility;

import android.location.Location;

import com.example.zooapp.Data.ZooNode;
import com.example.zooapp.Interface.ZooNodeDao;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to set up the locations of each exhibit
 */
public class ExhibitLocations {
    //Public fields
    public List<Location> exhibitLocations, allLocations;
    public List<ZooNode> exhibitsSubList, allZooNodes;

    //Private fields
    private ZooNodeDao dao;

    /**
     * Constructor
     *
     * @param dao the dao storing the list of exhibits
     */
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

    /**
     * Sets up the locations of each exhibit in the zoo and stores them in exhibitLocations
     *
     * @param exhibits the list of exhibits in the zoo
     */
    public void setupExhibitLocations(List<ZooNode> exhibits) {
        if( exhibits == null )
            return;
        exhibitLocations.clear();
        exhibitsSubList = new ArrayList<>(exhibits);

        //Go through each exhibit and get its location
        for(var zooNode: exhibits) {
            ZooNode updateZooNode = (zooNode.group_id == null) ? zooNode :
                    dao.getById(zooNode.group_id);
            Location zooNodeLocation = new Location(updateZooNode.id);
            zooNodeLocation.setLatitude(Double.parseDouble(updateZooNode.lat));
            zooNodeLocation.setLongitude(Double.parseDouble(updateZooNode.lng));
            exhibitLocations.add(zooNodeLocation);
        }
    }

    /**
     * Get the location of a single ZooNode
     *
     * @param zooNode the zooNode we want to find the location of
     * @return a Location of the zooNode
     */
    public Location getZooNodeLocation(ZooNode zooNode) {
        if( zooNode == null ) {
            return null;
        }
        ZooNode updateZooNode = (zooNode.group_id == null) ? zooNode :
                dao.getById(zooNode.group_id);
        Location result = null;
        for(Location zooNodeLocation: exhibitLocations) {
            if( updateZooNode.id.equals(zooNodeLocation.getProvider()) ) {
                result = zooNodeLocation;
                break;
            }
        }
        return result;
    }

    /**
     * Returns the exhibit that is closest to the user's location
     *
     * @param currentLocation the user's current location
     * @return a Location of the closest zooNode
     */
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
