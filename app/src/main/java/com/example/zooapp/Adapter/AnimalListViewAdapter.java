package com.example.zooapp.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zooapp.Data.ZooNode;
import com.example.zooapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This Adapter is used for displaying the animals that will be searched for.
 */
public class AnimalListViewAdapter extends RecyclerView.Adapter<AnimalListViewAdapter.ViewHolder>
        implements Filterable {
    // Private fields
    private List<ZooNode> zooNodeList, zooNodeListFull;
    private ClickListener clickListener;

    /**
     * Constructor
     *
     * @param zooNodeList List of all the exhibits in the zoo
     * @param clickListener Click listener for when the recycler view is clicked
     */
    public AnimalListViewAdapter(List<ZooNode> zooNodeList, ClickListener clickListener) {
        this.zooNodeList = zooNodeList;
        this.zooNodeListFull = new ArrayList<>(zooNodeList);
        this.clickListener = clickListener;
    }

    /**
     * Sets the view holder with the proper zoo node information
     *
     * @param parent Recycler view
     * @param viewType Not used
     * @return A newly created view holder with a click listener
     */
    @NonNull
    @Override
    public AnimalListViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                               int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.zoo_node_item,parent,false);

        return new ViewHolder(view, clickListener);
    }

    /**
     * Sets the view holder to display the correct animal name in the recycler view
     *
     * @param holder The current view holder
     * @param position The position of the view holder in the recycler view
     */
    @Override
    public void onBindViewHolder(@NonNull AnimalListViewAdapter.ViewHolder holder, int position) {
        holder.setZooAnimalName(zooNodeList.get(position));
    }

    /**
     * The number of exhibits
     *
     * @return The number of exhibits
     */
    @Override
    public int getItemCount() {
        return zooNodeList.size();
    }

    /**
     * Gets the custom filter
     *
     * @return A new custom filter
     */
    @Override
    public Filter getFilter() {
        return zooNodeFilter;
    }

    /**
     * A newly created filter to customly filter the animals by their typed tag
     */
    private Filter zooNodeFilter = new Filter() {
        /**
         * Custom filtering method
         *
         * @param constraint Input into search view
         * @return Results of the filter
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // Display list
            List<ZooNode> filteredList = new ArrayList<>();

            if( constraint == null || constraint.length() == 0 ) {
                // If nothing entered, displayed the full list
                filteredList.addAll(zooNodeListFull);
            } else {
                // Entered value in the search bar
                String filterPattern = constraint.toString().toLowerCase().trim();
                for( ZooNode zooNode: zooNodeListFull ) {
                    for( String tag: zooNode.tags ) {
                        if( tag.toLowerCase().contains(filterPattern) ) {
                            // Add animal when their tag is being searched for
                            filteredList.add(zooNode);
                            break;
                        }
                        /* For only Prefix and Suffix
                        if( tag.toLowerCase().startsWith(filterPattern) ||
                                tag.toLowerCase().endsWith(filterPattern) ) {
                            // Add animal when their tag is being searched for
                            filteredList.add(zooNode);
                            break;
                        }*/
                    }
                }
            }
            // Used during testing to see if the filter was working
            Log.d("Text Filter", "Filtering text");

            // Results of the filter
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        /**
         * Publish results so the user can see
         *
         * @param charSequence Input to search view
         * @param filterResults Results of the custom filter
         */
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            zooNodeList.clear();
            zooNodeList.addAll((List)filterResults.values);
            notifyDataSetChanged();
            Log.d("Text Filter", "Publish text");
        }
    };

    /**
     * Class for the view holder, custom for exhibits in the zoo
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Public fields
        TextView zooAnimalName;
        ZooNode zooNode;
        ClickListener clickListener;

        /**
         * Constructor
         *
         * @param itemView The item view in the view holder
         * @param clickListener Click listener for when a view holder is clicked
         */
        public ViewHolder(@NonNull View itemView, ClickListener clickListener) {
            super(itemView);
            zooAnimalName = itemView.findViewById(R.id.zooAnimalName);
            this.clickListener = clickListener;

            itemView.setOnClickListener(this);
        }

        /**
         * Setter for animal name
         *
         * @param zooNode Node that contains the animal name to be set
         */
        public void setZooAnimalName(ZooNode zooNode) {
            this.zooNode = zooNode;
            this.zooAnimalName.setText(this.zooNode.name);
        }

        /**
         * On click method for when the view holder is clicked
         *
         * @param view The view that is clicked
         */
        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition());
        }
    }

    /**
     * Interface for a custom ClickListener
     */
    public interface ClickListener {
        void onItemClick(int position);
    }
}
