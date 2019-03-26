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

import com.google.gson.Gson;
import com.zonetwyn.projects.ourideas.R;
import com.zonetwyn.projects.ourideas.activities.SubjectActivity;
import com.zonetwyn.projects.ourideas.database.SessionManager;
import com.zonetwyn.projects.ourideas.models.Domain;
import com.zonetwyn.projects.ourideas.models.Subject;
import com.zonetwyn.projects.ourideas.payloads.ApiResponse;
import com.zonetwyn.projects.ourideas.retrofit.interfaces.ApiClient;
import com.zonetwyn.projects.ourideas.retrofit.interfaces.ApiInterface;
import com.zonetwyn.projects.ourideas.utils.ConnectivityManager;
import com.zonetwyn.projects.ourideas.utils.Event;
import com.zonetwyn.projects.ourideas.utils.EventBus;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView description;
        public TextView date;
        public TextView username;
        public LinearLayout dateLayout;
        public ScrollView domainsLayout;
        public LinearLayout domains;
        public TextView ideasCount;
        public ImageView like;
        public TextView likes;
        public ImageView reply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
            username = itemView.findViewById(R.id.username);
            dateLayout = itemView.findViewById(R.id.dateLayout);
            domainsLayout = itemView.findViewById(R.id.domainsLayout);
            domains = itemView.findViewById(R.id.domains);
            ideasCount = itemView.findViewById(R.id.ideasCount);
            like = itemView.findViewById(R.id.like);
            likes = itemView.findViewById(R.id.likes);
            reply = itemView.findViewById(R.id.reply);
        }
    }

    private Context context;
    private List<Subject> subjects;
    private Typeface medium;
    private Typeface semiBold;

    private SessionManager sessionManager;

    public SubjectAdapter(Context context, List<Subject> subjects) {
        this.context = context;
        this.subjects = subjects;
        medium = Typeface.createFromAsset(context.getAssets(), "fonts/poppins/Poppins-Medium.otf");
        semiBold = Typeface.createFromAsset(context.getAssets(), "fonts/poppins/Poppins-SemiBold.otf");

        sessionManager = SessionManager.getInstance(context.getApplicationContext());
    }

    @NonNull
    @Override
    public SubjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View domainView = inflater.inflate(R.layout.subject_item, viewGroup, false);
        return new ViewHolder(domainView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SubjectAdapter.ViewHolder viewHolder, final int i) {
        final Subject subject = subjects.get(i);

        // applying fonts
        viewHolder.title.setTypeface(medium);
        viewHolder.date.setTypeface(semiBold);
        viewHolder.username.setTypeface(semiBold);
        viewHolder.ideasCount.setTypeface(semiBold);
        viewHolder.likes.setTypeface(semiBold);

        viewHolder.title.setText(subject.getTitle());
        viewHolder.description.setText(truncate(subject.getDescription()));
        viewHolder.ideasCount.setText(String.valueOf(subject.getIdeasCount()));
        viewHolder.likes.setText(String.valueOf(subject.getLikes()));

        // username
        String username = "@" + subject.getUser().getUsername();
        viewHolder.username.setText(username);

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

        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManager.isLoggedIn()) {
                    if (ConnectivityManager.checkInternetConnection(context)) {
                        like(viewHolder, subject, i);
                    } else {
                        Toast.makeText(context, context.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.need_authentication), Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewHolder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManager.isLoggedIn()) {
                    if (ConnectivityManager.checkInternetConnection(context)) {
                        reply(subject);
                    } else {
                        Toast.makeText(context, context.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.need_authentication), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String truncate(String value) {
        if (value.length() > 200) {
            return value.substring(0, 200) + "...";
        }
        return value;
    }

    private void reply(Subject subject) {
        Event event = new Event(Event.SUBJECT_MAIN_NEW_IDEA, new Gson().toJson(subject));
        EventBus.publish(EventBus.SUBJECT_MAIN_ACTIVITY, event);
    }

    private void like(final ViewHolder viewHolder, final Subject subject, final int position) {
        ApiInterface api = ApiClient.getClient(context).create(ApiInterface.class);
        Call<ApiResponse> call = api.like(subject.getId());

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    viewHolder.like.setImageResource(R.drawable.ic_thumb_up_green);
                    int likes = subject.getLikes();
                    subject.setLikes(++likes);
                    notifyItemChanged(position);
                } else {
                    try {
                        ApiResponse error = new Gson().fromJson(response.errorBody().string(), ApiResponse.class);
                        Toast.makeText(context, error.getError(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

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
