package com.zonetwyn.projects.ourideas.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.zonetwyn.projects.ourideas.R;
import com.zonetwyn.projects.ourideas.adapters.IdeaAdapter;
import com.zonetwyn.projects.ourideas.models.Idea;
import com.zonetwyn.projects.ourideas.models.Subject;
import com.zonetwyn.projects.ourideas.payloads.IdeaResponse;
import com.zonetwyn.projects.ourideas.utils.ConnectivityManager;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SubjectActivity extends AppCompatActivity {

    public static String keySubject = "Subject";

    private IdeaViewModel viewModel;
    private Subject subject;

    private SwipeRefreshLayout refresh;
    private RecyclerView recyclerView;

    private IdeaAdapter adapter;
    private List<Idea> ideas;

    private int currentPage = 1;
    private boolean hasNext = false;
    private boolean isLoading = false;

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
        setContentView(R.layout.activity_subject);

        // view model
        viewModel = ViewModelProviders.of(this).get(IdeaViewModel.class);

        // init subject
        Intent intent = getIntent();
        subject = intent.getParcelableExtra(keySubject);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setDisplayShowHomeEnabled(true);
        }

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(truncate(subject.getTitle()));
        toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/poppins/Poppins-Medium.otf"));

        // init views
        recyclerView = findViewById(R.id.recyclerView);
        refresh = findViewById(R.id.refresh);

        // initializations
        initIdeas();
        initSwipeRefresh();

        // view model binding
        viewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != null) {
                    isLoading = aBoolean;
                    refresh.setRefreshing(isLoading);
                }
            }
        });

        viewModel.getIdeas(this, subject.getId(), currentPage).observe(this, new Observer<IdeaResponse>() {
            @Override
            public void onChanged(@Nullable IdeaResponse ideaResponse) {
                if (ideaResponse != null) {
                    currentPage = ideaResponse.getPage();
                    hasNext = ideaResponse.isHasNextPage();
                    insertIdeas(ideaResponse.getIdeas());
                }
            }
        });
    }

    private String truncate(String value) {
        if (value.length() > 20) {
            return value.substring(0, 18) + "...";
        }
        return value;
    }

    private void initIdeas() {
        ideas = new ArrayList<>();
        adapter = new IdeaAdapter(SubjectActivity.this, ideas);
        recyclerView.setLayoutManager(new LinearLayoutManager(SubjectActivity.this));
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
                    if (linearLayoutManager != null && linearLayoutManager.findLastVisibleItemPosition() == ideas.size() - 1) {
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void initSwipeRefresh() {
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    private void insertIdeas(List<Idea> ideas) {
        this.ideas.addAll(ideas);
        adapter.notifyDataSetChanged();
    }

    private void refresh() {
        if (ConnectivityManager.checkInternetConnection(SubjectActivity.this)) {
            ideas.clear();
            adapter.notifyDataSetChanged();
            viewModel.loadIdeas(SubjectActivity.this, subject.getId(), 1);
        } else {
            refresh.setRefreshing(false);
            showToast(getString(R.string.no_internet_connection));
        }
    }

    private void loadMore() {
        if (hasNext) {
            viewModel.loadIdeas(SubjectActivity.this, subject.getId(),  ++currentPage);
        }
    }

    private void showToast(String message) {
        Toast.makeText(SubjectActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
