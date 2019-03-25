package com.zonetwyn.projects.ourideas.components;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.zonetwyn.projects.ourideas.R;
import com.zonetwyn.projects.ourideas.adapters.FiltersAdapter;
import com.zonetwyn.projects.ourideas.models.Domain;
import com.zonetwyn.projects.ourideas.utils.Event;
import com.zonetwyn.projects.ourideas.utils.EventBus;
import com.zonetwyn.projects.ourideas.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class Filters extends LinearLayout {

    private Context context;
    private RecyclerView recyclerView;
    private FiltersAdapter adapter;
    private List<Domain> domains;

    private List<Domain> selectedDomains;

    public Filters(Context context) {
        super(context);
        this.context = context;
    }

    public Filters(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        init();
    }

    private void init() {
        // binding views
        View rootView = LayoutInflater.from(context).inflate(R.layout.filters_view, this, true);
        recyclerView = rootView.findViewById(R.id.filtersRecyclerView);

        // init filters
        domains = new ArrayList<>();
        selectedDomains = new ArrayList<>();
        adapter = new FiltersAdapter(context, domains);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        onFilterClick();
    }

    private void onFilterClick() {
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Domain domain = domains.get(position);
                boolean selected = !domain.isSelected();
                if (selected) {
                    selectedDomains.add(domain);
                    domain.setRemove(false);
                } else {
                    selectedDomains.remove(domain);
                    domain.setRemove(true);
                }

                // send domain
                Event event = new Event(Event.SUBJECT_FILTERS_DOMAINS, domain);
                EventBus.publish(EventBus.SUBJECT_HOME_FRAGMENT, event);

                if (position == 0) {
                    if (selected) {
                        for (Domain d : domains) {
                            d.setSelected(false);
                        }
                        domain.setSelected(true);
                        domains.set(position, domain);
                        adapter.notifyDataSetChanged();
                    } else {
                        domain.setSelected(false);
                        domains.set(position, domain);
                        adapter.notifyItemChanged(position);
                    }
                } else {
                    domain.setSelected(selected);
                    domains.set(position, domain);
                    if (selectedDomains.contains(domains.get(0))) {
                        selectedDomains.remove(domains.get(0));
                        domains.get(0).setSelected(false);
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter.notifyItemChanged(position);
                    }
                }

                if (selectedDomains.size() == 0) {
                    Domain top = domains.get(0);
                    top.setRemove(false);
                    top.setSelected(true);
                    domains.set(0, top);
                    selectedDomains.add(top);

                    Event ev = new Event(Event.SUBJECT_FILTERS_DOMAINS, top);
                    EventBus.publish(EventBus.SUBJECT_HOME_FRAGMENT, ev);

                    adapter.notifyItemChanged(0);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
    }

    public void setDomains(List<Domain> domains) {
        domains.get(0).setSelected(true);
        selectedDomains.add(domains.get(0));

        this.domains.addAll(domains);
        adapter.notifyDataSetChanged();

        // send domain
        Event event = new Event(Event.SUBJECT_FILTERS_DOMAINS, domains.get(0));
        EventBus.publish(EventBus.SUBJECT_HOME_FRAGMENT, event);
    }
}
