package com.zonetwyn.projects.ourideas.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zonetwyn.projects.ourideas.R;
import com.zonetwyn.projects.ourideas.adapters.UserSubjectAdapter;
import com.zonetwyn.projects.ourideas.database.SessionManager;
import com.zonetwyn.projects.ourideas.models.Subject;
import com.zonetwyn.projects.ourideas.payloads.SubjectResponse;
import com.zonetwyn.projects.ourideas.utils.ConnectivityManager;

import java.util.ArrayList;
import java.util.List;

public class SubjectsFragment extends Fragment {

    private Context context;

    private SubjectsViewModel viewModel;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refresh;

    private boolean isLoading = false;
    private int currentPage = 1;
    private boolean hasNext = false;

    private UserSubjectAdapter adapter;
    private List<Subject> subjects;

    private SessionManager sessionManager;

    public static SubjectsFragment newInstance() {
        return new SubjectsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.subjects_fragment, container, false);

        sessionManager = SessionManager.getInstance(context.getApplicationContext());

        recyclerView = rootView.findViewById(R.id.recyclerView);
        refresh = rootView.findViewById(R.id.refresh);

        if (sessionManager.isLoggedIn()) {
            initSubjects();
            initSwipeRefresh();
        } else {
            Toast.makeText(context, context.getString(R.string.need_authentication), Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    private void initSwipeRefresh() {
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    private void refresh() {
        if (ConnectivityManager.checkInternetConnection(context)) {
            subjects.clear();
            adapter.notifyDataSetChanged();
            viewModel.loadSubjects(context, 1);
        } else {
            refresh.setRefreshing(false);
            showToast(getString(R.string.no_internet_connection));
        }
    }


    private void initSubjects() {
        subjects = new ArrayList<>();
        adapter = new UserSubjectAdapter(context, subjects);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastVisibleItemPosition() == subjects.size() - 1) {
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void insertSubjects(List<Subject> subjects) {
        this.subjects.addAll(subjects);
        if (this.subjects.size() == 0) {
            showToast(getString(R.string.no_subjects));
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void loadMore() {
        if (hasNext) {
            viewModel.loadSubjects(context, ++currentPage);
        }
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(SubjectsViewModel.class);

        if (!ConnectivityManager.checkInternetConnection(context)) {
            showToast(getString(R.string.no_internet_connection));
            return;
        }

        // subscribe to loading state
        viewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != null) {
                    isLoading = aBoolean;
                    refresh.setRefreshing(isLoading);
                }
            }
        });

        // subscribe to subjects
        viewModel.getSubjects(context, currentPage).observe(this, new Observer<SubjectResponse>() {
            @Override
            public void onChanged(@Nullable SubjectResponse response) {
                if (response != null && response.getSubjects() != null) {
                    currentPage = response.getPage();
                    hasNext = response.isHasNextPage();
                    insertSubjects(response.getSubjects());
                }
            }
        });
    }

}
