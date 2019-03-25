package com.zonetwyn.projects.ourideas.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.google.gson.Gson;
import com.zonetwyn.projects.ourideas.payloads.ApiResponse;
import com.zonetwyn.projects.ourideas.payloads.SignInRequest;
import com.zonetwyn.projects.ourideas.payloads.SignInResponse;
import com.zonetwyn.projects.ourideas.payloads.SignUpRequest;
import com.zonetwyn.projects.ourideas.retrofit.interfaces.ApiClient;
import com.zonetwyn.projects.ourideas.retrofit.interfaces.ApiInterface;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpViewModel extends ViewModel {

    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<ApiResponse> apiResponse;
    private MutableLiveData<SignInResponse> signInResponse;


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

    public LiveData<SignInResponse> getSignInResponse() {
        if (signInResponse == null)
            signInResponse = new MutableLiveData<>();

        return signInResponse;
    }

    public void signUp(final Context context, String username, String password) {
        isLoading.setValue(true);
        SignUpRequest user = new SignUpRequest(username, password, "user");
        SignInRequest sign = new SignInRequest(username, password);

        ApiInterface api = ApiClient.getClient(context).create(ApiInterface.class);
        // sign up
        Call<ApiResponse> call = api.signUp(user);
        // sign in
        final Call<SignInResponse> signInCall = api.signIn(sign);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    // apiResponse.setValue(response.body());
                    signInCall.enqueue(new Callback<SignInResponse>() {
                        @Override
                        public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                            isLoading.setValue(false);
                            if (response.isSuccessful()) {
                                signInResponse.setValue(response.body());
                            }
                        }

                        @Override
                        public void onFailure(Call<SignInResponse> call, Throwable t) {
                            isLoading.setValue(false);
                        }
                    });
                } else {
                    isLoading.setValue(false);
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
