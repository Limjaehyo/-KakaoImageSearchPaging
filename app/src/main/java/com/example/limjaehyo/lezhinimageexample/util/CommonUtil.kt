package com.example.limjaehyo.lezhinimageexample.util

import com.example.limjaehyo.lezhinimageexample.BuildConfig
import com.example.limjaehyo.lezhinimageexample.repository.RetrofitAPI
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class CommonUtil {

   companion object  {
        fun getRetrofitAPI(): RetrofitAPI {
            val logging = HttpLoggingInterceptor()
            val httpClient = OkHttpClient.Builder()

            val gson = GsonBuilder()
                    .setLenient()
                    .create()
            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder  = original.newBuilder().header("Authorization","KakaoAK 76b08acf0079a56ec963bd146ea42e58")
                val request = requestBuilder.build()
                chain.proceed(request)

            }

            if(BuildConfig.DEBUG){
                logging.level = HttpLoggingInterceptor.Level.BODY
                httpClient.addInterceptor(logging)

            }else{
                logging.level = HttpLoggingInterceptor.Level.HEADERS
            }


            //        httpClient.readTimeout(60, TimeUnit.SECONDS);
            val retrofit = Retrofit.Builder()
                    .baseUrl("https://dapi.kakao.com/")
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()

            return retrofit.create(RetrofitAPI::class.java)
        }
    }

}

