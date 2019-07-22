package com.bonny.bonnyphc.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bonny.bonnyphc.R;
import com.bonny.bonnyphc.models.ScheduleLists;
import com.bonny.bonnyphc.models.VaccineModel;
import com.bonny.bonnyphc.util.Utils;

import java.util.ArrayList;

/**
 * @author Aditya Kulkarni
 */

public class PendingVaccineListAdapter extends RecyclerView.Adapter<PendingVaccineListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<VaccineModel> vaccineModels;
    private View view;

    public PendingVaccineListAdapter(Context context, ArrayList<VaccineModel> vaccineModels) {
        this.context = context;
        this.vaccineModels = vaccineModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.pending_vaccine_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            if (vaccineModels.get(position).getStatus().equalsIgnoreCase("pending")) {
                holder.cbMarkVaccine.setText(vaccineModels.get(position).getVaccine());

                holder.cbMarkVaccine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (compoundButton.isChecked()) {
                            if (!ScheduleLists.selectedVaccinesForScheduleList.contains(vaccineModels.get(position))) {
                                ScheduleLists.selectedVaccinesForScheduleList.add(vaccineModels.get(position));
                            }
                        } else if (!compoundButton.isChecked()) {
                            ScheduleLists.selectedVaccinesForScheduleList.remove(vaccineModels.get(position));
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return vaccineModels.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox cbMarkVaccine;

        public ViewHolder(View itemView) {
            super(itemView);

            cbMarkVaccine = itemView.findViewById(R.id.cbMarkVaccine);
            cbMarkVaccine.setSelected(true);
        }
    }
}
