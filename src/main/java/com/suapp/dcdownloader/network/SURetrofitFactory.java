package com.suapp.dcdownloader.network;

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

/**
 * Retrofit 工厂类
 */
public class SURetrofitFactory extends RetrofitFactory {

    public static <T> T create(@NonNull Context context, @NonNull String baseApiUrl, @NonNull Class<T> service) {
        return create(context, baseApiUrl, true, service);
    }

    public static <T> T create(@NonNull Context context, @NonNull String baseApiUrl, boolean encrypt, @NonNull Class<T> service) {
        return create(context, baseApiUrl, encrypt, CallAdapterType.CALL_ADAPTER_RXJAVA2, service);
    }

    public static <T> T create(Context context, String baseApiUrl, boolean encrypt, @CallAdapterType int callAdapterType, Class<T> service) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        clientBuilder.connectTimeout(10000, TimeUnit.MILLISECONDS);
        clientBuilder.readTimeout(10000, TimeUnit.MILLISECONDS);
        clientBuilder.writeTimeout(50000, TimeUnit.MILLISECONDS);

        clientBuilder.addInterceptor(getPublicParamsInterceptor(context));

        CallAdapter.Factory callAdapterFactory;
        switch (callAdapterType) {
            case CallAdapterType.CALL_ADAPTER_RXJAVA2:
                callAdapterFactory = RxJava2CallAdapterFactory.create();
                break;
            case CallAdapterType.CALL_ADAPTER_RXJAVA:
            default:
                callAdapterFactory = RxJava2CallAdapterFactory.create();
                break;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseApiUrl)
                .addConverterFactory(DecodeConverterFactory.create(encrypt))
                .addCallAdapterFactory(callAdapterFactory)
                .client(clientBuilder.build())
                .build();

        return retrofit.create(service);
    }

    @IntDef(value = {CallAdapterType.CALL_ADAPTER_RXJAVA, CallAdapterType.CALL_ADAPTER_RXJAVA2})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CallAdapterType {
        // Retrofit 默认添加 Call 返回 类型的 CallAdapterFactory，所以这里不定义
        int CALL_ADAPTER_RXJAVA = 1;
        int CALL_ADAPTER_RXJAVA2 = 2;
    }
}
