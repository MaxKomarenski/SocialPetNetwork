package com.hollybits.socialpetnetwork.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.models.Breed;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Victor on 27.07.2018.
 */

public class BreedAdapter extends ArrayAdapter<Breed> {

    private List<Breed> allBreads;

    public BreedAdapter(@NonNull Context context,  @NonNull List<Breed> breedList) {
        super(context,0, breedList);
        allBreads = new ArrayList<>(breedList);
    }


    private Filter breedFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults  results = new FilterResults();
            List<Breed> suggestions = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                suggestions.addAll(allBreads);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Breed b:allBreads){
                    if(b.getName().toLowerCase().startsWith(filterPattern)){
                        suggestions.add(b);
                    }
                }
            }
            results.values = suggestions;
            results.count = suggestions.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List<Breed>)results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Breed)resultValue).getName();
        }
    };

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.breed_row, parent, false);
        }
        TextView breedName = convertView.findViewById(R.id.breed_row);
        Breed breed = getItem(position);
        if(breed!=null) {
            breedName.setText(breed.getName());
        }
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return breedFilter;
    }
}
