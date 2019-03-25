package com.zonetwyn.projects.ourideas.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zonetwyn.projects.ourideas.R;
import com.zonetwyn.projects.ourideas.models.Domain;

import java.util.List;

public class HeaderAdapter extends RecyclerView.Adapter<HeaderAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = (TextView) itemView;
        }
    }

    private Context context;
    private List<Domain> domains;

    public HeaderAdapter(Context context, List<Domain> domains) {
        this.context = context;
        this.domains = domains;
    }

    @NonNull
    @Override
    public HeaderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View domainView = inflater.inflate(R.layout.header_item, viewGroup, false);
        return new ViewHolder(domainView);
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderAdapter.ViewHolder viewHolder, int i) {
        Domain domain = domains.get(i);
        viewHolder.name.setText(domain.getName());
    }

    @Override
    public int getItemCount() {
        return domains.size();
    }
}
