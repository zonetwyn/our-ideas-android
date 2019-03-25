package com.zonetwyn.projects.ourideas.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.zonetwyn.projects.ourideas.payloads.IdeaResponse;
import com.zonetwyn.projects.ourideas.retrofit.interfaces.ApiClient;
import com.zonetwyn.projects.ourideas.retrofit.interfaces.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IdeaViewModel extends ViewModel {

    private MutableLiveData<IdeaResponse> ideas;
    private MutableLiveData<Boolean> isLoading;

    // we will call this method to get the data
    public LiveData<IdeaResponse> getIdeas(Context context, String subjectId, int page) {
        // if the list is null
        if (ideas == null) {
            ideas = new MutableLiveData<>();
            loadIdeas(context, subjectId, page);
        }

        // finally we will return the data
        return ideas;
    }

    // we will call this method to get loading state
    public LiveData<Boolean> isLoading() {
        if (isLoading == null) {
            isLoading = new MutableLiveData<>();
            isLoading.setValue(true);
        }

        return isLoading;
    }

    // This method is using Retrofit to get the JSON data from URL
    public void loadIdeas(Context context, String subjectId, int page) {
        isLoading.setValue(true);

        ApiInterface api = ApiClient.getClient(context).create(ApiInterface.class);
        Call<IdeaResponse> call = api.getIdeas(subjectId, page);

        call.enqueue(new Callback<IdeaResponse>() {

            @Override
            public void onResponse(Call<IdeaResponse> call, Response<IdeaResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    ideas.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<IdeaResponse> call, Throwable t) {

            }
        });
    }
}
