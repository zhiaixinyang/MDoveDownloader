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
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownLoaderService.ACTION_START_DOWNLOAD_FILE);
        intentFilter.addAction(DownLoaderService.ACTION_START_INIT_DOWNLOAD_FILE);
        intentFilter.addAction(DownLoaderService.ACTION_ERROR_INIT_DOWNLOAD_FILE);
        intentFilter.addAction(DownLoaderService.ACTION_ERROR_DOWNLOAD_FILE);
        intentFilter.addAction(DownLoaderService.ACTION_UPDATE_PROGRESS_DOWNLOAD_FILE);
        intentFilter.addAction(DownLoaderService.ACTION_SUCCESS_DOWNLOAD_FILE);
        intentFilter.addAction(DownLoaderService.ACTION_DOWNLOAD_IS_START);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    public void aaaa(View view) {
        DownLoaderService.startDownLoad(this, mRequest);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case DownLoaderService.ACTION_DOWNLOAD_IS_START: {
                    Log.d(TAG, "下载已经开始");
                    break;
                }
                case DownLoaderService.ACTION_START_DOWNLOAD_FILE: {
                    Log.d(TAG, "开始下载文件");
                    break;
                }
                case DownLoaderService.ACTION_START_INIT_DOWNLOAD_FILE: {
                    Log.d(TAG, "开始初始化文件，准备平分任务开始下载");
                    break;
                }
                case DownLoaderService.ACTION_SUCCESS_DOWNLOAD_FILE: {
                    Log.d(TAG, "下载成功");
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    File apkFile = new File(mRequest.getFileLocation() + "/" + mRequest.getFileName());
                    install.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                    startActivity(install);
                    break;
                }
                case DownLoaderService.ACTION_UPDATE_PROGRESS_DOWNLOAD_FILE: {
                    long finish = intent.getLongExtra(EXTRA_FILE_FINISHED_LENGTH, 0);
                    Log.d(TAG, finish + "某线程的下载进度：" + finish);
                    break;
                }
                case DownLoaderService.ACTION_ERROR_DOWNLOAD_FILE: {
                    Log.d(TAG, "文件下载失败");
                    break;
                }
                case DownLoaderService.ACTION_ERROR_INIT_DOWNLOAD_FILE: {
                    Log.d(TAG, "文件初始化失败");
                    break;
                }
            }
        }
    };

}
