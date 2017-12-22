package com.suapp.dcdownloader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.suapp.dcdownloader.listener.DownloadStatusAdapterListener;
import com.suapp.dcdownloader.model.Request;
import com.suapp.dcdownloader.service.DownLoaderService;

import java.io.File;

import static com.suapp.dcdownloader.service.DownLoaderService.EXTRA_FILE_FINISHED_LENGTH;

public class MainActivity extends AppCompatActivity {
    private Request mRequest;
    private final static String TAG = "MainActivity";
    private static final String URL = "http://47.94.132.220/build/Collage/collage-release-v1.1.8-20-201712201727.apk";
    private static final String FILE_NAME = "collage-release-v1.1.8-20-201712201727.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRequest = new Request.Builder()
                .setFileName(FILE_NAME)
                .setFileUrl(URL)
                .setFileLocation(MainActivity.this)
                .setThreadCount(3)
                .builder();
    }

    public void aaaa(View view) {
        DownloadManager.setDownloadStatusListener(new DownloadStatusAdapterListener() {
            @Override
            public void onPercentProgress(int percent) {
                Log.d(TAG, percent+"%");
            }
        });
        DownloadManager.download(this, mRequest);
    }


}
