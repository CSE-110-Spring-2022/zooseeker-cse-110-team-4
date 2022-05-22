package com.example.zooapp;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExhibitsSetup {
    private final Activity activity;
    private List<ZooNode> totalExhibits;

    public ExhibitsSetup(Activity a) {

        this.activity = a;
        totalExhibits = new ArrayList<>();

    }

    void getExhibitInformation() {
        // Get all the animals available in the zoo, exhibits
        ZooNodeDao dao = ZooNodeDatabase.getSingleton(activity).ZooNodeDao();
        totalExhibits = dao.getZooNodeKind("exhibit");
        List<String> toSort = new ArrayList<String>();

        // Sort the animals in alphabetical order
        for (int i = 0; i < totalExhibits.size(); i++) {
            toSort.add(totalExhibits.get(i).name);
        }
        Collections.sort(toSort);
        totalExhibits.clear();

        for (int i = 0; i < toSort.size(); i++) {
            totalExhibits.add(dao.getByName(toSort.get(i)));
        }
        Log.d("Search View", "Exhibits sorted");
    }

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

    List<ZooNode> getUserExhibits(){
        PlannedAnimalDao plannedAnimalDao = PlannedAnimalDatabase.getSingleton(activity).plannedAnimalDao();
        return plannedAnimalDao.getAll();
    }

    void setUserExhibits(List<ZooNode> e){
        PlannedAnimalDao plannedAnimalDao = PlannedAnimalDatabase.getSingleton(activity).plannedAnimalDao();

        //clear list
        plannedAnimalDao.deleteAll();

        //set the new list of exhibits
        for( ZooNode zooNode: e ) {
            plannedAnimalDao.insert(zooNode);
        }
    }

    List<ZooNode> getTotalExhibits(){
        return totalExhibits;
    }


}