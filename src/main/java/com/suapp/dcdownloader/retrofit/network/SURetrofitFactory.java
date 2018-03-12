package com.suapp.dcdownloader.retrofit.network;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit 工厂类
 */
public class SURetrofitFactory extends RetrofitFactory {

    public static <T> T create(Context context, String baseApiUrl, Class<T> service) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        clientBuilder.connectTimeout(10000, TimeUnit.MILLISECONDS);
        clientBuilder.readTimeout(10000, TimeUnit.MILLISECONDS);
        clientBuilder.writeTimeout(50000, TimeUnit.MILLISECONDS);

        clientBuilder.addInterceptor(getPublicParamsInterceptor(context));

        CallAdapter.Factory callAdapterFactory;
        callAdapterFactory = RxJava2CallAdapterFactory.create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseApiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(callAdapterFactory)
                .client(clientBuilder.build())
                .build();

        return retrofit.create(service);
    }
}
