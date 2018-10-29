package com.bonny.bonnyphc.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bonny.bonnyphc.R;
import com.bonny.bonnyphc.api.API;
import com.bonny.bonnyphc.config.RetrofitConfig;
import com.bonny.bonnyphc.models.TokenModel;
import com.bonny.bonnyphc.models.UserModel;
import com.bonny.bonnyphc.session.SessionManager;
import com.bonny.bonnyphc.util.ProgressDialogUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Aditya Kulkarni
 */
public class LoginActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private AutoCompleteTextView aCUsername;
    private EditText etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUi();
        login();
    }

    private void initUi() {
        getSupportActionBar().setTitle(R.string.login);
        aCUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void login() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (aCUsername.getText().toString().isEmpty()) {
                    aCUsername.setError(getString(R.string.error_invalid_username));
                } else if (!isPasswordValid(etPassword.getText().toString())) {
                    etPassword.setError(getString(R.string.error_invalid_password));
                } else {
                    final ProgressDialog progressDialog = ProgressDialogUtil.progressDialog(LoginActivity.this,
                            getString(R.string.veryfying), false);
                    progressDialog.show();
                    API api = new RetrofitConfig().config();
                    Call<TokenModel> call = api.getToken(aCUsername.getText().toString(),
                            etPassword.getText().toString());

                    call.enqueue(new Callback<TokenModel>() {
                        @Override
                        public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                            switch (response.code()) {
                                case 200:
                                    getUser("Token " + response.body().getKey(), progressDialog, view);
                                    break;
                                case 400:
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    Snackbar.make(view, getString(R.string.invalid_credentials), Snackbar.LENGTH_LONG).show();
                                    break;
                                default:
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    Snackbar.make(view, getString(R.string.something_went_wrong), Snackbar.LENGTH_LONG).show();
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(Call<TokenModel> call, Throwable t) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Snackbar.make(view, getString(R.string.something_went_wrong), Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void getUser(final String key, final ProgressDialog progressDialog, final View view) {
        API api = new RetrofitConfig().config();
        Call<UserModel> call = api.getUser(key);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                switch (response.code()){
                    case 200:
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        int pk = response.body().getPk();
                        String username = response.body().getUsername();
                        String email = response.body().getEmail();
                        String first_name = response.body().getFirst_name();
                        String last_name = response.body().getLast_name();

                        Intent intent = new Intent(LoginActivity.this, AllBabiesActivity.class);
                        intent.putExtra("pk", String.valueOf(pk));
                        intent.putExtra("username", username);
                        intent.putExtra("email", email);

                        new SessionManager(LoginActivity.this).createLoginSession(username, key);
                        startActivity(intent);
                        break;
                    default:
                        Snackbar.make(view, getString(R.string.unable_to_fetch_profile), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Snackbar.make(view, getString(R.string.failure), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private boolean isEmailValid(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}

