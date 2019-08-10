package com.bonny.bonnyphc.ui.activities;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bonny.bonnyphc.R;
import com.bonny.bonnyphc.api.API;
import com.bonny.bonnyphc.config.RetrofitConfig;
import com.bonny.bonnyphc.models.BabyModel;
import com.bonny.bonnyphc.models.FormDataHolder;
import com.bonny.bonnyphc.models.ParentModel;
import com.bonny.bonnyphc.nfc.NFCHandler;
import com.bonny.bonnyphc.session.SessionManager;
import com.bonny.bonnyphc.ui.fragments.BabyFormAdministration;
import com.bonny.bonnyphc.ui.fragments.BabyFormMedical;
import com.bonny.bonnyphc.ui.fragments.BabyFormPersonal;
import com.bonny.bonnyphc.util.ProgressDialogUtil;

import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BabyFormActivity extends AppCompatActivity implements View.OnClickListener {

    ViewPager viewPager;
    LinearLayout linearLayout;
    Button btnCancel, btnNext;
    TextView[] tvDots;
    SessionManager sessionManager;
    IntroAdapter introAdapter;
    int NUMBER_OF_SLIDES = 3;
    BabyFormAdministration babyFormAdministration;
    BabyFormMedical babyFormMedical;
    private String TAG = getClass().getSimpleName(), tagId, token;
    private NFCHandler nfcHandler;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_form);
        getSupportActionBar().setTitle(getString(R.string.add_baby));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        sessionManager = new SessionManager(this);
        token = sessionManager.getUserDetails().get("key");
        viewPager = findViewById(R.id.introViewPager);
        linearLayout = findViewById(R.id.linearLayoutDots);
        btnCancel = findViewById(R.id.btnCancel);
        btnNext = findViewById(R.id.btnNext);

        addBottomDots(0);

        introAdapter = new IntroAdapter(getSupportFragmentManager());
        viewPager.setAdapter(introAdapter);

        viewPagerListeners();
        btnCancel.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        nfcHandler = NFCHandler.getInstance(NfcAdapter.getDefaultAdapter(BabyFormActivity.this), this);

        if (getIntent().hasExtra("babyModel")) {
            getSupportActionBar().setTitle(getString(R.string.update_details));
            FormDataHolder.babyModel = (BabyModel) getIntent().getExtras().get("babyModel");
        } else {
            FormDataHolder.babyModel = null;
            FormDataHolder.flag = false;
        }

    }

    private void viewPagerListeners() {
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                viewPager.getParent().requestDisallowInterceptTouchEvent(true);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);

                // changing the next button text 'NEXT' / 'Submit'
                if (position == NUMBER_OF_SLIDES - 1) {
                    // last page. make button text to Submit
                    btnNext.setText(getString(R.string.submit));
                } else {
                    btnNext.setText(getString(R.string.next));
                    btnCancel.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public class IntroAdapter extends FragmentStatePagerAdapter {

        public IntroAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new BabyFormPersonal();
                case 1:
                    babyFormMedical = new BabyFormMedical();
                    return babyFormMedical;
                case 2:
                    try {
                        babyFormAdministration = new BabyFormAdministration();
                        babyFormAdministration.setTagId(tagId);
                        return babyFormAdministration;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return babyFormAdministration;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUMBER_OF_SLIDES;
        }
    }

    @Override
    public void onClick(final View view) {
        if (view == btnCancel) {
            finish();
        } else if (view == btnNext) {
            int current = getItem(+1);
            if (current < NUMBER_OF_SLIDES) {
                viewPager.setCurrentItem(current);
            } else {

                final ProgressDialog progressDialog = ProgressDialogUtil.progressDialog(BabyFormActivity.this,
                        getString(R.string.please_wait), false);
                progressDialog.show();
                API api = new RetrofitConfig().config();

                if (!FormDataHolder.flag) {
                    if (FormDataHolder.parent == -1 ||
                            FormDataHolder.firstName == null ||
                            FormDataHolder.lastName == null ||
                            FormDataHolder.placeOfBirth == null ||
                            FormDataHolder.weight == -1 ||
                            FormDataHolder.bloodGroup == null ||
                            FormDataHolder.dateOfBirth == null ||
                            FormDataHolder.timeOfBirth == null ||
                            FormDataHolder.gender == null ||
                            FormDataHolder.tag == null ||
                            FormDataHolder.specialNotes == null) {
                        Snackbar.make(view, getString(R.string.all_fields_required), Snackbar.LENGTH_LONG).show();
                    }else {
                        Call<ResponseBody> call = api.postBabyDetails(token,
                                FormDataHolder.parent,
                                FormDataHolder.firstName,
                                FormDataHolder.lastName,
                                FormDataHolder.placeOfBirth,
                                FormDataHolder.weight,
                                FormDataHolder.bloodGroup,
                                FormDataHolder.dateOfBirth + "T" + FormDataHolder.timeOfBirth + ":00" + "+00:00",
                                FormDataHolder.gender.toLowerCase(),
                                FormDataHolder.tag,
                                FormDataHolder.specialNotes,
                                false);

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                switch (response.code()) {
                                    case 201:
                                        Snackbar.make(view, getString(R.string.success), Snackbar.LENGTH_LONG).show();
                                        finish();
                                        break;
                                    default:
                                        Snackbar.make(view, getString(R.string.something_went_wrong), Snackbar.LENGTH_LONG).show();
                                        break;
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                Snackbar.make(view, getString(R.string.failure), Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    Call<ResponseBody> call = api.updateBabies(token, FormDataHolder.babyModel.getId(), FormDataHolder.babyModel);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            switch (response.code()) {
                                case 200:
                                    Snackbar.make(view, getString(R.string.success), Snackbar.LENGTH_LONG).show();
                                    finish();
                                    break;
                                default:
                                    Snackbar.make(view, getString(R.string.something_went_wrong), Snackbar.LENGTH_LONG).show();
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Snackbar.make(view, getString(R.string.failure), Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void addBottomDots(int currentPage) {
        tvDots = new TextView[NUMBER_OF_SLIDES];

        linearLayout.removeAllViews();
        for (int i = 0; i < tvDots.length; i++) {
            tvDots[i] = new TextView(this);
            tvDots[i].setText(Html.fromHtml("&#8226;"));
            tvDots[i].setTextSize(35);
            tvDots[i].setTextColor(getColor(R.color.dot_dark_screen3));
            linearLayout.addView(tvDots[i]);
        }

        if (tvDots.length > 0) {
            tvDots[currentPage].setTextColor(getColor(R.color.dot_light_screen3));
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, intent.getAction());

        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            try {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                tagId = NFCHandler.getNfcId(tag.getId());
                if (babyFormAdministration != null) {
                    babyFormAdministration.etBabyTag.setText(tagId);
                }
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
        Intent intent = new Intent(this, BabyFormActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        if (nfcHandler.getNfcAdapter() != null) {
            nfcHandler.getNfcAdapter().enableForegroundDispatch(this, pendingIntent, filters, null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
