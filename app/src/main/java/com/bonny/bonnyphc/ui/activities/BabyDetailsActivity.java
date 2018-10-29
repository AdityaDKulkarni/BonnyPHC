package com.bonny.bonnyphc.ui.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.bonny.bonnyphc.R;
import com.bonny.bonnyphc.adapters.PagerAdapter;
import com.bonny.bonnyphc.models.BabyModel;
import com.bonny.bonnyphc.ui.fragments.AppointmentHistoryFragment;
import com.bonny.bonnyphc.ui.fragments.ScheduleFragment;

public class BabyDetailsActivity extends AppCompatActivity {

    private String TAG = getClass().getSimpleName();
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_baby_details));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initUi();

        tabLayout = findViewById(R.id.tlToolbarTabs);
        viewPager = findViewById(R.id.vPSchedule);
        pagerAdapter = new PagerAdapter(BabyDetailsActivity.this.getSupportFragmentManager());
        pagerAdapter.addFragments(new ScheduleFragment(), getString(R.string.schedule));
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

    private void initUi(){
        viewPager = findViewById(R.id.vPSchedule);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
