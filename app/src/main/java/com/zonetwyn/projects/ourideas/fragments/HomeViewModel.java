package com.zonetwyn.projects.ourideas.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.zonetwyn.projects.ourideas.payloads.DomainResponse;
import com.zonetwyn.projects.ourideas.payloads.SubjectResponse;
import com.zonetwyn.projects.ourideas.retrofit.interfaces.ApiClient;
import com.zonetwyn.projects.ourideas.retrofit.interfaces.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<SubjectResponse> subjects;
    private MutableLiveData<Boolean> isLoading;

    private MutableLiveData<DomainResponse> domains;

    // we will call this method to get the data
    public LiveData<SubjectResponse> getSubjects(Context context, int page) {
        // if the list is null
        if (subjects == null) {
            subjects = new MutableLiveData<>();
            loadSubjects(context, page);
        }

        // finally we will return the data
        return subjects;
    }

    // we will call this method to get loading state
    public LiveData<Boolean> isLoading() {
        if (isLoading == null) {
            isLoading = new MutableLiveData<>();
            isLoading.setValue(true);
        }

        return isLoading;
    }

    // we will call this method to get domains
    public LiveData<DomainResponse> getDomains(Context context, int limit) {
        if (domains == null) {
            domains = new MutableLiveData<>();
            loadDomains(context, limit);
        }

        return domains;
    }

    // This method is using Retrofit to get the JSON data from URL
    public void loadSubjects(Context context, int page) {
        isLoading.setValue(true);

        ApiInterface api = ApiClient.getClient(context).create(ApiInterface.class);
        Call<SubjectResponse> call = api.getSubjects(page);

        call.enqueue(new Callback<SubjectResponse>() {

            @Override
            public void onResponse(Call<SubjectResponse> call, Response<SubjectResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    subjects.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<SubjectResponse> call, Throwable t) {

            }
        });
    }

    private void loadDomains(Context context, int limit) {
        ApiInterface api = ApiClient.getClient(context).create(ApiInterface.class);
        Call<DomainResponse> call = api.getDomains(limit);

        call.enqueue(new Callback<DomainResponse>() {
            @Override
            public void onResponse(Call<DomainResponse> call, Response<DomainResponse> response) {
                if (response.isSuccessful()) {
                    domains.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<DomainResponse> call, Throwable t) {

            }
        });
    }
}
