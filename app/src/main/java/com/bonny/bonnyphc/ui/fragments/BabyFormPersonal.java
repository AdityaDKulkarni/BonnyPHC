package com.bonny.bonnyphc.ui.fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
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
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bonny.bonnyphc.R;
import com.bonny.bonnyphc.adapters.ParentAutoCompleteAdapter;
import com.bonny.bonnyphc.api.API;
import com.bonny.bonnyphc.config.RetrofitConfig;
import com.bonny.bonnyphc.models.FormDataHolder;
import com.bonny.bonnyphc.models.ParentModel;
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

public class BabyFormPersonal extends Fragment{

    private EditText etFirstName, etLastName;
    private AutoCompleteTextView acParentName;
    private ArrayList<ParentModel> parentModels;
    public ParentAutoCompleteAdapter parentAutoCompleteAdapter;
    private String TAG = getClass().getSimpleName();

    public BabyFormPersonal() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.baby_form_personal, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setStatusBarColor(R.color.colorAccent);
        parentModels = new ArrayList<>();
        etFirstName = view.findViewById(R.id.etBabyFirstName);
        etLastName = view.findViewById(R.id.etBabyLastName);
        acParentName = view.findViewById(R.id.acParentName);

        getParents(view);
        setData();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void setStatusBarColor(int color){

        Window window = getActivity().getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(getActivity(),color));
    }

    private void getParents(final View view){
        final ProgressDialog progressDialog = ProgressDialogUtil.progressDialog(getActivity(),
                getString(R.string.updating_parent_list), false);
        progressDialog.show();
        SessionManager sessionManager = new SessionManager(getActivity());
        API api = new RetrofitConfig().config();
        Call<List<ParentModel>> call = api.getParents(sessionManager.getUserDetails().get("key"));
        call.enqueue(new Callback<List<ParentModel>>() {
            @Override
            public void onResponse(Call<List<ParentModel>> call, Response<List<ParentModel>> response) {
                parentModels.clear();
                ParentModel parentModel1 = new ParentModel();
                parentModel1.setFirst_name("Select");
                parentModel1.setLast_name("parent");
                parentModels.add(parentModel1);
                for(int i = 0; i <response.body().size(); i++ ){
                    ParentModel parentModel = new ParentModel();
                    parentModel.setId(response.body().get(i).getId());
                    parentModel.setUser(response.body().get(i).getUser());
                    parentModel.setFirst_name(response.body().get(i).getFirst_name());
                    parentModel.setLast_name(response.body().get(i).getLast_name());
                    parentModel.setContact(response.body().get(i).getContact());
                    parentModel.setEmail(response.body().get(i).getEmail());
                    parentModel.setAddress(response.body().get(i).getAddress());
                    parentModel.setUnique_id(response.body().get(i).getUnique_id());
                    parentModels.add(parentModel);
                }
                parentAutoCompleteAdapter = new ParentAutoCompleteAdapter(getActivity(), R.layout.custom_spinner_row, parentModels);
                acParentName.setAdapter(parentAutoCompleteAdapter);
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }

                if(FormDataHolder.babyModel != null){
                    acParentName.setClickable(false);
                    etFirstName.setText(FormDataHolder.babyModel.getFirst_name());
                    etLastName.setText(FormDataHolder.babyModel.getLast_name());

                    if(parentModels != null){
                        int parentId = FormDataHolder.babyModel.getParent();
                        for(int i = 0; i < parentModels.size(); i++){
                            if(parentId == parentModels.get(i).getId()){
                                acParentName.setText(parentModels.get(i).getFirst_name()
                                        + parentModels.get(i).getLast_name()
                                        + " (" + parentModels.get(i).getContact() + ")");
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ParentModel>> call, Throwable t) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                Snackbar.make(view, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setData(){
        etFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(etFirstName.getText().toString().isEmpty()){
                    etFirstName.setError(getString(R.string.cannot_be_empty));
                }else{
                    FormDataHolder.firstName = etFirstName.getText().toString();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(FormDataHolder.babyModel != null){
                    FormDataHolder.babyModel.setFirst_name(etFirstName.getText().toString());
                }
            }
        });

        etLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(etLastName.getText().toString().isEmpty()){
                    etLastName.setError(getString(R.string.cannot_be_empty));
                }else{
                    FormDataHolder.lastName = etLastName.getText().toString();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(FormDataHolder.babyModel != null){
                    FormDataHolder.babyModel.setFirst_name(etLastName.getText().toString());
                }
            }
        });
    }
}
