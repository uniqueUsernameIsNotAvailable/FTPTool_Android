package com.tyoma.testingzone.libs.callback;

// onRequest callback
public interface MyFTPCallback<E> {

    void onSuccess(E response);

    void onFail(int code, String msg);
}
