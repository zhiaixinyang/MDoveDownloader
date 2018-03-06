package com.suapp.dcdownloader.retrofit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.suapp.dcdownloader.base.BaseRequest;
import com.suapp.dcdownloader.config.FileConfig;
import com.suapp.dcdownloader.config.UrlConfig;
import com.suapp.dcdownloader.network.SURetrofitFactory;
import com.suapp.dcdownloader.retrofit.api.ApiService;
import com.suapp.dcdownloader.retrofit.listener.DcCallback;
import com.suapp.dcdownloader.retrofit.listener.DcDownloadSubscriber;
import com.suapp.dcdownloader.retrofit.model.DownLoadProgress;
import com.suapp.dcdownloader.utils.FileUtils;

import org.reactivestreams.Publisher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by zhaojing on 2018/3/5.
 */

public class DownloadRequest extends BaseRequest<DownloadRequest> {
    private String mFileLocation;
    private String mFileName = FileConfig.DEFAULT_DOWNLOAD_FILE_NAME;
    private Context mContext;

    public DownloadRequest(Context context, String url) {
        mContext = context;
        mFileLocation = FileUtils.getDiskCachePath(mContext);
        downloadUrl(url);
        initNet();
    }

    //设置File存放位置，默认在app的cache目录
    public DownloadRequest setFileSaveLocation(String fileLocation) {
        if (!TextUtils.isEmpty(fileLocation)) {
            mFileLocation = fileLocation;
        }
        return this;
    }

    public DownloadRequest setFileName(String fileName) {
        if (!TextUtils.isEmpty(fileName)) {
            mFileName = fileName;
        }
        return this;
    }

    @Override
    public <T> void request(@NonNull DcCallback<T> callback) {
        execute(callback);
    }

    @Override
    protected Observable execute() {
        return mApiService
                .downFile(mDownLoadUrl, mParams)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .toFlowable(BackpressureStrategy.LATEST)
                .flatMap(new Function<ResponseBody, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(final ResponseBody responseBody) throws Exception {
                        return Flowable.create(new FlowableOnSubscribe<DownLoadProgress>() {
                            @Override
                            public void subscribe(FlowableEmitter<DownLoadProgress> subscriber) throws Exception {
                                File dir = FileUtils.getFileDir(mFileLocation, mFileName);
                                if (!dir.exists()) {
                                    dir.mkdirs();
                                }
                                File file = new File(dir.getPath() + File.separator + mFileName);
                                saveFile(subscriber, file, responseBody);
                            }
                        }, BackpressureStrategy.LATEST);
                    }
                })
                .sample(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable();
    }

    @Override
    protected <T> void execute(@NonNull DcCallback<T> callback) {
        DisposableObserver disposableObserver = new DcDownloadSubscriber(callback);
        execute().subscribe(disposableObserver);
    }

    @Override
    protected void initNet() {
        if (mApiService == null) {
            mApiService = SURetrofitFactory.create(mContext, UrlConfig.getHostApi(), false, ApiService.class);
        }
    }

    private void saveFile(FlowableEmitter<? super DownLoadProgress> sub, File saveFile, ResponseBody resp) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            try {
                int readLen;
                int downloadSize = 0;
                byte[] buffer = new byte[4096];

                DownLoadProgress downProgress = new DownLoadProgress();
                inputStream = resp.byteStream();
                downProgress.setStream(inputStream);
                sub.onNext(downProgress);

                outputStream = new FileOutputStream(saveFile);

                long contentLength = resp.contentLength();
                downProgress.setTotalSize(contentLength);

                while ((readLen = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, readLen);
                    downloadSize += readLen;
                    downProgress.setDownloadSize(downloadSize);
                    sub.onNext(downProgress);
                }
                outputStream.flush();
                downProgress.setDownloadFile(saveFile);
                sub.onComplete();
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (resp != null) {
                    resp.close();
                }
            }
        } catch (IOException e) {
            sub.onError(e);
        }
    }
}
