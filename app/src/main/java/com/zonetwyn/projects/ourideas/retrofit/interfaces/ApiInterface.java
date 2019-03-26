package com.zonetwyn.projects.ourideas.retrofit.interfaces;

import com.zonetwyn.projects.ourideas.payloads.DomainResponse;
import com.zonetwyn.projects.ourideas.payloads.IdeaRequest;
import com.zonetwyn.projects.ourideas.payloads.IdeaResponse;
import com.zonetwyn.projects.ourideas.payloads.MessageRequest;
import com.zonetwyn.projects.ourideas.payloads.SignInRequest;
import com.zonetwyn.projects.ourideas.payloads.SignInResponse;
import com.zonetwyn.projects.ourideas.payloads.SignUpRequest;
import com.zonetwyn.projects.ourideas.payloads.SubjectRequest;
import com.zonetwyn.projects.ourideas.payloads.SubjectResponse;
import com.zonetwyn.projects.ourideas.payloads.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    String BASE_URL = "https://our-ideas-api.herokuapp.com/api/v1/";

    @GET("subjects")
    Call<SubjectResponse> getSubjects(@Query("page") int page);

    @GET("users/subjects")
    Call<SubjectResponse> getUserSubjects(@Query("page") int page);

    @GET("subjects/{subjectId}/ideas")
    Call<IdeaResponse> getIdeas(@Path("subjectId") String subjectId, @Query("page") int page);

    @GET("domains")
    Call<DomainResponse> getDomains(@Query("limit") int limit);

    @POST("auth/signup")
    Call<ApiResponse> signUp(@Body SignUpRequest request);

    @POST("auth/signin")
    Call<SignInResponse> signIn(@Body SignInRequest request);

    @POST("subjects")
    Call<ApiResponse> newSubject(@Body SubjectRequest request);

    @POST("messages")
    Call<ApiResponse> newMessage(@Body MessageRequest request);

    @PATCH("subjects/{subjectId}/like")
    Call<ApiResponse> like(@Path("subjectId") String subjectId);

    @PATCH("ideas/{ideaId}/like")
    Call<ApiResponse> likeIdea(@Path("ideaId") String ideaId);

    @PUT("subjects/{subjectId}/ideas")
    Call<ApiResponse> newIdea(@Path("subjectId") String subjectId, @Body IdeaRequest request);
}
