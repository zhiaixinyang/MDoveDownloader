package com.suapp.dcdownloader.system.utils;

import android.drm.DrmConvertedStatus;
import android.drm.DrmManagerClient;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;

import com.google.gson.internal.Streams;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownServiceException;
import java.util.Arrays;

import static android.drm.DrmConvertedStatus.STATUS_OK;
import static android.system.OsConstants.SEEK_SET;

/**
 * Created by zhaojing on 2018/3/22.
 */

public class DrmOutputStream  extends OutputStream {
    private static final String TAG = "DrmOutputStream";
    private final DrmManagerClient mClient;
    private final ParcelFileDescriptor mPfd;
    private final FileDescriptor mFd;
    private int mSessionId = INVALID_SESSION;

    /**
     * @param pfd Opened with "rw" mode.
     */
    public DrmOutputStream(DrmManagerClient client, ParcelFileDescriptor pfd, String mimeType)
            throws IOException {
        mClient = client;
        mPfd = pfd;
        mFd = pfd.getFileDescriptor();
        mSessionId = mClient.openConvertSession(mimeType);
        if (mSessionId == INVALID_SESSION) {
            throw new UnknownServiceException("Failed to open DRM session for " + mimeType);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void finish() throws IOException {
        final DrmConvertedStatus status = mClient.closeConvertSession(mSessionId);
        if (status.statusCode == STATUS_OK) {
            try {
                Os.lseek(mFd, status.offset, SEEK_SET);
            } catch (ErrnoException e) {
                e.rethrowAsIOException();
            }
            IoBridge.write(mFd, status.convertedData, 0, status.convertedData.length);
            mSessionId = INVALID_SESSION;
        } else {
            throw new IOException("Unexpected DRM status: " + status.statusCode);
        }
    }

    @Override
    public void close() throws IOException {
        if (mSessionId == INVALID_SESSION) {
            Log.w(TAG, "Closing stream without finishing");
        }
        mPfd.close();
    }

    @Override
    public void write(byte[] buffer, int offset, int count) throws IOException {
        Arrays.checkOffsetAndCount(buffer.length, offset, count);
        final byte[] exactBuffer;
        if (count == buffer.length) {
            exactBuffer = buffer;
        } else {
            exactBuffer = new byte[count];
            System.arraycopy(buffer, offset, exactBuffer, 0, count);
        }
        final DrmConvertedStatus status = mClient.convertData(mSessionId, exactBuffer);
        if (status.statusCode == STATUS_OK) {
            IoBridge.write(mFd, status.convertedData, 0, status.convertedData.length);
        } else {
            throw new IOException("Unexpected DRM status: " + status.statusCode);
        }
    }

    @Override
    public void write(int oneByte) throws IOException {
        Streams.writeSingleByte(this, oneByte);
    }
}
