package com.bonny.bonnyphc.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bonny.bonnyphc.R;
import com.bonny.bonnyphc.models.FormDataHolder;
import com.bonny.bonnyphc.util.Utils;

import java.util.ArrayList;

/**
 * @author Aditya Kulkarni
 */

public class CusotomBloodGroupAdapter extends ArrayAdapter<String>{

    private Context context;
    private ArrayList<String> strings;
    private String TAG = getClass().getSimpleName();

    public CusotomBloodGroupAdapter(@NonNull Context context, int resource, ArrayList<String> strings) {
        super(context, resource);
        this.context = context;
        this.strings = strings;
    }

    @Override
    public int getCount() {
        return strings.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.custom_spinner_row, parent, false);
        TextView tvParentName = convertView.findViewById(R.id.tvCustomSpinnerItem);
        tvParentName.setBackground(context.getResources().getDrawable(R.drawable.eclipse_white));
        tvParentName.setText(strings.get(position));
        FormDataHolder.bloodGroup = Utils.getRawBloodGroup(strings.get(position));
        if(FormDataHolder.babyModel != null){
            FormDataHolder.babyModel.setBlood_group(Utils.getRawBloodGroup(strings.get(position)));
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.custom_spinner_row, parent, false);
        TextView tvParentName = convertView.findViewById(R.id.tvCustomSpinnerItem);
        tvParentName.setBackground(context.getResources().getDrawable(R.drawable.eclipse_white));
        tvParentName.setText(strings.get(position));
        return convertView;
    }
}
