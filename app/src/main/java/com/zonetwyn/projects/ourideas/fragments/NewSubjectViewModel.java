package com.zonetwyn.projects.ourideas.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.zonetwyn.projects.ourideas.payloads.DomainResponse;
import com.zonetwyn.projects.ourideas.retrofit.interfaces.ApiClient;
import com.zonetwyn.projects.ourideas.retrofit.interfaces.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewSubjectViewModel extends ViewModel {

    private MutableLiveData<DomainResponse> domains;

    // we will call this method to get domains
    public LiveData<DomainResponse> getDomains(Context context, int limit) {
        if (domains == null) {
            domains = new MutableLiveData<>();
            loadDomains(context, limit);
        }

        return domains;
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
