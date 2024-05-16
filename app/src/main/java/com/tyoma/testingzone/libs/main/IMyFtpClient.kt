package com.tyoma.testingzone.libs.main

import com.tyoma.testingzone.libs.callback.MyFTPCallback
import com.tyoma.testingzone.libs.callback.MyFTPTransferCallback


interface IMyFtpClient {
    fun connect(
        serverIp: String,
        port: Int,
        userName: String,
        password: String
    )

    fun connect(
        serverIp: String,
        port: Int,
        userName: String,
        password: String,
        callBack: MyFTPCallback<Void>?
    )

    fun disconnect()

    fun disconnect(callBack: MyFTPCallback<Void>?)

    fun getCurDirFileList(callBack: MyFTPCallback<List<MyFtpFile>>?)

    fun getCurDirPath(callBack: MyFTPCallback<String>?)

    fun changeDirectory(path: String, callBack: MyFTPCallback<String>?)

    fun moveUpDir(callBack: MyFTPCallback<String>?)

    fun downloadFile(
        remoteFile: MyFtpFile,
        localFilePath: String,
        callback: MyFTPTransferCallback?
    )

    fun uploadFile(localFilePath: String, callback: MyFTPTransferCallback?)

    fun isConnected(): Boolean

    fun isCurDirHome(): Boolean

    fun backToHomeDir(callBack: MyFTPCallback<String>)

    fun release()
}