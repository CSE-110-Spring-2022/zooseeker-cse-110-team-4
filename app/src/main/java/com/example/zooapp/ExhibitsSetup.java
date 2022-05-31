package com.example.zooapp;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is used to set up the list of all exhibits in the zoo for SearchActivity
 */
public class ExhibitsSetup {

    //Private fields
    private final Activity activity;
    private List<ZooNode> totalExhibits;

    /**
     * Constructor
     *
     * @param Activity The activity where we need the list of exhibits
     */
    public ExhibitsSetup(Activity a) {
        this.activity = a;
        totalExhibits = new ArrayList<>();

    }

    /**
     * Takes the list of exhibits stored in a ZooNodeDao and sorts the exhibits alphabetically
     * Adds the sorted list of exhibits into the variable totalExhibits
     */
    void getExhibitInformation() {
        // Get all the animals available in the zoo, exhibits
        activity.deleteDatabase("zoo_app.db");
        ZooNodeDao dao = ZooNodeDatabase.getSingleton(activity).ZooNodeDao();
        totalExhibits = dao.getZooNodeKind("exhibit");
        List<String> toSort = new ArrayList<String>();

        // Sort the animals in alphabetical order
        for (int i = 0; i < totalExhibits.size(); i++) {
            toSort.add(totalExhibits.get(i).name);
        }
        Collections.sort(toSort);
        totalExhibits.clear();

        //Add the sorted animals into totalExhibits
        for (int i = 0; i < toSort.size(); i++) {
            totalExhibits.add(dao.getByName(toSort.get(i)));
        }
        Log.d("Search View", "Exhibits sorted");
    }

    /**
     * Checks if the selected animal from the search bar has already been added to the list of planned animals.
     * If so, do nothing, else add the selected animal to the plannedAnimalDao
     *
     * @param int position of the item in the recycler view in SearchActivity
     */
    void addAnimalPlannedList(int position){

        boolean animalExists = false;

        // Checks if the zoo node has already been added
        PlannedAnimalDao plannedAnimalDao = PlannedAnimalDatabase.getSingleton(activity).plannedAnimalDao();
        for( ZooNode zooNode: plannedAnimalDao.getAll()) {
            if( zooNode.name.equals(totalExhibits.get(position).name) ) {
                animalExists = true;
                Log.d("Search View", "Appear if the animal is already in the list");
            }
        }
        // Only add if the animal hasn't been added
        if( !animalExists ) {
//            userExhibits.add(totalExhibits.get(position));
            plannedAnimalDao.insert(totalExhibits.get(position));

            Log.d("Search View", "Unique animal added");
        }

    }

    /**
     * Returns a list of all of the planned animals in the Dao
     *
     * @return List of ZooNodes that have been added to the planned animals list Dao
     */
    List<ZooNode> getUserExhibits(){
        PlannedAnimalDao plannedAnimalDao = PlannedAnimalDatabase.getSingleton(activity).plannedAnimalDao();
        return plannedAnimalDao.getAll();
    }

    /**
     * Returns a list of all of the planned animals that have been stored in the variable totalExhibits
     *
     * @return List of ZooNodes that have been added to the planned animals list
     */
    List<ZooNode> getTotalExhibits(){
        return totalExhibits;
    }

    /**
     * Sets the dao storing the list of planned animals to the new given list
     *
     * @param List of ZooNodes to plan
     */
    void setUserExhibits(List<ZooNode> e){
        PlannedAnimalDao plannedAnimalDao = PlannedAnimalDatabase.getSingleton(activity).plannedAnimalDao();

        //clear list
        plannedAnimalDao.deleteAll();

        //set the new list of exhibits
        for( ZooNode zooNode: e ) {
            plannedAnimalDao.insert(zooNode);
        }
    }




}