package com.zonetwyn.projects.ourideas.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zonetwyn.projects.ourideas.R;
import com.zonetwyn.projects.ourideas.database.SessionManager;
import com.zonetwyn.projects.ourideas.models.Idea;
import com.zonetwyn.projects.ourideas.payloads.ApiResponse;
import com.zonetwyn.projects.ourideas.retrofit.interfaces.ApiClient;
import com.zonetwyn.projects.ourideas.retrofit.interfaces.ApiInterface;
import com.zonetwyn.projects.ourideas.utils.ConnectivityManager;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IdeaAdapter extends RecyclerView.Adapter<IdeaAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        
        public TextView description;
        public TextView date;
        public TextView username;
        public ImageView like;
        public TextView likes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
            username = itemView.findViewById(R.id.username);
            like = itemView.findViewById(R.id.like);
            likes = itemView.findViewById(R.id.likes);
        }
    }

    private Context context;
    private List<Idea> ideas;
    private Typeface semiBold;

    private SessionManager sessionManager;

    public IdeaAdapter(Context context, List<Idea> ideas) {
        this.context = context;
        this.ideas = ideas;
        semiBold = Typeface.createFromAsset(context.getAssets(), "fonts/poppins/Poppins-SemiBold.otf");

        sessionManager = SessionManager.getInstance(context.getApplicationContext());
    }

    @NonNull
    @Override
    public IdeaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View domainView = inflater.inflate(R.layout.idea_item, viewGroup, false);
        return new ViewHolder(domainView);
    }

    @Override
    public void onBindViewHolder(@NonNull final IdeaAdapter.ViewHolder viewHolder, final int i) {
        final Idea idea = ideas.get(i);

        // applying fonts
        viewHolder.date.setTypeface(semiBold);
        viewHolder.username.setTypeface(semiBold);
        viewHolder.likes.setTypeface(semiBold);

        viewHolder.description.setText(idea.getDescription());
        viewHolder.likes.setText(String.valueOf(idea.getLikes()));

        // username
        String username = "@" + idea.getUser().getUsername();
        viewHolder.username.setText(username);


        // date
        // date
        String date = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date currentDate = new Date();
        Date createdAt = null;
        long createdAtTime = 0;
        try {
            createdAt = format.parse(idea.getCreatedAt());
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
            date += "on " + idea.getCreatedAt().split("T")[0].replaceAll("-", "/");
        } else {
            if (hours >= 1 && hours <= 24) {
                date += "" + hours + context.getString(R.string.hours_ago);
            } else {
                date += "" + minutes + context.getString(R.string.minutes_ago);
            }
        }
        viewHolder.date.setText(date);

        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManager.isLoggedIn()) {
                    if (ConnectivityManager.checkInternetConnection(context)) {
                        like(viewHolder, idea, i);
                    } else {
                        Toast.makeText(context, context.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.need_authentication), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void like(final IdeaAdapter.ViewHolder viewHolder, final Idea idea, final int position) {
        ApiInterface api = ApiClient.getClient(context).create(ApiInterface.class);
        Call<ApiResponse> call = api.likeIdea(idea.getId());

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    viewHolder.like.setImageResource(R.drawable.ic_thumb_up_green);
                    int likes = idea.getLikes();
                    idea.setLikes(++likes);
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

    @Override
    public int getItemCount() {
        return ideas.size();
    }
}
