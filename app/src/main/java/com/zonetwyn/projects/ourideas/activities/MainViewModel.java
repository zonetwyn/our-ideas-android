package com.zonetwyn.projects.ourideas.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.google.gson.Gson;
import com.zonetwyn.projects.ourideas.models.Subject;
import com.zonetwyn.projects.ourideas.payloads.ApiResponse;
import com.zonetwyn.projects.ourideas.payloads.IdeaRequest;
import com.zonetwyn.projects.ourideas.payloads.SubjectRequest;
import com.zonetwyn.projects.ourideas.retrofit.interfaces.ApiClient;
import com.zonetwyn.projects.ourideas.retrofit.interfaces.ApiInterface;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {

    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<ApiResponse> subjectResponse;
    private MutableLiveData<ApiResponse> ideaResponse;

    private MutableLiveData<Integer> currentMenu;

    public LiveData<Boolean> getLoading() {
        if (isLoading == null)
            isLoading = new MutableLiveData<>();

        isLoading.setValue(false);
        return isLoading;
    }

    public LiveData<ApiResponse> getSubjectResponse() {
        if (subjectResponse == null)
            subjectResponse = new MutableLiveData<>();

        return subjectResponse;
    }

    public LiveData<ApiResponse> getIdeaResponse() {
        if (ideaResponse == null)
            ideaResponse = new MutableLiveData<>();

        return ideaResponse;
    }

    public LiveData<Integer> getCurrentMenu() {
        if (currentMenu == null)
            currentMenu = new MutableLiveData<>();

        return currentMenu;
    }

    public void setCurrentMenu(int menu) {
        currentMenu.setValue(menu);
    }

    public void newSubject(Context context, final SubjectRequest request) {
        isLoading.setValue(true);
        ApiInterface api = ApiClient.getClient(context).create(ApiInterface.class);
        Call<ApiResponse> call = api.newSubject(request);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    subjectResponse.setValue(response.body());
                } else {
                    try {
                        ApiResponse error = new Gson().fromJson(response.errorBody().string(), ApiResponse.class);
                        subjectResponse.setValue(error);
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

    public void newIdea(Context context, final IdeaRequest request, final Subject subject) {
        isLoading.setValue(true);
        ApiInterface api = ApiClient.getClient(context).create(ApiInterface.class);
        Call<ApiResponse> call = api.newIdea(subject.getId(), request);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    ApiResponse data = response.body();
                    if (data != null) {
                        data.setData(new Gson().toJson(subject));
                        ideaResponse.setValue(data);
                    }
                } else {
                    try {
                        ApiResponse error = new Gson().fromJson(response.errorBody().string(), ApiResponse.class);
                        ideaResponse.setValue(error);
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
