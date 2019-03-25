package com.zonetwyn.projects.ourideas.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zonetwyn.projects.ourideas.R;
import com.zonetwyn.projects.ourideas.adapters.SubjectAdapter;
import com.zonetwyn.projects.ourideas.components.Filters;
import com.zonetwyn.projects.ourideas.components.Header;
import com.zonetwyn.projects.ourideas.database.SessionManager;
import com.zonetwyn.projects.ourideas.models.Domain;
import com.zonetwyn.projects.ourideas.models.Subject;
import com.zonetwyn.projects.ourideas.payloads.DomainResponse;
import com.zonetwyn.projects.ourideas.payloads.SubjectResponse;
import com.zonetwyn.projects.ourideas.utils.ConnectivityManager;
import com.zonetwyn.projects.ourideas.utils.Event;
import com.zonetwyn.projects.ourideas.utils.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

public class HomeFragment extends Fragment {

    private Context context;

    private HomeViewModel viewModel;

    private DrawerLayout drawerLayout;
    private Filters filters;
    private Header header;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refresh;
    private Button newSubject;


    private boolean isLoading = false;
    private int currentPage = 1;
    private boolean hasNext = false;

    private SubjectAdapter adapter;
    private List<Subject> subjects;

    private List<Domain> filterDomains = new ArrayList<>();

    private SessionManager sessionManager;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment, container, false);

        sessionManager = SessionManager.getInstance(context.getApplicationContext());

        drawerLayout = rootView.findViewById(R.id.drawerLayout);
        filters = rootView.findViewById(R.id.filters);
        header = rootView.findViewById(R.id.header);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        refresh = rootView.findViewById(R.id.refresh);
        newSubject = rootView.findViewById(R.id.newSubject);

        subscribeToBus();

        initSubjects();
        initSwipeRefresh();
        initNewSubjectButton();

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

    private void initNewSubjectButton() {
        // add fonts
        newSubject.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/poppins/Poppins-SemiBold.otf"));
        newSubject.setTransformationMethod(null);

        newSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManager.isLoggedIn()) {
                    Event event = new Event(Event.SUBJECT_MAIN_NEW_SUBJECT, "Show Form");
                    EventBus.publish(EventBus.SUBJECT_MAIN_ACTIVITY, event);
                }  else {
                    Toast.makeText(context, context.getString(R.string.need_authentication), Toast.LENGTH_SHORT).show();
                }
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

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void initSubjects() {
        subjects = new ArrayList<>();
        adapter = new SubjectAdapter(context, subjects);
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

    private void initDomains(List<Domain> domains) {
        filters.setDomains(domains);
    }

    private void insertSubjects(List<Subject> subjects) {
        this.subjects.addAll(subjects);
        adapter.notifyDataSetChanged();
    }

    private void loadMore() {
        if (hasNext) {
            viewModel.loadSubjects(context, ++currentPage);
        }
    }

    private void subscribeToBus() {
        EventBus.subscribe(EventBus.SUBJECT_HOME_FRAGMENT, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                if (o instanceof Event) {
                    Event event = (Event) o;
                    if (event.getData() != null && event.getSubject() != 0) {
                        switch (event.getSubject()) {
                            case Event.SUBJECT_FILTERS_DOMAINS:
                                if (event.getData() instanceof Domain) {
                                    Domain domain = (Domain) event.getData();
                                    onFiltersSelected(domain);
                                }
                                break;
                            case Event.SUBJECT_HEADER_FILTER_CLICK:
                                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                                    drawerLayout.closeDrawer(GravityCompat.END);
                                } else {
                                    drawerLayout.openDrawer(GravityCompat.END);
                                }
                                break;
                            case Event.SUBJECT_HOME_UPDATE_COUNT:
                                if (event.getData() instanceof String) {
                                    String json = (String) event.getData();
                                    Subject subject = new Gson().fromJson(json, Subject.class);
                                    updateCount(subject);
                                }
                                break;
                        }
                    }
                }
            }
        });
    }

    private void updateCount(Subject subject) {
        Subject toUpdate = null;
        int position = 0;
        for (int i=0; i<subjects.size(); i++) {
            Subject s = subjects.get(i);
            if (s.getId().equals(subject.getId())) {
                toUpdate = s;
                position = i;
                break;
            }
        }
        if (toUpdate != null) {
            int count = toUpdate.getIdeasCount();
            toUpdate.setIdeasCount(++count);
            subjects.set(position, toUpdate);
            adapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

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
                    List<Subject> filteredSubjects = response.getSubjects();
                    if (filterDomains.size() >= 1) {
                        filteredSubjects = applyFilters(response.getSubjects());
                    }
                    insertSubjects(filteredSubjects);
                }
            }
        });

        // subscribe to domains limit to 20
        viewModel.getDomains(context, 20).observe(this, new Observer<DomainResponse>() {
            @Override
            public void onChanged(@Nullable DomainResponse response) {
                if (response != null && response.getDomains() != null) {
                    List<Domain> domains = response.getDomains();
                    domains.add(0, new Domain("", "Tous", ""));
                    initDomains(domains);
                }
            }
        });
    }

    // apply filters
    private List<Subject> applyFilters(List<Subject> subjectList) {
        List<Subject> list = new ArrayList<>();
        for (Subject subject : subjectList) {
            boolean valid = false;
            for (Domain domain : filterDomains) {
                if (match(subject.getDomains(), domain)) {
                    valid = true;
                    break;
                }
            }

            if (valid) {
                list.add(subject);
            }
        }

        return list;
    }

    private boolean match(List<Domain> values, Domain matcher) {
        boolean match = false;
        for (Domain d : values) {
            if (matcher.getName().contains(d.getName())) {
                match = true;
                break;
            }
        }

        return match;
    }

    private void onFiltersSelected(Domain domain) {
        if (domain.isRemove()) {
            header.removeDomain(domain);
        } else {
            if (domain.getName().equals("Tous")) {
                header.addDomain(domain, true);
            } else {
                header.addDomain(domain, false);
            }
        }

        setFilters();
    }

    private void setFilters() {
        //
        filterDomains = new ArrayList<>();
        filterDomains.addAll(header.getDomains());
        // check "all" filter
        if (filterDomains.size() == 1 && filterDomains.get(0).getName().equals("Tous")) {
            filterDomains.clear();
        }

        refresh();
    }

    public void onLimitReached() {
        Toast.makeText(context, "Limit reached", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.unregister(this);
    }
}
