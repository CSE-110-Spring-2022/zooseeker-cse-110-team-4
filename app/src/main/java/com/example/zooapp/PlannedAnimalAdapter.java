package com.example.zooapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class PlannedAnimalAdapter extends RecyclerView.Adapter<PlannedAnimalAdapter.ViewHolder> {
    private List<ZooNode> sampleAnimals = Collections.emptyList();

    public void setAnimalList(List<ZooNode> newSampleAnimals){
        this.sampleAnimals.clear();
        this.sampleAnimals = newSampleAnimals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.planned_animal_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setAnimal(sampleAnimals.get(position));
    }

    @Override
    public int getItemCount() {
        return sampleAnimals.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView;
        private ZooNode sampleAnimal;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            this.textView = itemView.findViewById(R.id.planned_animal_text);
        }
        public ZooNode getAnimal(){return sampleAnimal;}

        public void setAnimal(ZooNode sampleAnimal){
            this.sampleAnimal = sampleAnimal;
            this.textView.setText(sampleAnimal.name);
        }
    }
}
