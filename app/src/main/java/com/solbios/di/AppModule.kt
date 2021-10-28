package com.solbios.di

import com.solbios.interfaces.ApiServiceIn
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import com.google.gson.GsonBuilder

import com.google.gson.Gson
import okhttp3.logging.HttpLoggingInterceptor


@Module
@InstallIn(SingletonComponent::class)
 object AppModule {

    val baseUrl  = "https://www.savisoft.in/medical/public/api/v1/"
    var gson = GsonBuilder()
        .setLenient()
        .create()

    val interceptor =  HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    val client =  OkHttpClient.Builder().addInterceptor(interceptor).build()

    @Provides
    @Singleton
    fun provideApiService() = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build().create(ApiServiceIn::class.java)

  /*  @Provides
    @Singleton
    fun providOkhhtp()=
        OkHttpClient.Builder().addInterceptor(logging) .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS);*/


}