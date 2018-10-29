package com.bonny.bonnyphc.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bonny.bonnyphc.R;
import com.bonny.bonnyphc.adapters.PendingVaccineListAdapter;
import com.bonny.bonnyphc.models.ScheduleLists;
import com.bonny.bonnyphc.models.VaccineModel;

import java.util.ArrayList;

/**
 * @author Aditya Kulkarni
 */

@SuppressLint("ValidFragment")
public class BottomDialogFragment extends BottomSheetDialogFragment {

    public static BottomDialogFragment getInstance() {
        return new BottomDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.content_bottom_sheet, container, false);
        final Button btnSchedule = view.findViewById(R.id.btnScheduleVaccine);
        RecyclerView rvPendingVaccine = view.findViewById(R.id.rvPendingVaccine);
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
        rvPendingVaccine.setAdapter(new PendingVaccineListAdapter(getContext(), ScheduleLists.pendingScheduleList));
        ScheduleLists.selectedVaccinesForScheduleList = new ArrayList<>();
        ScheduleLists.selectedVaccinesForScheduleList.clear();

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ScheduleLists.selectedVaccinesForScheduleList.size() != 0){
                    Toast.makeText(getContext(), String.valueOf(ScheduleLists.selectedVaccinesForScheduleList.size())
                            + " " + getString(R.string.vaccines_added_for_shcedule), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}
