package com.tyoma.testingzone.libs.main;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tyoma.testingzone.libs.callback.MyFTPCallback;
import com.tyoma.testingzone.libs.callback.MyFTPTransferCallback;

import java.util.List;

interface IMyFtpClient {
    void connect(@NonNull String serverIp,
                 @NonNull int port,
                 @NonNull String userName,
                 @NonNull String password);

    void connect(@NonNull String serverIp,
                 @NonNull int port,
                 @NonNull String userName,
                 @NonNull String password,
                 @Nullable MyFTPCallback<Void> callBack);

    void disconnect();

    void disconnect(@Nullable MyFTPCallback<Void> callBack);


    void getCurDirFileList(@Nullable MyFTPCallback<List<MyFtpFile>> callBack);

    void getCurDirPath(@Nullable MyFTPCallback<String> callBack);

    void changeDirectory(@NonNull String path, @Nullable MyFTPCallback<String> callBack);

    void moveUpDir(@Nullable MyFTPCallback<String> callBack);

    void downloadFile(@NonNull MyFtpFile remoteFile, @NonNull String localFilePath, @Nullable MyFTPTransferCallback callback);

    void uploadFile(@NonNull String localFilePath, @Nullable MyFTPTransferCallback callback);

    boolean isConnected();

    boolean isCurDirHome();

    void backToHomeDir(MyFTPCallback<String> callBack);

    void release();
}
