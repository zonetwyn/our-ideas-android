package com.zonetwyn.projects.ourideas.activities;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
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
import com.zonetwyn.projects.ourideas.payloads.SignInResponse;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignInActivity extends AppCompatActivity {

    private SignInViewModel viewModel;

    private TextView error;
    private EditText username;
    private EditText password;
    private Button signIn;

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

        setContentView(R.layout.activity_sign_in);

        // session manager
        sessionManager = SessionManager.getInstance(getApplicationContext());

        // init viewModel
        viewModel = ViewModelProviders.of(this).get(SignInViewModel.class);

        // init views
        error = findViewById(R.id.error);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        signIn = findViewById(R.id.signIn);

        initSignIn();
        initProgressDialog();

        // view model
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

        viewModel.getSignInResponse().observe(this, new Observer<SignInResponse>() {
            @Override
            public void onChanged(@Nullable SignInResponse signInResponse) {
                if (signInResponse != null) {
                    if (signInResponse.getError() != null) {
                        error.setVisibility(View.VISIBLE);
                        error.setText(signInResponse.getError());
                    } else if (signInResponse.getMessage() != null) {
                       sessionManager.signIn(signInResponse.getToken(), currentUsername);
                       onBackPressed();
                    }
                } else {
                    showToast("Failed");
                }
            }
        });
    }

    private void initSignIn() {
        // apply fonts
        signIn.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/poppins/Poppins-SemiBold.otf"));
        signIn.setTransformationMethod(null);

        signIn.setOnClickListener(new View.OnClickListener() {
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

        if (usernameText.isEmpty() || passwordText.isEmpty()) {
            showToast(getString(R.string.must_fill));
        } else {
            error.setVisibility(View.GONE);
            currentUsername = usernameText;
            viewModel.signIn(SignInActivity.this, usernameText, passwordText);
        }
    }

    private void showToast(String message) {
        Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
