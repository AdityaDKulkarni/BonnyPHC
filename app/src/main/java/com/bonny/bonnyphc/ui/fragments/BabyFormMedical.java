package com.bonny.bonnyphc.ui.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bonny.bonnyphc.R;
import com.bonny.bonnyphc.adapters.CusotomBloodGroupAdapter;
import com.bonny.bonnyphc.adapters.CustomGenderAdapter;
import com.bonny.bonnyphc.models.FormDataHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Aditya Kulkarni
 */

public class BabyFormMedical extends Fragment implements View.OnClickListener{

    private EditText etBabyPlaceofBirth, etBabyWeight, etDateOfBirth, etTimeOfBirth;
    public Spinner spnBloodGroup, spnGender;
    private TextInputLayout tILDateOfBirth, tILTimeOfBirth;
    private int day, month, year, hour, minute;
    private String TAG = getClass().getSimpleName();

    public BabyFormMedical() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.baby_form_medical, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setStatusBarColor(R.color.colorAccent);
        tILDateOfBirth = view.findViewById(R.id.tILBabyDateOfBirth);
        tILTimeOfBirth= view.findViewById(R.id.tILBabyTimeofBirth);
        etBabyPlaceofBirth = view.findViewById(R.id.etBabyPlaceOfBirth);
        etBabyWeight = view.findViewById(R.id.etBabyWeight);
        etDateOfBirth = view.findViewById(R.id.etBabyDateOfBirth);
        etTimeOfBirth= view.findViewById(R.id.etBabyTimeofBirth);
        spnBloodGroup = view.findViewById(R.id.spnBloodGroup);
        spnGender= view.findViewById(R.id.spnGender);

        tILTimeOfBirth.setOnClickListener(this);
        etTimeOfBirth.setOnClickListener(this);
        tILDateOfBirth.setOnClickListener(this);
        etDateOfBirth.setOnClickListener(this);

        final ArrayList<String> strings = new ArrayList<>();
        strings.add("Male");
        strings.add("Female");
        Log.e(TAG, strings.toString());
        spnGender.setAdapter(new CustomGenderAdapter(getActivity(), R.layout.custom_spinner_row, strings));

        ArrayList<String> strings1 = new ArrayList<>();
        strings1.add("A positive");
        strings1.add("A negative");
        strings1.add("B positive");
        strings1.add("B negative");
        strings1.add("O positive");
        strings1.add("O negative");
        strings1.add("AB positive");
        strings1.add("AB negative");
        Log.e(TAG, strings1.toString());
        spnBloodGroup.setAdapter(new CusotomBloodGroupAdapter(getActivity(), R.layout.custom_spinner_row, strings1));

        setData();

        if(FormDataHolder.babyModel != null){
            etBabyPlaceofBirth.setText(FormDataHolder.babyModel.getPlace_of_birth());
            etBabyWeight.setText(FormDataHolder.babyModel.getWeight() + "");
            int blood_position = strings1.indexOf(FormDataHolder.babyModel.getBlood_group());
            spnBloodGroup.setSelection(blood_position);
            String[] dateTime = FormDataHolder.babyModel.getBirth_date().split(" ", 2);
            etDateOfBirth.setText(dateTime[0]);
            etTimeOfBirth.setText(dateTime[1]);
            int gender_position = strings.indexOf(FormDataHolder.babyModel.getGender());
            spnGender.setSelection(gender_position);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void setStatusBarColor(int color){

        Window window = getActivity().getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(getActivity(),color));
    }

    private void showDatePicker(){
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        c.set(year, month, day);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int dayOfMonth, int monthOfYear, int year) {
                        etDateOfBirth.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePikcer(){
        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        etTimeOfBirth.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, true);

        timePickerDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.etBabyDateOfBirth:
                showDatePicker();
                break;

            case R.id.tILBabyDateOfBirth:
                showDatePicker();
                break;

            case R.id.etBabyTimeofBirth:
                showTimePikcer();
                break;

            case R.id.tILBabyTimeofBirth:
                showTimePikcer();
                break;
        }
    }

    private void setData(){
        etBabyPlaceofBirth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(etBabyPlaceofBirth.getText().toString().isEmpty()){
                    etBabyPlaceofBirth.setError(getString(R.string.cannot_be_empty));
                }else{
                    FormDataHolder.placeOfBirth = etBabyPlaceofBirth.getText().toString();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(FormDataHolder.babyModel != null){
                    FormDataHolder.babyModel.setPlace_of_birth(etBabyPlaceofBirth.getText().toString());
                }
            }
        });

        etBabyWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(etBabyWeight.getText().toString().isEmpty()){
                    etBabyWeight.setError(getString(R.string.cannot_be_empty));
                }else{
                    FormDataHolder.weight = Integer.valueOf(etBabyWeight.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(FormDataHolder.babyModel != null){
                    FormDataHolder.babyModel.setWeight(Integer.valueOf(etBabyWeight.getText().toString()));
                }
            }
        });

        etDateOfBirth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(FormDataHolder.babyModel != null){
                    String[] dateTokens = FormDataHolder.babyModel.getBirth_date().split("-", 3);
                    String[] yearTokens = dateTokens[2].split(" ", 2);
                    String[] timeTokens = yearTokens[1].split(":", 2);
                    FormDataHolder.babyModel.setBirth_date(yearTokens[0] + "-" + dateTokens[1] + "-" + dateTokens[0] + "T" + timeTokens[0] + ":" + timeTokens[1] + ":00");
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(etDateOfBirth.getText().toString().isEmpty()){
                    etDateOfBirth.setError(getString(R.string.cannot_be_empty));
                }else{
                    FormDataHolder.dateOfBirth = etDateOfBirth.getText().toString();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etTimeOfBirth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(etTimeOfBirth.getText().toString().isEmpty()){
                    etTimeOfBirth.setError(getString(R.string.cannot_be_empty));
                }else{
                    FormDataHolder.timeOfBirth = etTimeOfBirth.getText().toString();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(FormDataHolder.babyModel != null && FormDataHolder.flag){
                    FormDataHolder.babyModel.setBirth_date(FormDataHolder.dateOfBirth + "T" + FormDataHolder.timeOfBirth + ":00");
                }
            }
        });
    }
}
