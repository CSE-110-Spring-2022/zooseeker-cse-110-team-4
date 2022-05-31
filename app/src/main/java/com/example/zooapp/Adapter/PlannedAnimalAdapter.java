package com.example.zooapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zooapp.Data.ZooNode;
import com.example.zooapp.R;

import java.util.Collections;
import java.util.List;

/**
 * Adapter for the recycler view displaying all the planned animals from the user
 */
public class PlannedAnimalAdapter extends RecyclerView.Adapter<PlannedAnimalAdapter.ViewHolder> {
    // Private fields
    private List<ZooNode> userAnimals = Collections.emptyList();

    /**
     * Sets the list of animals the user has chosen
     *
     * @param newSampleAnimals List of animals the user has chosen
     */
    public void setAnimalList(List<ZooNode> newSampleAnimals){
        this.userAnimals.clear();
        this.userAnimals = newSampleAnimals;
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.planned_animal_list,parent,false);
        return new ViewHolder(view);
    }

    /**
     * Sets the view holder with correct values
     *
     * @param holder Current view holder
     * @param position Position of view holder in recycler view
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setAnimal(userAnimals.get(position));
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
        private ZooNode userAnimal;

        /**
         * Constructor
         *
         * @param itemView Current item view
         */
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            this.textView = itemView.findViewById(R.id.planned_animal_text);
        }
        public ZooNode getAnimal(){return userAnimal;}

        /**
         * Set animal with correct values
         *
         * @param userAnimal Animal to be used to get correct values
         */
        public void setAnimal(ZooNode userAnimal){
            this.userAnimal = userAnimal;
            this.textView.setText(userAnimal.name);
        }
    }
}
