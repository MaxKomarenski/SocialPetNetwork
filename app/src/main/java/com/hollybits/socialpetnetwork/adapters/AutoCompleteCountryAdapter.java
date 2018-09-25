package com.hollybits.socialpetnetwork.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.models.Country;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteCountryAdapter extends ArrayAdapter<Country> {

    private List<Country> countryListFull;

    public AutoCompleteCountryAdapter(@NonNull Context context,  @NonNull List<Country> countryList) {
        super(context, 0, countryList);
        countryListFull = new ArrayList<>(countryList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return countryFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.country_simple_dropdown_item_line,
                    parent, false);
        }
        TextView name = convertView.findViewById(R.id.nameOfCountry);
        ImageView flag = convertView.findViewById(R.id.imageOfCountry);
        Country country = getItem(position);
        if(country != null){
            name.setText(country.getName());
            Resources resources = this.getContext().getResources();
            int resourceId = resources.getIdentifier(country.getCode().toLowerCase(), "drawable", MainActivity.getInstance().getPackageName());
            flag.setImageResource(resourceId);
        }


        return convertView;
    }

    private Filter countryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Country> suggestions = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                suggestions.addAll(countryListFull);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Country c: countryListFull) {
                    if(c.getName().toLowerCase().startsWith(filterPattern)){
                        suggestions.add(c);
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
            addAll((List<Country>) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Country) resultValue).getName();
        }
    };
}
