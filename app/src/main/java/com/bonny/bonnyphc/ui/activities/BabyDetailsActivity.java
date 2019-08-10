package com.bonny.bonnyphc.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bonny.bonnyphc.R;
import com.bonny.bonnyphc.adapters.PagerAdapter;
import com.bonny.bonnyphc.models.BabyModel;
import com.bonny.bonnyphc.models.FormDataHolder;
import com.bonny.bonnyphc.ui.fragments.AppointmentHistoryFragment;
import com.bonny.bonnyphc.ui.fragments.ScheduleFragment;
import com.bonny.bonnyphc.util.Utils;

public class BabyDetailsActivity extends AppCompatActivity {

    private String TAG = getClass().getSimpleName();
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    private BabyModel babyModel;
    private TextView tvDetailsBabyName, tvDetailsBabyBloodGroup, tvDetailsBabyWeight;
    private Button btnEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_baby_details));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initUi();

        if (getIntent().hasExtra("babyModel")) {
            babyModel = (BabyModel) getIntent().getExtras().get("babyModel");
            tvDetailsBabyName.setText("Name: " + babyModel.getFirst_name());
            tvDetailsBabyBloodGroup.setText("Blood Group: " + Utils.getFormattedBloodGroup(babyModel.getBlood_group()));
            tvDetailsBabyWeight.setText("Weight: " + String.valueOf(babyModel.getWeight()) + " Kg");
            FormDataHolder.specialNotes = babyModel.getSpecial_notes();
        }

        tabLayout = findViewById(R.id.tlToolbarTabs);
        viewPager = findViewById(R.id.vPSchedule);
        viewPager.setId(R.id.vPSchedule);
        pagerAdapter = new PagerAdapter(BabyDetailsActivity.this.getSupportFragmentManager());
        pagerAdapter.addFragments(ScheduleFragment.newInstance(babyModel.getId()), getString(R.string.schedule));
        pagerAdapter.addFragments(new AppointmentHistoryFragment(), getString(R.string.history));
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initUi() {
        viewPager = findViewById(R.id.vPSchedule);
        tvDetailsBabyName = findViewById(R.id.tvDetailsBabyName);
        tvDetailsBabyBloodGroup = findViewById(R.id.tvDetailsBabyBloodGroup);
        tvDetailsBabyWeight = findViewById(R.id.tvDetailsBabyWeight);
        btnEdit = findViewById(R.id.btnEdit);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BabyDetailsActivity.this, BabyFormActivity.class);
                intent.putExtra("babyModel", babyModel);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
