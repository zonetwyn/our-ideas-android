package com.zonetwyn.projects.ourideas.components;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zonetwyn.projects.ourideas.R;
import com.zonetwyn.projects.ourideas.adapters.HeaderAdapter;
import com.zonetwyn.projects.ourideas.models.Domain;
import com.zonetwyn.projects.ourideas.utils.Event;
import com.zonetwyn.projects.ourideas.utils.EventBus;

import java.util.ArrayList;
import java.util.List;

public class Header extends LinearLayout {

    private Context context;
    private RecyclerView recyclerView;
    private ImageView filter;
    private HeaderAdapter adapter;
    private List<Domain> domains;


    public Header(Context context) {
        super(context);
        this.context = context;
    }

    public Header(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        init();
    }

    private void init() {
        // binding views
        View rootView = LayoutInflater.from(context).inflate(R.layout.header_view, this, true);
        recyclerView = rootView.findViewById(R.id.headerRecyclerView);
        filter = rootView.findViewById(R.id.headerFilter);

        // init filters
        domains = new ArrayList<>();
        adapter = new HeaderAdapter(context, domains);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        onFilterClick();
    }

    private void onFilterClick() {
        filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Event event = new Event(Event.SUBJECT_HEADER_FILTER_CLICK, "Open Drawer");
                EventBus.publish(EventBus.SUBJECT_HOME_FRAGMENT, event);
            }
        });
    }

    public void addDomain(Domain domain, boolean clear) {
        if (!domains.contains(domain)) {
            if (clear)
                domains.clear();
            else {
                Domain top = null;
                for (Domain d : domains) {
                    if (d.getName().equals("Tous")) {
                        top = d;
                        break;
                    }
                }
                if (top != null) {
                    domains.remove(top);
                }
            }
            domains.add(domain);
            adapter.notifyDataSetChanged();
        }
    }

    public void removeDomain(Domain domain) {
        domains.remove(domain);
        adapter.notifyDataSetChanged();
    }

    public List<Domain> getDomains() {
        return domains;
    }
}
