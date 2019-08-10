package com.bonny.bonnyphc.api;

import com.bonny.bonnyphc.models.AppointmentModel;
import com.bonny.bonnyphc.models.BabyModel;
import com.bonny.bonnyphc.models.EmployeeModel;
import com.bonny.bonnyphc.models.ParentModel;
import com.bonny.bonnyphc.models.RecordModel;
import com.bonny.bonnyphc.models.TokenModel;
import com.bonny.bonnyphc.models.UserModel;
import com.bonny.bonnyphc.models.VaccineModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author Aditya Kulkarni
 */

public interface API {

    @POST("auth/api/login/")
    @FormUrlEncoded
    Call<TokenModel> getToken(
            @Field("username") String username,
            @Field("password") String password
    );

    @GET("auth/api/user/")
    Call<UserModel> getUser(
            @Header("Authorization") String key
    );

    @POST("auth/api/logout/")
    Call<UserModel> logout();

    @GET("api/babies/")
    Call<List<BabyModel>> getAllBabies(
            @Header("Authorization") String key
    );

    @GET("/api/phc_emp/")
    Call<List<EmployeeModel>> getEmployee(
            @Header("Authorization") String key
    );

    @GET("api/parent/")
    Call<List<ParentModel>> getParents(
            @Header("Authorization") String key
    );

    @POST("api/babies/")
    @FormUrlEncoded
    Call<ResponseBody> postBabyDetails(
            @Header("Authorization") String key,
            @Field("parent") int parent,
            @Field("first_name") String first_name,
            @Field("last_name") String last_name,
            @Field("place_of_birth") String place_of_birth,
            @Field("weight") int weight,
            @Field("blood_group") String blood_group,
            @Field("birth_date") String date,
            @Field("gender") String gender,
            @Field("tag") String tag,
            @Field("special_notes") String special_notes,
            @Field("text_notifications") boolean text_notifs
    );

    @PATCH("api/babies/{pk}/")
    Call<ResponseBody> updateBabies(
            @Header("Authorization") String key,
            @Path("pk") int pk,
            @Body BabyModel babyModel
    );

    @GET("api/schedule/")
    Call<List<VaccineModel>> getSchedule(
            @Header("Authorization") String key,
            @Query("pk") int pk
    );

    @GET("api/appointments/")
    Call<List<AppointmentModel>> getAppointments(
            @Header("Authorization") String key,
            @Query("pk") int pk
    );

    @GET("api/vaccinations/")
    Call<List<RecordModel>> getRecords(
            @Header("Authorization") String key,
            @Query("pk") int pk
    );

    @PATCH("api/vaccinations/{pk}/")
    Call<RecordModel> updateRecords(
            @Header("Authorization") String key,
            @Path("pk") int pk,
            @Body RecordModel recordModel
    );

    @POST("api/appointments/")
    @FormUrlEncoded
    Call<AppointmentModel> createAppointment(
            @Header("Authorization") String key,
            @Field("baby") int id
    );

    @POST("api/schedule_vaccines/")
    @FormUrlEncoded
    Call<ResponseBody> scheduleVaccine(
            @Header("Authorization") String key,
            @Field("appointment") int appointment,
            @Field("vaccines") String vaccines
    );

    @GET("api/babies/")
    Call<List<BabyModel>> getBaby(
            @Header("Authorization") String key,
            @Query("search") String query
    );
}