package com.example.zooapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

import java.util.Collections;
import java.util.List;

/**
 * Adapter for the recycler view displaying the route plan summary
 */
public class RoutePlanSummaryAdapter extends RecyclerView.Adapter<RoutePlanSummaryAdapter.ViewHolder> {
    // Private fields
    private List<ZooNode> userAnimals = Collections.emptyList();
    private List<Double> exhibitDistances = Collections.emptyList();

    private static final String TEXT_VIEW_FORMAT = "%s (%.0f ft)";

    /**
     * Sets the list of animals (exhibits) the user has chosen
     * and the distances to each exhibit
     *
     * @param newSampleAnimals List of animals the user has chosen
     * @param exhibitDistances list of exhibit distances from entrance gate for each
     */
    public void setAnimalList(List<ZooNode> newSampleAnimals, List<Double> exhibitDistances){
        if (newSampleAnimals.size() != exhibitDistances.size()) {
            throw new IllegalArgumentException("userAnimal Size different from exhibitDistances size");
        }
        this.userAnimals.clear();
        this.userAnimals = newSampleAnimals;
        this.exhibitDistances.clear();
        this.exhibitDistances = exhibitDistances;
        notifyDataSetChanged();
    }


    /**
     * Sets the view holder with the proper animal information
     *
     * @param parent Recycler view
     * @param viewType Not used
     * @return A newly created view holder with a click listener
     */
    @NonNull
    @Override
    public RoutePlanSummaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.planned_animal_list,parent,false);
        return new RoutePlanSummaryAdapter.ViewHolder(view);
    }

    /**
     * Sets the view holder with correct values
     *
     * @param holder Current view holder
     * @param position Position of view holder in recycler view
     */
    @Override
    public void onBindViewHolder(@NonNull RoutePlanSummaryAdapter.ViewHolder holder, int position) {
        if (userAnimals.size() != exhibitDistances.size()) {
            Log.e("Error", "userAnimal Size different from exhibitDistances size");
        }
        if (position > userAnimals.size()) {
            Log.e("Error", "position is " + position + ", but userAnimals size is" + userAnimals.size());
        }
        holder.setAnimal(userAnimals.get(position), exhibitDistances.get(position));
    }

    /**
     * Total amount of planned animals
     *
     * @return Total amount of planned animals
     */
    @Override
    public int getItemCount() {
        return userAnimals.size();
    }

    /**
     * Class for the view holder for planned animals
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{
        // Private fields
        private final TextView textView;
//        private ZooNode userAnimal;
//        private double distanceToAnimal;
        private String textDisplay;

        /**
         * Constructor
         *
         * @param itemView Current item view
         */
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            this.textView = itemView.findViewById(R.id.planned_animal_text);
        }

        /**
         * Set animal with correct values
         *
         * @param userAnimal Animal to be used to get correct values
         * @param distanceToAnimal distance to animal exhibit
         */
        public void setAnimal(ZooNode userAnimal, double distanceToAnimal){
            this.textDisplay = String.format(TEXT_VIEW_FORMAT, userAnimal.name, distanceToAnimal);
            this.textView.setText(textDisplay);
        }
    }
}
