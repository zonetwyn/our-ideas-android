package com.zonetwyn.projects.ourideas.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.widget.Toast;

import com.zonetwyn.projects.ourideas.payloads.SubjectResponse;
import com.zonetwyn.projects.ourideas.retrofit.interfaces.ApiClient;
import com.zonetwyn.projects.ourideas.retrofit.interfaces.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubjectsViewModel extends ViewModel {

    private MutableLiveData<SubjectResponse> subjects;
    private MutableLiveData<Boolean> isLoading;

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

    public void loadSubjects(final Context context, int page) {
        isLoading.setValue(true);

        ApiInterface api = ApiClient.getClient(context).create(ApiInterface.class);
        Call<SubjectResponse> call = api.getUserSubjects(page);

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
                isLoading.setValue(false);
            }
        });
    }
}
