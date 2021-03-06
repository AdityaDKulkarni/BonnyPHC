package com.bonny.bonnyphc.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.bonny.bonnyphc.R;
import com.bonny.bonnyphc.models.FormDataHolder;
import com.bonny.bonnyphc.util.Utils;

/**
 * @author Aditya Kulkarni
 */

public class BabyFormAdministration extends Fragment{

    private final String TAG = getClass().getSimpleName();
    private String tagId = "No tag";
    private boolean flag = false;
    public EditText etBabyTag, etBabySpecialNotes;

    public BabyFormAdministration() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.baby_form_administration, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setStatusBarColor(R.color.colorAccent);
        etBabyTag = view.findViewById(R.id.etBabyTag);
        etBabySpecialNotes = view.findViewById(R.id.etBabySpecialNotes);

        etBabyTag.setText(getTagId());
        setData();

        if(FormDataHolder.babyModel != null){
            FormDataHolder.flag = true;
            etBabyTag.setText(FormDataHolder.babyModel.getTag());
            etBabySpecialNotes.setText(FormDataHolder.babyModel.getSpecial_notes());
            FormDataHolder.babyModel.setText_notifications(false);
            FormDataHolder.babyModel.setBlood_group(Utils.getRawBloodGroup(FormDataHolder.babyModel.getBlood_group()));
            FormDataHolder.babyModel.setGender(FormDataHolder.babyModel.getGender().toLowerCase());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void setStatusBarColor(int color){

        Window window = getActivity().getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(getActivity(),color));
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    private void setData(){
        etBabyTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(etBabyTag.getText().toString().isEmpty()){
                    etBabyTag.setError(getString(R.string.cannot_be_empty));
                }else{
                    FormDataHolder.tag = etBabyTag.getText().toString();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(FormDataHolder.babyModel != null){
                    FormDataHolder.babyModel.setTag(etBabyTag.getText().toString());
                }
            }
        });

        etBabySpecialNotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(etBabySpecialNotes.getText().toString().isEmpty()){
                    etBabySpecialNotes.setError(getString(R.string.cannot_be_empty));
                }else{
                    FormDataHolder.specialNotes = etBabySpecialNotes.getText().toString();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(FormDataHolder.babyModel != null){
                    FormDataHolder.babyModel.setSpecial_notes(etBabySpecialNotes.getText().toString());
                }
            }
        });
    }
}
