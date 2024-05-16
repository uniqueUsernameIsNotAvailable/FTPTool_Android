package com.tyoma.testingzone.libs.main

import com.tyoma.testingzone.libs.callback.MyFTPCallback
import com.tyoma.testingzone.libs.callback.MyFTPTransferCallback

// MyFtpClient.kt
class MyFtpClient : IMyFtpClient {
    private val ftpClientImpl: IMyFtpClient = MyFtpClientImpl()

    override fun connect(serverIp: String, port: Int, userName: String, password: String) {
        connect(serverIp, port, userName, password, null)
    }

    override fun connect(
        serverIp: String,
        port: Int,
        userName: String,
        password: String,
        callBack: MyFTPCallback<Void>?
    ) {
        ftpClientImpl.connect(serverIp, port, userName, password, callBack)
    }

    override fun disconnect() {
        ftpClientImpl.disconnect()
    }

    override fun disconnect(callBack: MyFTPCallback<Void>?) {
        ftpClientImpl.disconnect(callBack)
    }

    override fun isConnected(): Boolean {
        return ftpClientImpl.isConnected()
    }

    override fun getCurDirFileList(callBack: MyFTPCallback<List<MyFtpFile>>?) {
        ftpClientImpl.getCurDirFileList(callBack)
    }

    override fun getCurDirPath(callBack: MyFTPCallback<String>?) {
        ftpClientImpl.getCurDirPath(callBack)
    }

    override fun changeDirectory(path: String, callBack: MyFTPCallback<String>?) {
        ftpClientImpl.changeDirectory(path, callBack)
    }

    override fun moveUpDir(callBack: MyFTPCallback<String>?) {
        ftpClientImpl.moveUpDir(callBack)
    }

    override fun downloadFile(
        remoteFile: MyFtpFile,
        localFilePath: String,
        callback: MyFTPTransferCallback?
    ) {
        ftpClientImpl.downloadFile(remoteFile, localFilePath, callback)
    }

    override fun uploadFile(localFilePath: String, callback: MyFTPTransferCallback?) {
        ftpClientImpl.uploadFile(localFilePath, callback)
    }

    override fun isCurDirHome(): Boolean {
        return ftpClientImpl.isCurDirHome()
    }

    override fun backToHomeDir(callBack: MyFTPCallback<String>) {
        ftpClientImpl.backToHomeDir(callBack)
    }

    override fun release() {
        ftpClientImpl.release()
    }
}