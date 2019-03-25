package com.zonetwyn.projects.ourideas.fragments;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zonetwyn.projects.ourideas.R;
import com.zonetwyn.projects.ourideas.adapters.DomainAdapter;
import com.zonetwyn.projects.ourideas.models.Domain;
import com.zonetwyn.projects.ourideas.payloads.DomainResponse;
import com.zonetwyn.projects.ourideas.payloads.SubjectRequest;
import com.zonetwyn.projects.ourideas.utils.ConnectivityManager;
import com.zonetwyn.projects.ourideas.utils.Event;
import com.zonetwyn.projects.ourideas.utils.EventBus;
import com.zonetwyn.projects.ourideas.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class NewSubjectFragment extends BottomSheetDialogFragment {

    private Context context;
    private NewSubjectViewModel viewModel;

    private TextView header;
    private EditText title;
    private EditText description;
    private RecyclerView recyclerView;
    private Button save;

    private DomainAdapter adapter;
    private List<Domain> domains;

    private List<Domain> selectedDomains;

    public NewSubjectFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         View rootView = inflater.inflate(R.layout.new_subject_fragment, container, false);
         header = rootView.findViewById(R.id.header);
         title = rootView.findViewById(R.id.title);
         description = rootView.findViewById(R.id.description);
         recyclerView = rootView.findViewById(R.id.recyclerView);
         save = rootView.findViewById(R.id.save);

         initSave();
         initDomains();

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
        String titleText = title.getText() != null ? title.getText().toString() : "";
        String descriptionText = description.getText() != null ? description.getText().toString() : "";
        int count = selectedDomains.size();

        if (titleText.isEmpty() || descriptionText.isEmpty()) {
            showToast(getString(R.string.title_description_empty));
        } else {
            if (count == 0) {
                showToast(getString(R.string.one_domain));
            } else {
                SubjectRequest request = new SubjectRequest(titleText, descriptionText, selectedDomains);
                String json = new Gson().toJson(request);
                Event event = new Event(Event.SUBJECT_MAIN_SAVE_SUBJECT, json);
                EventBus.publish(EventBus.SUBJECT_MAIN_ACTIVITY, event);

                dismiss();
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void initDomains() {
        domains = new ArrayList<>();
        selectedDomains = new ArrayList<>();
        adapter = new DomainAdapter(context, domains);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Domain domain = domains.get(position);
                boolean selected = !domain.isSelected();
                if (selected) {
                    selectedDomains.add(domain);
                } else {
                    selectedDomains.remove(domain);
                }

                domain.setSelected(selected);
                domains.set(position, domain);
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(NewSubjectViewModel.class);

        if (!ConnectivityManager.checkInternetConnection(context)) {
            showToast(getString(R.string.no_internet_connection));
            return;
        }

        // subscribe to domains limit to 20
        viewModel.getDomains(context, 20).observe(this, new Observer<DomainResponse>() {
            @Override
            public void onChanged(@Nullable DomainResponse response) {
                if (response != null && response.getDomains() != null) {
                    domains.addAll(response.getDomains());
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
