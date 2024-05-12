package com.tyoma.testingzone.libs.main;

import android.os.Handler;
import android.os.Looper;

import com.tyoma.testingzone.libs.callback.MyFTPCallback;

//callback wrapper, to change the current thread to ui thread.

final class MyFtpCallbackWrapper<E> implements MyFTPCallback<E> {

    private final Object lock = new Object();
    private MyFTPCallback<E> onMyFtpCallBack;
    private Handler handler = new Handler(Looper.getMainLooper());

    public MyFtpCallbackWrapper(MyFTPCallback<E> onMyFtpCallBack) {
        this.onMyFtpCallBack = onMyFtpCallBack;
    }

    @Override
    public void onSuccess(final E response) {
        synchronized (lock) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (onMyFtpCallBack != null) {
                        onMyFtpCallBack.onSuccess(response);
                    }
                }
            });
        }
    }

    @Override
    public void onFail(final int code, final String msg) {
        synchronized (lock) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (onMyFtpCallBack != null) {
                        onMyFtpCallBack.onFail(code, msg);
                    }
                }
            });
        }
    }
}
