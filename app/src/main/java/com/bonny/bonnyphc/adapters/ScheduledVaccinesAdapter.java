package com.bonny.bonnyphc.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bonny.bonnyphc.R;
import com.bonny.bonnyphc.api.API;
import com.bonny.bonnyphc.config.RetrofitConfig;
import com.bonny.bonnyphc.models.RecordModel;
import com.bonny.bonnyphc.session.SessionManager;
import com.bonny.bonnyphc.util.ProgressDialogUtil;
import com.bonny.bonnyphc.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Aditya Kulkarni
 */

public class ScheduledVaccinesAdapter extends RecyclerView.Adapter<ScheduledVaccinesAdapter.ViewHolder> {

    private final ArrayList<RecordModel> vaccineModels;
    private String TAG = getClass().getSimpleName();
    private Context context;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;

    public ScheduledVaccinesAdapter(ArrayList<RecordModel> vaccineModels, Context context) {
        this.vaccineModels = vaccineModels;
        this.context = context;
        this.sessionManager = new SessionManager(context);
        this.userDetails = sessionManager.getUserDetails();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vaccine_record_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.vaccineModel = vaccineModels.get(position);
        holder.tvVaccineName.setText(Utils.getFormattedVaccine(vaccineModels.get(position).getVaccine()));
        if(vaccineModels.get(position).getStatus().equalsIgnoreCase("pending")){
            holder.tvVaccineStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
            holder.tvVaccineStatus.setText(R.string.pending);
        }else if(vaccineModels.get(position).getStatus().equalsIgnoreCase("cancelled")){
            holder.tvVaccineStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
            holder.tvVaccineStatus.setText(R.string.cancelled);
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
        }else if(vaccineModels.get(position).getStatus().equalsIgnoreCase("administered")){
            holder.tvVaccineStatus.setTextColor(context.getResources().getColor(R.color.green));
            holder.tvVaccineStatus.setText(R.string.administered);
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
        }else if(vaccineModels.get(position).getStatus().equalsIgnoreCase("scheduled")){
            holder.tvVaccineStatus.setTextColor(context.getResources().getColor(R.color.yellow));
            holder.tvVaccineStatus.setText(R.string.scheduled);
        }

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vaccineModels.get(position).setStatus("administered");
                updateRecords(vaccineModels.get(position));
            }
        });
        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vaccineModels.get(position).setStatus("cancelled");
                updateRecords(vaccineModels.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return vaccineModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvVaccineName, tvVaccineDate, tvVaccineWeek, tvVaccineStatus;
        Button btnAccept, btnReject;
        RecordModel vaccineModel;

        ViewHolder(View view) {
            super(view);
            tvVaccineName = view.findViewById(R.id.tvVaccineName);
            tvVaccineDate = view.findViewById(R.id.tvVaccineDate);
            tvVaccineWeek = view.findViewById(R.id.tvVaccineWeek);
            tvVaccineStatus = view.findViewById(R.id.tvVaccineStatus);
            btnAccept = view.findViewById(R.id.btnAdminister);
            btnReject = view.findViewById(R.id.btnReject);
        }
    }

    private void updateRecords(RecordModel recordModel){
        final ProgressDialog progressDialog = ProgressDialogUtil.progressDialog(context, context.getString(R.string.please_wait), false);
        progressDialog.show();
        API api = new RetrofitConfig().config();
        Call<RecordModel> call = api.updateRecords(userDetails.get("key"), recordModel.getId(),recordModel);
        call.enqueue(new Callback<RecordModel>() {
            @Override
            public void onResponse(Call<RecordModel> call, Response<RecordModel> response) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                if(response.code() == 201){
                    notifyDataSetChanged();
                }else{
                    Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RecordModel> call, Throwable t) {
                Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        });
    }

}
