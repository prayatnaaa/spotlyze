package com.bangkit.spotlyze.data.remote.retrofit

import com.bangkit.spotlyze.data.remote.request.FavoriteRequest
import com.bangkit.spotlyze.data.remote.request.LoginRequest
import com.bangkit.spotlyze.data.remote.response.AddFavoriteResponse
import com.bangkit.spotlyze.data.remote.response.DeleteFavouriteResponse
import com.bangkit.spotlyze.data.remote.response.GetFavoriteResponseItem
import com.bangkit.spotlyze.data.remote.response.GetSkincareResponseItem
import com.bangkit.spotlyze.data.remote.response.GetUserProfileResponse
import com.bangkit.spotlyze.data.remote.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("skincare/{id}")
    suspend fun getSkincareById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): List<GetSkincareResponseItem>

    @GET("skincare")
    suspend fun getAllSkincare(
        @Header("Authorization") token: String
    ): List<GetSkincareResponseItem>

    @GET("profile/{id}")
    suspend fun getUserProfile(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): GetUserProfileResponse

    @POST("favorite")
    suspend fun addFavorite(
        @Header("Authorization") token: String,
        @Body request: FavoriteRequest
    ): AddFavoriteResponse

    @HTTP(method = "DELETE", path = "favorite", hasBody = true)
    suspend fun deleteFavorite(
        @Header("Authorization") token: String,
        @Body request: FavoriteRequest
    ): DeleteFavouriteResponse

    @GET("favorite/{id}")
    suspend fun getFavorite(
        @Header("Authorization") token: String,
        @Path("id") userId: String
    ): List<GetFavoriteResponseItem>


}