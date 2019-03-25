package com.zonetwyn.projects.ourideas.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.google.gson.Gson;
import com.zonetwyn.projects.ourideas.payloads.SignInRequest;
import com.zonetwyn.projects.ourideas.payloads.SignInResponse;
import com.zonetwyn.projects.ourideas.retrofit.interfaces.ApiClient;
import com.zonetwyn.projects.ourideas.retrofit.interfaces.ApiInterface;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInViewModel extends ViewModel {

    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<SignInResponse> signInResponse;

    public LiveData<Boolean> getLoading() {
        if (isLoading == null)
            isLoading = new MutableLiveData<>();

        isLoading.setValue(false);
        return isLoading;
    }

    public LiveData<SignInResponse> getSignInResponse() {
        if (signInResponse == null)
            signInResponse = new MutableLiveData<>();

        return signInResponse;
    }

    public void signIn(Context context, String username, String password) {
        isLoading.setValue(true);
        SignInRequest sign = new SignInRequest(username, password);

        ApiInterface api = ApiClient.getClient(context).create(ApiInterface.class);
        final Call<SignInResponse> call = api.signIn(sign);

        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    signInResponse.setValue(response.body());
                } else {
                    try {
                        SignInResponse error = new Gson().fromJson(response.errorBody().string(), SignInResponse.class);
                        signInResponse.setValue(error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
    }
}
