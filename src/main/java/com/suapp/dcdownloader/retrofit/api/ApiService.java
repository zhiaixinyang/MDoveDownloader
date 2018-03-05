package com.suapp.dcdownloader.retrofit.api;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by zhaojing on 2018/3/5.
 */

public interface ApiService {
    @Streaming
    @GET()
    Observable<ResponseBody> downFile(@Url() String url, @QueryMap Map<String, String> maps);
}
