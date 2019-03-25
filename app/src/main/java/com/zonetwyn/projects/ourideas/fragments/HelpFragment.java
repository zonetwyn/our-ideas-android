package com.zonetwyn.projects.ourideas.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zonetwyn.projects.ourideas.R;
import com.zonetwyn.projects.ourideas.database.SessionManager;
import com.zonetwyn.projects.ourideas.payloads.ApiResponse;
import com.zonetwyn.projects.ourideas.payloads.MessageRequest;
import com.zonetwyn.projects.ourideas.payloads.SubjectRequest;
import com.zonetwyn.projects.ourideas.utils.ConnectivityManager;
import com.zonetwyn.projects.ourideas.utils.Event;
import com.zonetwyn.projects.ourideas.utils.EventBus;

public class HelpFragment extends Fragment {

    private Context context;
    private HelpViewModel viewModel;

    private TextView headerOne;
    private TextView headerTwo;

    private EditText title;
    private EditText description;
    private Button submit;

    private ProgressDialog progressDialog;

    private SessionManager sessionManager;

    public static HelpFragment newInstance() {
        return new HelpFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.help_fragment, container, false);

        sessionManager = SessionManager.getInstance(context.getApplicationContext());

        headerOne = rootView.findViewById(R.id.headerOne);
        headerTwo = rootView.findViewById(R.id.headerTwo);

        title = rootView.findViewById(R.id.title);
        description = rootView.findViewById(R.id.description);
        submit = rootView.findViewById(R.id.submit);

        // headers font
        headerOne.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/poppins/Poppins-SemiBold.otf"));
        headerTwo.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/poppins/Poppins-SemiBold.otf"));

        initSubmit();
        initProgressDialog();

        return rootView;
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(getString(R.string.wait));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
    }

    private void initSubmit() {
        submit.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/poppins/Poppins-SemiBold.otf"));
        submit.setTransformationMethod(null);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManager.isLoggedIn()) {
                    if (ConnectivityManager.checkInternetConnection(context)) {
                        checkData();
                    } else {
                        showToast(getString(R.string.no_internet_connection));
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.need_authentication), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkData() {
        String titleText = title.getText() != null ? title.getText().toString() : "";
        String descriptionText = description.getText() != null ? description.getText().toString() : "";

        if (titleText.isEmpty() || descriptionText.isEmpty()) {
            showToast(getString(R.string.title_description_empty));
        } else {
            MessageRequest request = new MessageRequest(titleText, descriptionText);
            viewModel.newMessage(context, request);
        }
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(HelpViewModel.class);

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
                        showToast(apiResponse.getError());
                    } else if (apiResponse.getMessage() != null) {
                        showMessageDialog(apiResponse.getMessage());
                    }
                }
            }
        });

    }

    private void showMessageDialog(String message) {
        Dialog messageDialog = new Dialog(context);
        messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        messageDialog.setContentView(R.layout.message_dialog);
        Window window = messageDialog.getWindow();
        if (window != null) {
            window.setLayout(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        }

        TextView messageView = messageDialog.findViewById(R.id.message);
        messageView.setText(message);

        // show dialog
        messageDialog.show();
    }
}
