package com.zonetwyn.projects.ourideas.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.zonetwyn.projects.ourideas.R;
import com.zonetwyn.projects.ourideas.activities.SubjectActivity;
import com.zonetwyn.projects.ourideas.database.SessionManager;
import com.zonetwyn.projects.ourideas.models.Domain;
import com.zonetwyn.projects.ourideas.models.Subject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class UserSubjectAdapter extends RecyclerView.Adapter<UserSubjectAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView description;
        public TextView date;
        public LinearLayout dateLayout;
        public ScrollView domainsLayout;
        public LinearLayout domains;
        public TextView ideasCount;
        public ImageView like;
        public TextView likes;
        public TextView status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
            dateLayout = itemView.findViewById(R.id.dateLayout);
            domainsLayout = itemView.findViewById(R.id.domainsLayout);
            domains = itemView.findViewById(R.id.domains);
            ideasCount = itemView.findViewById(R.id.ideasCount);
            like = itemView.findViewById(R.id.like);
            likes = itemView.findViewById(R.id.likes);
            status = itemView.findViewById(R.id.status);
        }
    }

    private Context context;
    private List<Subject> subjects;
    private Typeface medium;
    private Typeface semiBold;

    private SessionManager sessionManager;

    public UserSubjectAdapter(Context context, List<Subject> subjects) {
        this.context = context;
        this.subjects = subjects;
        medium = Typeface.createFromAsset(context.getAssets(), "fonts/poppins/Poppins-Medium.otf");
        semiBold = Typeface.createFromAsset(context.getAssets(), "fonts/poppins/Poppins-SemiBold.otf");

        sessionManager = SessionManager.getInstance(context.getApplicationContext());
    }

    @NonNull
    @Override
    public UserSubjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View domainView = inflater.inflate(R.layout.user_subject_item, viewGroup, false);
        return new ViewHolder(domainView);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserSubjectAdapter.ViewHolder viewHolder, final int i) {
        final Subject subject = subjects.get(i);

        // applying fonts
        viewHolder.title.setTypeface(medium);
        viewHolder.date.setTypeface(semiBold);
        viewHolder.ideasCount.setTypeface(semiBold);
        viewHolder.likes.setTypeface(semiBold);
        viewHolder.status.setTypeface(semiBold);

        viewHolder.title.setText(subject.getTitle());
        viewHolder.description.setText(subject.getDescription());
        viewHolder.ideasCount.setText(String.valueOf(subject.getIdeasCount()));
        viewHolder.likes.setText(String.valueOf(subject.getLikes()));

        // display status
        if (subject.getStatus().equals("opened")) {
            viewHolder.status.setText(context.getString(R.string.status_opened));
            viewHolder.status.setBackgroundResource(R.drawable.subject_status_opened);
        } else {
            viewHolder.status.setText(context.getString(R.string.status_rejected));
            viewHolder.status.setBackgroundResource(R.drawable.subject_status_rejected);
        }

        // displaying domains
        if (subject.getDomains() != null && !subject.getDomains().isEmpty()) {
            viewHolder.domains.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(context);
            for (Domain domain : subject.getDomains()) {
                TextView view = (TextView) inflater.inflate(R.layout.domain_item, viewHolder.domains, false);
                view.setText(domain.getName());
                viewHolder.domains.addView(view);
            }
        } else {
            viewHolder.domainsLayout.setVisibility(View.GONE);
        }

        // date
        String date = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date currentDate = new Date();
        Date createdAt = null;
        long createdAtTime = 0;
        try {
            createdAt = format.parse(subject.getCreatedAt());
            format.setTimeZone(TimeZone.getDefault());
            String formatted = format.format(createdAt);
            createdAtTime = format.parse(formatted).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long elapsed = currentDate.getTime() - createdAtTime;
        long days = TimeUnit.DAYS.convert(elapsed, TimeUnit.MILLISECONDS);
        long hours = TimeUnit.HOURS.convert(elapsed, TimeUnit.MILLISECONDS);
        long minutes = TimeUnit.MINUTES.convert(elapsed, TimeUnit.MILLISECONDS);

        if (days >= 1 && days <= 30) {
            date += "" + days + context.getString(R.string.days_ago);
        } else if (days > 30) {
            date += "on " + subject.getCreatedAt().split("T")[0].replaceAll("-", "/");
        } else {
            if (hours >= 1 && hours <= 24) {
                date += "" + hours + context.getString(R.string.hours_ago);
            } else {
                date += "" + minutes + context.getString(R.string.minutes_ago);
            }
        }
        viewHolder.date.setText(date);

        // handle click
        viewHolder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIdeas(subject);
            }
        });
        viewHolder.description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIdeas(subject);
            }
        });
    }

    private void showIdeas(Subject subject) {
        if (subject.getIdeasCount() == 0) {
            Toast.makeText(context, context.getString(R.string.lack_ideas), Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(context, SubjectActivity.class);
            intent.putExtra(SubjectActivity.keySubject, subject);
            context.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }
}
