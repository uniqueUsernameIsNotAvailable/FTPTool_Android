package com.tyoma.testingzone.libs.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tyoma.testingzone.libs.callback.MyFTPCallback;
import com.tyoma.testingzone.libs.callback.MyFTPTransferCallback;

import java.util.List;

public final class MyFtpClient implements IMyFtpClient {

    private static final String TAG = "MyFtpClient";

    private IMyFtpClient ftpClientIml;

    public MyFtpClient() {
        ftpClientIml = new MyFtpClientImpl();
    }

    @Override
    public void connect(@NonNull String serverIp, @NonNull int port, @NonNull String userName, @NonNull String password) {
        connect(serverIp, port, userName, password, null);
    }

    @Override
    public void connect(@NonNull String serverIp, @NonNull int port, @NonNull String userName, @NonNull String password, @Nullable MyFTPCallback<Void> callBack) {
        ftpClientIml.connect(serverIp, port, userName, password, callBack);
    }

    @Override
    public void disconnect() {
        ftpClientIml.disconnect();
    }

    @Override
    public void disconnect(@Nullable MyFTPCallback<Void> callBack) {
        ftpClientIml.disconnect(callBack);
    }

    @Override
    public boolean isConnected() {
        return ftpClientIml.isConnected();
    }

    @Override
    public void getCurDirFileList(@Nullable MyFTPCallback<List<MyFtpFile>> callBack) {
        ftpClientIml.getCurDirFileList(callBack);
    }

    @Override
    public void getCurDirPath(@Nullable MyFTPCallback<String> callBack) {
        ftpClientIml.getCurDirPath(callBack);
    }

    @Override
    public void changeDirectory(@NonNull String path, @Nullable MyFTPCallback<String> callBack) {
        ftpClientIml.changeDirectory(path, callBack);
    }

    @Override
    public void moveUpDir(@Nullable MyFTPCallback<String> callBack) {
        ftpClientIml.moveUpDir(callBack);
    }

    @Override
    public void downloadFile(@NonNull MyFtpFile remoteFile, @NonNull String localFilePath, @Nullable MyFTPTransferCallback callback) {
        ftpClientIml.downloadFile(remoteFile, localFilePath, callback);
    }

    @Override
    public void uploadFile(@NonNull String localFilePath, @Nullable MyFTPTransferCallback callback) {
        ftpClientIml.uploadFile(localFilePath, callback);
    }

    @Override
    public boolean isCurDirHome() {
        return ftpClientIml != null && ftpClientIml.isCurDirHome();
    }

    @Override
    public void backToHomeDir(MyFTPCallback<String> callBack) {
        ftpClientIml.backToHomeDir(callBack);
    }

    @Override
    public void release() {
        ftpClientIml.release();
    }
}
