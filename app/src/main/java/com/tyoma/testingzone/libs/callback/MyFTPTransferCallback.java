package com.tyoma.testingzone.libs.callback;

// callback of data transfer
public interface MyFTPTransferCallback {
    int START = 1;
    int TRANSFER = 2;
    int COMPLETED = 3;
    int ERROR = 4;
    int ABORTED = 5;

    void onStateChanged(int state);

    void onTransferDone(long fileSize, int resultSize);

    void onErr(int code, String msg);
}
