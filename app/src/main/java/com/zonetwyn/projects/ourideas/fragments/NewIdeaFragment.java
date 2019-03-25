package com.zonetwyn.projects.ourideas.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zonetwyn.projects.ourideas.R;
import com.zonetwyn.projects.ourideas.models.Subject;
import com.zonetwyn.projects.ourideas.payloads.IdeaData;
import com.zonetwyn.projects.ourideas.payloads.IdeaRequest;
import com.zonetwyn.projects.ourideas.utils.ConnectivityManager;
import com.zonetwyn.projects.ourideas.utils.Event;
import com.zonetwyn.projects.ourideas.utils.EventBus;

public class NewIdeaFragment extends BottomSheetDialogFragment {

    public static String keySubject = "subject";
    private Subject subject;

    private Context context;

    private TextView header;
    private EditText description;
    private Button save;

    public NewIdeaFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         View rootView = inflater.inflate(R.layout.new_idea_fragment, container, false);
         header = rootView.findViewById(R.id.header);
         description = rootView.findViewById(R.id.description);
         save = rootView.findViewById(R.id.save);

         // get subject id
         if (getArguments() != null) {
             subject = getArguments().getParcelable(keySubject);
         }

         initSave();

         // setup header font
         header.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/poppins/Poppins-Medium.otf"));

         return rootView;
    }

    private void initSave() {
        save.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/poppins/Poppins-SemiBold.otf"));
        save.setTransformationMethod(null);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityManager.checkInternetConnection(context)) {
                    checkData();
                } else {
                    showToast(getString(R.string.no_internet_connection));
                }
            }
        });
    }

    private void checkData() {
        String descriptionText = description.getText() != null ? description.getText().toString() : "";

        if (descriptionText.isEmpty()) {
            showToast(getString(R.string.description_empty));
        } else {
            IdeaRequest request = new IdeaRequest("Reply to a subject", descriptionText);
            IdeaData ideaData = new IdeaData(request, subject);
            String json = new Gson().toJson(ideaData);
            Event event = new Event(Event.SUBJECT_MAIN_SAVE_IDEA, json);
            EventBus.publish(EventBus.SUBJECT_MAIN_ACTIVITY, event);

            dismiss();
        }
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
