package com.bonny.bonnyphc.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bonny.bonnyphc.R;
import com.bonny.bonnyphc.models.AppointmentModel;

import java.util.ArrayList;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<AppointmentModel> appointmentModels;

    public AppointmentsAdapter(Context context, ArrayList<AppointmentModel> appointmentModels) {
        this.context = context;
        this.appointmentModels = appointmentModels;
    }

    @Override
    public AppointmentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.appointment_row_layout, parent, false);

        return new AppointmentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppointmentsAdapter.ViewHolder holder, int position) {
        try {
            holder.tvPlace.setText(appointmentModels.get(position).getAdministered_at().getName());
            holder.tvTime.setText(appointmentModels.get(position).getAdministered_on());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return appointmentModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlace, tvDate, tvTime;
        public ViewHolder(View itemView) {
            super(itemView);

            tvPlace = itemView.findViewById(R.id.tv_place);
            tvTime = itemView.findViewById(R.id.tv_date);
        }
    }
}
