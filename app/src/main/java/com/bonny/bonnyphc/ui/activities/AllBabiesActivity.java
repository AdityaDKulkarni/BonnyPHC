package com.bonny.bonnyphc.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bonny.bonnyphc.R;
import com.bonny.bonnyphc.adapters.BabyRecyclerAdapter;
import com.bonny.bonnyphc.api.API;
import com.bonny.bonnyphc.config.RetrofitConfig;
import com.bonny.bonnyphc.listener.RecyclerViewListener;
import com.bonny.bonnyphc.models.BabyModel;
import com.bonny.bonnyphc.models.EmployeeModel;
import com.bonny.bonnyphc.models.ScheduleLists;
import com.bonny.bonnyphc.models.UserModel;
import com.bonny.bonnyphc.models.VaccineModel;
import com.bonny.bonnyphc.session.SessionManager;
import com.bonny.bonnyphc.util.ProgressDialogUtil;
import com.bonny.bonnyphc.util.Utils;

import java.util.ArrayList;
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
    private String username, email, key, TAG = getClass().getSimpleName();
    private SessionManager sessionManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<BabyModel> babyModels;

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

        tvName.setText(username);

        if (sessionManager.isFirstTimeLaunch()) {
            Snackbar.make(findViewById(R.id.coordinator), getString(R.string.msg_add_baby), Snackbar.LENGTH_LONG)
                    .show();
            sessionManager.setIsFirstTimeLaunch(false);
        }
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
        API api = new RetrofitConfig().config();
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
        API api = new RetrofitConfig().config();
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
        final ProgressDialog progressDialog = new ProgressDialogUtil().progressDialog(this,
                "Getting schedule...", false);
        progressDialog.show();

        API api = new RetrofitConfig().config();
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

                            if(vaccineModel.getStatus().equalsIgnoreCase("pending")
                                    && vaccineModel.getWeek() == babyModels.get(position).getWeek()){
                                ScheduleLists.currentWeekVaccineList.add(vaccineModel);
                            }
                        }
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

    @Override
    protected void onResume() {
        super.onResume();
        getAllBabies();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            API api = new RetrofitConfig().config();
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
}
