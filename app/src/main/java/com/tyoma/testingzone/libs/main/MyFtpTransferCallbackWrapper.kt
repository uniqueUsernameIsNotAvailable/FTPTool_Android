package com.tyoma.testingzone.libs.main;

import android.os.Handler;
import android.os.Looper;

import com.tyoma.testingzone.libs.callback.MyFTPTransferCallback;

//transfer data cb wrapper, thread -> ui thread
final class MyFtpTransferCallbackWrapper implements MyFTPTransferCallback {

    private MyFTPTransferCallback callback;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Object lock = new Object();

    public MyFtpTransferCallbackWrapper(MyFTPTransferCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onStateChanged(final int state) {
        synchronized (lock) {
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onStateChanged(state);
                }
            });
        }
    }

    @Override
    public void onTransferDone(final long fileSize, final int resultSize) {
        synchronized (lock) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (callback != null) {
                        callback.onTransferDone(fileSize, resultSize);
                    }
                }
            });
        }
    }

    @Override
    public void onErr(final int code, final String msg) {
        synchronized (lock) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (callback != null) {
                        callback.onErr(code, msg);
                    }
                }
            });
        }
    }
}
