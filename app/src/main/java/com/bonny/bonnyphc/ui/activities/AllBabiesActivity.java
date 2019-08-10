package com.bonny.bonnyphc.ui.activities;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bonny.bonnyphc.R;
import com.bonny.bonnyphc.adapters.BabyRecyclerAdapter;
import com.bonny.bonnyphc.api.API;
import com.bonny.bonnyphc.comparators.WeekComparator;
import com.bonny.bonnyphc.config.RetrofitConfig;
import com.bonny.bonnyphc.listener.RecyclerViewListener;
import com.bonny.bonnyphc.models.BabyModel;
import com.bonny.bonnyphc.models.EmployeeModel;
import com.bonny.bonnyphc.models.FormDataHolder;
import com.bonny.bonnyphc.models.ScheduleLists;
import com.bonny.bonnyphc.models.UserModel;
import com.bonny.bonnyphc.models.VaccineModel;
import com.bonny.bonnyphc.nfc.NFCHandler;
import com.bonny.bonnyphc.session.SessionManager;
import com.bonny.bonnyphc.util.ProgressDialogUtil;
import com.bonny.bonnyphc.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllBabiesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private Toolbar toolbar;
    private View navHeaderView;
    private TextView tvName, tvEmail;
    private RecyclerView rvBabies;
    private HashMap<String, String> userDetails;
    private String username, email, key, TAG = getClass().getSimpleName(), tagId;
    private SessionManager sessionManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<BabyModel> babyModels;
    private NFCHandler nfcHandler;
    private Button btnScanNfc;
    private boolean scanningNFC = false;
    private API api;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.all_babies));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllBabiesActivity.this, BabyFormActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.setScrimColor(getResources().getColor(android.R.color.transparent));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        api = new RetrofitConfig().config();
        progressDialog = ProgressDialogUtil.progressDialog(this, getString(R.string.please_wait), false);

        intitUi();
        getEmployee();
        swipe();

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getAllBabies();
            }
        });

    }

    private void intitUi() {
        navHeaderView = navigationView.getHeaderView(0);
        tvName = navHeaderView.findViewById(R.id.tvHeaderName);
        tvEmail = navHeaderView.findViewById(R.id.tvHeaderEmail);
        rvBabies = findViewById(R.id.rvBabies);
        rvBabies.setLayoutManager(new LinearLayoutManager(this));
        sessionManager = new SessionManager(this);
        userDetails = sessionManager.getUserDetails();
        username = userDetails.get("username");
        key = userDetails.get("key");
        btnScanNfc = findViewById(R.id.btn_scan_tag);
        nfcHandler = NFCHandler.getInstance(NfcAdapter.getDefaultAdapter(AllBabiesActivity.this), this);

        tvName.setText(username);

        if (sessionManager.isFirstTimeLaunch()) {
            Snackbar.make(findViewById(R.id.coordinator), getString(R.string.msg_add_baby), Snackbar.LENGTH_LONG)
                    .show();
            sessionManager.setIsFirstTimeLaunch(false);
        }

        btnScanNfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nfcHandler.isNfcEnabled()) {
                    scanningNFC = true;
                    Snackbar.make(findViewById(R.id.coordinator), getString(R.string.scanning_for_a_card), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getString(R.string.cancel), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    scanningNFC = false;
                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.white))
                            .show();
                } else {
                    scanningNFC = false;
                    Snackbar.make(findViewById(R.id.coordinator), getString(R.string.device_is_not_nfc_supported), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void swipe() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllBabies();
            }
        });
    }

    private synchronized void getAllBabies() {
        swipeRefreshLayout.setRefreshing(true);
        Call<List<BabyModel>> call = api.getAllBabies(key);
        call.enqueue(new Callback<List<BabyModel>>() {
            @Override
            public void onResponse(Call<List<BabyModel>> call, Response<List<BabyModel>> response) {
                switch (response.code()) {
                    case 200:
                        babyModels = new ArrayList<>();
                        for (int i = 0; i < response.body().size(); i++) {
                            BabyModel babyModel = response.body().get(i);
                            babyModel.setBlood_group(Utils.getFormattedBloodGroup(babyModel.getBlood_group()));
                            babyModels.add(babyModel);
                        }
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        rvBabies.addOnItemTouchListener(new RecyclerViewListener(AllBabiesActivity.this,
                                rvBabies, new RecyclerViewListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                FormDataHolder.selectedBabyId = babyModels.get(position).getId();
                                FormDataHolder.selectedBabyModel = babyModels.get(position);
                                getSchedule(babyModels.get(position).getId(), position);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                        }));
                        rvBabies.setAdapter(new BabyRecyclerAdapter(AllBabiesActivity.this, babyModels));
                        break;
                    case 500:
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(AllBabiesActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<BabyModel>> call, Throwable t) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                Log.d(TAG, getString(R.string.something_went_wrong));


            }
        });
    }

    private synchronized void getEmployee() {
        Call<List<EmployeeModel>> call = api.getEmployee(key);
        call.enqueue(new Callback<List<EmployeeModel>>() {
            @Override
            public void onResponse(Call<List<EmployeeModel>> call, Response<List<EmployeeModel>> response) {
                try {
                    for (int i = 0; i < response.body().size(); i++) {
                        EmployeeModel model = new EmployeeModel();
                        model.setUser(response.body().get(i).getUser());
                        model.setFirst_name(response.body().get(i).getFirst_name());
                        model.setLast_name(response.body().get(i).getLast_name());
                        model.setEmail(response.body().get(i).getEmail());
                        model.setContact(response.body().get(i).getContact());
                        model.setHealthCare(response.body().get(i).getHealthCare());
                        email = response.body().get(i).getEmail();
                        tvEmail.setText(email);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<EmployeeModel>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(AllBabiesActivity.this, getString(R.string.session_expired), Toast.LENGTH_LONG).show();
                sessionManager.logOutUser();
            }
        });
    }

    private void getSchedule(int pk, final int position) {
        final ArrayList<VaccineModel> vaccineModels = new ArrayList<>();
        progressDialog.show();

        Call<List<VaccineModel>> call = api.getSchedule(new SessionManager(this).getUserDetails().get("key"), pk);
        call.enqueue(new Callback<List<VaccineModel>>() {
            @Override
            public void onResponse(Call<List<VaccineModel>> call, Response<List<VaccineModel>> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
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

                            if (vaccineModel.getStatus().equalsIgnoreCase("pending")
                                    && vaccineModel.getWeek() == babyModels.get(position).getWeek()) {
                                ScheduleLists.currentWeekVaccineList.add(vaccineModel);
                            }
                        }
                        Collections.sort(vaccineModels, new WeekComparator());
                        ScheduleLists.fullScheduleList = vaccineModels;
                        Intent intent = new Intent(AllBabiesActivity.this, BabyDetailsActivity.class);
                        intent.putExtra("babyModel", babyModels.get(position));
                        startActivity(intent);
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<VaccineModel>> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            api = new RetrofitConfig().config();
            Call<UserModel> call = api.logout();

            call.enqueue(new Callback<UserModel>() {
                @Override
                public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                    if (response.body().getDetail().equalsIgnoreCase("Successfully logged out.")) {
                        new SessionManager(AllBabiesActivity.this).logOutUser();
                    } else {
                        Toast.makeText(AllBabiesActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<UserModel> call, Throwable t) {
                    Toast.makeText(AllBabiesActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                }
            });

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {

        if (intent.hasExtra(NfcAdapter.EXTRA_TAG) && scanningNFC) {
            try {
                scanningNFC = false;
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                tagId = nfcHandler.getNfcId(tag.getId());
                Log.e(TAG, "Tag " + tagId + " scanned");

                Call<List<BabyModel>> call = api.getBaby(
                        key,
                        tagId
                );
                progressDialog.show();
                call.enqueue(new Callback<List<BabyModel>>() {
                    @Override
                    public void onResponse(Call<List<BabyModel>> call, Response<List<BabyModel>> response) {
                        progressDialog.setMessage(getString(R.string.redirecting));
                        BabyModel babyModel1 = null;
                        int i = 0;
                        for(i = 0; i < response.body().size(); i++){
                            if(response.body().get(i).getTag().equalsIgnoreCase(tagId)){
                                babyModel1 = response.body().get(i);
                                break;
                            }
                        }

                        FormDataHolder.selectedBabyId = babyModel1.getId();
                        FormDataHolder.selectedBabyModel = babyModel1;
                        final ArrayList<VaccineModel> vaccineModels = new ArrayList<>();
                        Call<List<VaccineModel>> call1 = api.getSchedule(new SessionManager(AllBabiesActivity.this).getUserDetails().get("key"), babyModel1.getId());
                        final BabyModel finalBabyModel = babyModel1;
                        call1.enqueue(new Callback<List<VaccineModel>>() {
                            @Override
                            public void onResponse(Call<List<VaccineModel>> call, Response<List<VaccineModel>> response) {
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

                                            if (vaccineModel.getStatus().equalsIgnoreCase("pending")
                                                    && vaccineModel.getWeek() == finalBabyModel.getWeek()) {
                                                ScheduleLists.currentWeekVaccineList.add(vaccineModel);
                                            }
                                        }
                                        Collections.sort(vaccineModels, new WeekComparator());
                                        ScheduleLists.fullScheduleList = vaccineModels;
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                        Intent intent = new Intent(AllBabiesActivity.this, BabyDetailsActivity.class);
                                        intent.putExtra("babyModel", finalBabyModel);
                                        startActivity(intent);
                                        break;
                                }
                            }

                            @Override
                            public void onFailure(Call<List<VaccineModel>> call, Throwable t) {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<List<BabyModel>> call, Throwable t) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Snackbar.make(findViewById(R.id.coordinator), getString(R.string.something_went_wrong), Snackbar.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter tagFilter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter techFilter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter ndefFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        IntentFilter[] filters = new IntentFilter[]{tagFilter, techFilter, ndefFilter};
        Intent intent = new Intent(this, AllBabiesActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        if (nfcHandler.getNfcAdapter() != null) {
            nfcHandler.getNfcAdapter().enableForegroundDispatch(this, pendingIntent, filters, null);
        }

        //getAllBabies();
        scanningNFC = false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finishAffinity();
        }
    }
}
