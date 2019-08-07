package com.bonny.bonnyphc.ui.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bonny.bonnyphc.R;
import com.bonny.bonnyphc.adapters.PendingVaccineListAdapter;
import com.bonny.bonnyphc.api.API;
import com.bonny.bonnyphc.config.RetrofitConfig;
import com.bonny.bonnyphc.models.AppointmentModel;
import com.bonny.bonnyphc.models.FormDataHolder;
import com.bonny.bonnyphc.models.ScheduleLists;
import com.bonny.bonnyphc.session.SessionManager;
import com.bonny.bonnyphc.util.ProgressDialogUtil;
import com.bonny.bonnyphc.util.Utils;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.security.AccessController.getContext;

/**
 * @author Aditya Kulkarni
 */

@SuppressLint("ValidFragment")
public class BottomDialogFragment extends BottomSheetDialogFragment {

    String vaccines;
    SessionManager sessionManager;
    private static BottomDialogFragment bottomDialogFragment = null;

    public static BottomDialogFragment getInstance() {
        if(bottomDialogFragment == null){
            bottomDialogFragment = new BottomDialogFragment();
        }

        return bottomDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.content_bottom_sheet, container, false);
        final TextView tvNotes = view.findViewById(R.id.tvBabyNotes);
        final Button btnSchedule = view.findViewById(R.id.btnScheduleVaccine);
        RecyclerView rvPendingVaccine = view.findViewById(R.id.rvPendingVaccine);
        sessionManager = new SessionManager(getActivity());
        rvPendingVaccine.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        rvPendingVaccine.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPendingVaccine.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0 && btnSchedule.getVisibility() == View.VISIBLE){
                    btnSchedule.setVisibility(View.INVISIBLE);
                }else if(dy < 0 && btnSchedule.getVisibility() != View.VISIBLE){
                    btnSchedule.setVisibility(View.VISIBLE);
                }
            }
        });
        if(!FormDataHolder.specialNotes.equalsIgnoreCase("na")){
            tvNotes.setVisibility(View.VISIBLE);
            tvNotes.setText(FormDataHolder.specialNotes);
        }
        rvPendingVaccine.setAdapter(new PendingVaccineListAdapter(getContext(), ScheduleLists.currentWeekVaccineList));
        ScheduleLists.selectedVaccinesForScheduleList = new ArrayList<>();
        ScheduleLists.selectedVaccinesForScheduleList.clear();

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ScheduleLists.selectedVaccinesForScheduleList.size() != 0){
                    final ProgressDialog progressDialog = ProgressDialogUtil.progressDialog(getActivity(), getString(R.string.creating_appointment), false);
                    API api = new RetrofitConfig().config();
                    Call<AppointmentModel> call = api.createAppointment(new SessionManager(getActivity()).getUserDetails().get("key"), FormDataHolder.id);
                    call.enqueue(new Callback<AppointmentModel>() {
                        @Override
                        public void onResponse(Call<AppointmentModel> call, Response<AppointmentModel> response) {
                            progressDialog.dismiss();
                            if (response.code() == 201){
                                Log.e("Appointment code", response.code() + "");
                                Toast.makeText(getActivity(), "Appointment created", Toast.LENGTH_SHORT).show();
                                final ProgressDialog progressDialog1 = ProgressDialogUtil.progressDialog(getActivity(), getString(R.string.scheduling_vaccines), false);
                                progressDialog1.show();
                                API api1 = new RetrofitConfig().config();
                                StringBuffer stringBuffer = new StringBuffer();
                                for(int i = 0; i < ScheduleLists.selectedVaccinesForScheduleList.size(); i++){
                                    if(ScheduleLists.selectedVaccinesForScheduleList.size() - i == 1){
                                        stringBuffer.append(Utils.getRawVaccine(ScheduleLists.selectedVaccinesForScheduleList.get(i).getVaccine()));
                                    }else{
                                        stringBuffer.append(Utils.getRawVaccine(ScheduleLists.selectedVaccinesForScheduleList.get(i).getVaccine()) + ",");
                                    }
                                }

                                vaccines = stringBuffer.toString();
                                Call<ResponseBody> call1 = api1.scheduleVaccine(sessionManager.getUserDetails().get("key"), response.body().getId(),
                                        vaccines);

                                call1.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        progressDialog1.dismiss();
                                        Log.e("Schedule code", response.code() + "");
                                        if(response.code() == 200){
                                            Toast.makeText(getActivity(), getString(R.string.vaccines_shceduled), Toast.LENGTH_LONG).show();
                                            getInstance().dismiss();
                                        }else {
                                            Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                                            getInstance().dismiss();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        progressDialog1.dismiss();
                                        Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }else{
                                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                                getInstance().dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<AppointmentModel> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
