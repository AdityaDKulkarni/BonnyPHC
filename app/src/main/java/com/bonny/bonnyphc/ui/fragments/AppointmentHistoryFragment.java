package com.bonny.bonnyphc.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bonny.bonnyphc.R;
import com.bonny.bonnyphc.api.API;
import com.bonny.bonnyphc.config.RetrofitConfig;
import com.bonny.bonnyphc.models.AppointmentModel;
import com.bonny.bonnyphc.models.ScheduleLists;
import com.bonny.bonnyphc.session.SessionManager;
import com.bonny.bonnyphc.util.ProgressDialogUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Aditya Kulkarni
 */
public class AppointmentHistoryFragment extends Fragment {

    private View v;
    private ArrayList<AppointmentModel> appointmentModels;
    private ListView listView;
    private List<String> strings;
    private ProgressDialog progressDialog;
    private String TAG = getClass().getSimpleName();

    public AppointmentHistoryFragment() {
        appointmentModels = new ArrayList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_appointment_history, container, false);
        listView = v.findViewById(R.id.lvAppointment);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getHistory();
    }

    synchronized private void getHistory() {
        progressDialog = ProgressDialogUtil.progressDialog(getContext(), getString(R.string.getting_history), false);
        progressDialog.show();
        strings = new ArrayList();
        API api = new RetrofitConfig().config();
        Call<List<AppointmentModel>> call = api.getAppointments(new SessionManager(getContext()).getUserDetails().get("key"), ScheduleLists.fullScheduleList.get(0).getBaby());
        call.enqueue(new Callback<List<AppointmentModel>>() {
            @Override
            public void onResponse(Call<List<AppointmentModel>> call, Response<List<AppointmentModel>> response) {
                for (int i = 0; i < response.body().size(); i++) {
                    if (response.body().get(i).getBaby() == ScheduleLists.fullScheduleList.get(i).getBaby()) {
                        AppointmentModel model = new AppointmentModel();
                        model.setId(response.body().get(i).getId());
                        model.setBaby(response.body().get(i).getBaby());
                        model.setAdministered_at(response.body().get(i).getAdministered_at());
                        model.setAdministered_on(response.body().get(i).getAdministered_on());
                        appointmentModels.add(model);
                        strings.add(model.getAdministered_at().getName() + " - " + model.getAdministered_on());
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, strings);
                listView.setAdapter(adapter);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<AppointmentModel>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        });
    }

}