package com.example.limjaehyo.lezhinimageexample.repository

import com.example.limjaehyo.lezhinimageexample.model.datasource.ImageQueryModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface RetrofitAPI {

   @GET("/v2/search/image")
  fun getSearchImage(@QueryMap value : Map<String, String>) :  Single<ImageQueryModel>


}