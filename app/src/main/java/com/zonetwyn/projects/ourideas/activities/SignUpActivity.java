package com.zonetwyn.projects.ourideas.activities;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zonetwyn.projects.ourideas.R;
import com.zonetwyn.projects.ourideas.database.SessionManager;
import com.zonetwyn.projects.ourideas.payloads.ApiResponse;
import com.zonetwyn.projects.ourideas.payloads.SignInResponse;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUpActivity extends AppCompatActivity {

    private SignUpViewModel viewModel;

    private TextView error;
    private EditText username;
    private EditText password;
    private EditText confirmPassword;
    private Button signUp;

    private ProgressDialog progressDialog;

    private String currentUsername;

    private SessionManager sessionManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/poppins/Poppins-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_sign_up);

        // session manager
        sessionManager = SessionManager.getInstance(getApplicationContext());

        // init viewModel
        viewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);

        // init views
        error = findViewById(R.id.error);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        signUp = findViewById(R.id.signUp);

        // initializations
        initSignUp();
        initProgressDialog();

        // view models binding
        viewModel.getLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != null) {
                    if (aBoolean) {
                        progressDialog.show();
                    } else {
                        progressDialog.dismiss();
                    }
                }
            }
        });

        viewModel.getResponse().observe(this, new Observer<ApiResponse>() {
            @Override
            public void onChanged(@Nullable ApiResponse apiResponse) {
                if (apiResponse != null) {
                    if (apiResponse.getError() != null) {
                        error.setVisibility(View.VISIBLE);
                        error.setText(apiResponse.getError());
                    } else if (apiResponse.getMessage() != null) {
                        showToast(apiResponse.getMessage());
                    }
                }
            }
        });

        viewModel.getSignInResponse().observe(this, new Observer<SignInResponse>() {
            @Override
            public void onChanged(@Nullable SignInResponse signInResponse) {
                if (signInResponse != null) {
                    sessionManager.signIn(signInResponse.getToken(), currentUsername);
                    onBackPressed();
                } else {
                    showToast("Failed");
                }
            }
        });
    }

    private void initSignUp() {
        // apply fonts
        signUp.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/poppins/Poppins-SemiBold.otf"));
        signUp.setTransformationMethod(null);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData();
            }
        });
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.registration));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
    }

    private void checkData() {
        String usernameText = username.getText() != null ? username.getText().toString() : "";
        String passwordText = password.getText() != null ? password.getText().toString() : "";
        String confirmPasswordText = confirmPassword.getText() != null ? confirmPassword.getText().toString() : "";

        if (usernameText.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty()) {
            showToast(getString(R.string.must_fill));
        } else {
            if (!passwordText.equals(confirmPasswordText)) {
                showToast(getString(R.string.passwords));
            } else {
                if (usernameText.length() <= 4) {
                    showToast(getString(R.string.username_length));
                } else {
                    if (passwordText.length() < 6) {
                        showToast(getString(R.string.password_length));
                    } else {
                        error.setVisibility(View.GONE);
                        currentUsername = usernameText;
                        viewModel.signUp(SignUpActivity.this, usernameText, passwordText);
                    }
                }
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
