package com.bonny.bonnyphc.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.bonny.bonnyphc.R;
import com.bonny.bonnyphc.models.FormDataHolder;
import com.bonny.bonnyphc.models.ParentModel;

import java.util.ArrayList;

/**
 * @author AdityaKulkarni
 */

public class ParentAutoCompleteAdapter extends ArrayAdapter<ParentModel> {

    private Context context;
    private ArrayList<ParentModel> parentModels, suggestions, tempModel;
    private String TAG = getClass().getSimpleName();

    public ParentAutoCompleteAdapter(Context context, int resource, ArrayList<ParentModel> parentModels) {
        super(context, resource, parentModels);
        this.context = context;
        this.parentModels = parentModels;
        this.suggestions = new ArrayList(parentModels);
        this.tempModel = new ArrayList(parentModels);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.custom_spinner_row, parent, false);
        TextView tvParentName = convertView.findViewById(R.id.tvCustomSpinnerItem);
        tvParentName.setBackground(context.getResources().getDrawable(R.drawable.eclipse_white));
        tvParentName.setTextColor(context.getResources().getColor(R.color.colorAccent));
        tvParentName.setText(parentModels.get(position).getFirst_name()
                + " "
                + parentModels.get(position).getLast_name()
                + " ("
                + parentModels.get(position).getContact()
                + ")");
        FormDataHolder.parent = parentModels.get(position).getId();
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            ParentModel parentModel = (ParentModel) resultValue;
            return parentModel.getFirst_name() + " " + parentModel.getLast_name()
                    + " (" + parentModel.getContact() + ")";
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if (charSequence != null) {
                suggestions.clear();
                for (ParentModel people : tempModel) {
                    if (people.getFirst_name().toLowerCase().startsWith(charSequence.toString().toLowerCase())) {
                        suggestions.add(people);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }

        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            ArrayList<ParentModel> c = (ArrayList<ParentModel>) filterResults.values;
            if (filterResults != null && filterResults.count > 0) {
                clear();
                for (ParentModel cust : c) {
                    add(cust);
                    notifyDataSetChanged();
                }
            } else {
                clear();
                notifyDataSetChanged();
            }
        }
    };
}
