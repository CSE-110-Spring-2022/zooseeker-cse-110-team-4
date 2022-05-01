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
    private List<SampleAnimal> sampleAnimals = Collections.emptyList();

    public void setAnimalList(List<SampleAnimal> newSampleAnimals){
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
    @Override
    public long getItemId(int position){ return sampleAnimals.get(position).id;}


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView;
        private SampleAnimal sampleAnimal;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            this.textView = itemView.findViewById(R.id.planned_animal_text);
        }
        public SampleAnimal getAnimal(){return sampleAnimal;}

        public void setAnimal(SampleAnimal sampleAnimal){
            this.sampleAnimal = sampleAnimal;
            this.textView.setText(sampleAnimal.name);
        }
    }
}
