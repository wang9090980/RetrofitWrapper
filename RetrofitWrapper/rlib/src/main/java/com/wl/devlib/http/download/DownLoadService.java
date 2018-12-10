package com.wl.devlib.http.download;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;


public interface DownLoadService {

    @Streaming
    @GET
    Flowable<ResponseBody> startDownLoad(@Url String fileUrl);

}
