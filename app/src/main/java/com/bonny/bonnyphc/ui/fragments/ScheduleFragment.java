package com.bonny.bonnyphc.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bonny.bonnyphc.R;
import com.bonny.bonnyphc.adapters.ScheduleRecyclerViewAdapter;
import com.bonny.bonnyphc.api.API;
import com.bonny.bonnyphc.comparators.WeekComparator;
import com.bonny.bonnyphc.config.RetrofitConfig;
import com.bonny.bonnyphc.models.BabyModel;
import com.bonny.bonnyphc.models.FormDataHolder;
import com.bonny.bonnyphc.models.ScheduleLists;
import com.bonny.bonnyphc.models.VaccineModel;
import com.bonny.bonnyphc.session.SessionManager;
import com.bonny.bonnyphc.util.ProgressDialogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Aditya Kulkarni
 */
public class ScheduleFragment extends Fragment {

    private RecyclerView recyclerView;
    private String TAG = getClass().getSimpleName();
    private LinearLayout llBottomSheet;
    private FloatingActionButton fabVaccine;
    private SwipeRefreshLayout swipeRefreshLayout;

    public ScheduleFragment() {
    }

    public static ScheduleFragment newInstance(int someInt) {
        ScheduleFragment myFragment = new ScheduleFragment();

        Bundle args = new Bundle();
        args.putInt("babyId", someInt);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container,savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_schedule_list, container, false);

        recyclerView = view.findViewById(R.id.rvSchedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fabVaccine = getActivity().findViewById(R.id.fabVaccine);
        swipeRefreshLayout = view.findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        final BottomDialogFragment dialogFragment = BottomDialogFragment.getInstance();
        fabVaccine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FormDataHolder.id = getArguments().getInt("babyId");
                dialogFragment.show(getFragmentManager(), "Bottom sheet");
            }
        });
        swipe();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0 && fabVaccine.getVisibility() == View.VISIBLE){
                    fabVaccine.hide();
                }else if(dy < 0 && fabVaccine.getVisibility() != View.VISIBLE){
                    fabVaccine.show();
                }
            }
        });
        ScheduleRecyclerViewAdapter scheduleRecyclerViewAdapter = new ScheduleRecyclerViewAdapter(getContext(),
                ScheduleLists.fullScheduleList);

        recyclerView.setAdapter(scheduleRecyclerViewAdapter);
        return view;
    }

    private void swipe() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSchedule(FormDataHolder.selectedBabyId);
            }
        });
    }

    private void getSchedule(int pk) {
        final ArrayList<BabyModel> babyModels = new ArrayList<>();
        final ArrayList<VaccineModel> vaccineModels = new ArrayList<>();
        final ProgressDialog progressDialog = new ProgressDialogUtil().progressDialog(getActivity(),
                "Getting schedule...", false);
        progressDialog.show();

        API api = new RetrofitConfig().config();
        Call<List<VaccineModel>> call = api.getSchedule(new SessionManager(getActivity()).getUserDetails().get("key"), pk);
        call.enqueue(new Callback<List<VaccineModel>>() {
            @Override
            public void onResponse(Call<List<VaccineModel>> call, Response<List<VaccineModel>> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
                switch (response.code()) {
                    case 200:
                        ScheduleLists.currentWeekVaccineList = new ArrayList<>();
                        ScheduleLists.fullScheduleList = new ArrayList<>();
                        for (int i = 0; i < response.body().size(); i++) {
                            VaccineModel vaccineModel = new VaccineModel();
                            vaccineModel.setBaby(response.body().get(i).getBaby());
                            vaccineModel.setVaccine(response.body().get(i).getVaccine());
                            vaccineModel.setWeek(response.body().get(i).getWeek());
                            vaccineModel.setTentative_date(response.body().get(i).getTentative_date());
                            vaccineModel.setStatus(response.body().get(i).getStatus());
                            vaccineModels.add(vaccineModel);

                            if(vaccineModel.getStatus().equalsIgnoreCase("pending")
                                    && vaccineModel.getWeek() == FormDataHolder.selectedBabyModel.getWeek()){
                                ScheduleLists.currentWeekVaccineList.add(vaccineModel);
                            }
                        }
                        Collections.sort(vaccineModels, new WeekComparator());
                        ScheduleLists.fullScheduleList = vaccineModels;
                        ScheduleRecyclerViewAdapter scheduleRecyclerViewAdapter = new ScheduleRecyclerViewAdapter(getContext(),
                                ScheduleLists.fullScheduleList);

                        recyclerView.setAdapter(scheduleRecyclerViewAdapter);
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<VaccineModel>> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }
}
