package com.zonetwyn.projects.ourideas.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.google.gson.Gson;
import com.zonetwyn.projects.ourideas.payloads.ApiResponse;
import com.zonetwyn.projects.ourideas.payloads.MessageRequest;
import com.zonetwyn.projects.ourideas.retrofit.interfaces.ApiClient;
import com.zonetwyn.projects.ourideas.retrofit.interfaces.ApiInterface;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HelpViewModel extends ViewModel {

    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<ApiResponse> apiResponse;

    public LiveData<Boolean> getLoading() {
        if (isLoading == null)
            isLoading = new MutableLiveData<>();

        isLoading.setValue(false);
        return isLoading;
    }

    public LiveData<ApiResponse> getResponse() {
        if (apiResponse == null)
            apiResponse = new MutableLiveData<>();

        return apiResponse;
    }

    public void newMessage(Context context, final MessageRequest request) {
        isLoading.setValue(true);
        ApiInterface api = ApiClient.getClient(context).create(ApiInterface.class);
        Call<ApiResponse> call = api.newMessage(request);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    apiResponse.setValue(response.body());
                } else {
                    try {
                        ApiResponse error = new Gson().fromJson(response.errorBody().string(), ApiResponse.class);
                        apiResponse.setValue(error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
    }
}
