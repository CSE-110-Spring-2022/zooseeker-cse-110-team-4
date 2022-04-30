package com.example.zooapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AnimalListViewAdapter extends RecyclerView.Adapter<AnimalListViewAdapter.ViewHolder> implements Filterable {
    private List<ZooNode> zooNodeList, zooNodeListFull;
    private ClickListener clickListener;

    AnimalListViewAdapter(List<ZooNode> zooNodeList, ClickListener clickListener) {
        this.zooNodeList = zooNodeList;
        this.zooNodeListFull = new ArrayList<>(zooNodeList);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public AnimalListViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.zoo_node_item,parent,false);

        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalListViewAdapter.ViewHolder holder, int position) {
        holder.setZooAnimalName(zooNodeList.get(position));
    }

    @Override
    public int getItemCount() {
        return zooNodeList.size();
    }

    @Override
    public Filter getFilter() {
        return zooNodeFilter;
    }

    private Filter zooNodeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ZooNode> filteredList = new ArrayList<>();

            if( constraint == null || constraint.length() == 0 ) {
                filteredList.addAll(zooNodeListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for( ZooNode zooNode: zooNodeListFull ) {
                    for( String tag: zooNode.tags ) {
                        if( tag.toLowerCase().startsWith(filterPattern) ) {
                            filteredList.add(zooNode);
                            break;
                        }
                    }
                }
            }
            Log.d("Text Filter", "Filtering text");
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            zooNodeList.clear();
            zooNodeList.addAll((List)filterResults.values);
            notifyDataSetChanged();
            Log.d("Text Filter", "Publish text");
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView zooAnimalName;
        ZooNode zooNode;
        ClickListener clickListener;

        public ViewHolder(@NonNull View itemView, ClickListener clickListener) {
            super(itemView);
            zooAnimalName = itemView.findViewById(R.id.zooAnimalName);
            this.clickListener = clickListener;

            itemView.setOnClickListener(this);
        }

        public void setZooAnimalName(ZooNode zooNode) {
            this.zooNode = zooNode;
            this.zooAnimalName.setText(this.zooNode.name);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface ClickListener {
        void onItemClick(int position);
    }
}
