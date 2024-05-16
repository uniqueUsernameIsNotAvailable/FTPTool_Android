package com.tyoma.testingzone.libs.main

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.tyoma.testingzone.libs.callback.MyFTPCallback
import com.tyoma.testingzone.libs.callback.MyFTPTransferCallback
import com.tyoma.testingzone.libs.exceptions.MyFtpNoInitExc
import it.sauronsoftware.ftp4j.FTPAbortedException
import it.sauronsoftware.ftp4j.FTPClient
import it.sauronsoftware.ftp4j.FTPDataTransferException
import it.sauronsoftware.ftp4j.FTPDataTransferListener
import it.sauronsoftware.ftp4j.FTPException
import it.sauronsoftware.ftp4j.FTPIllegalReplyException
import it.sauronsoftware.ftp4j.FTPListParseException
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneId

class MyFtpClientImpl : IMyFtpClient {
    private val TAG = "MyFtpClientImpl"
    private val HOME_DIR = "/"

    private var ftpClient: FTPClient? = null
    private val taskThread = HandlerThread("ftp-task")
    private var taskHandler: Handler? = null
    private val lock = Any()
    private var isInit = false
    private var curDirPath: String? = null

    init {
        init()
    }

    private fun setCurDirPath(path: String) {
        synchronized(lock) {
            this.curDirPath = path
        }
    }

    // init client
    private fun init() {
        synchronized(lock) {
            // init work thread
            val temp = taskThread
            if (!temp.isAlive) {
                temp.start()
                taskHandler = Handler(temp.looper)
            }
            // create ftp client object
            ftpClient = FTPClient()
            ftpClient?.setPassive(true)
            ftpClient?.setType(FTPClient.TYPE_BINARY)
            isInit = true
        }
    }

    // release client
    override fun release() {
        synchronized(lock) {
            // disconnect if it is currently connected
            if (ftpClient != null && isConnected()) {
                disconnect()
            }
            // release work thread
            val temp = taskThread
            if (temp.isAlive) {
                temp.quit()
            }
            // clear message queue
            taskHandler?.removeCallbacksAndMessages(null)
            isInit = false
        }
    }

    private fun checkInit() {
        if (!isInit) {
            throw MyFtpNoInitExc("MyFTPClient: not init/released！")
        }
    }

    private fun getPrevPath(): String? {
        if (curDirPath.isNullOrEmpty()) {
            return null
        }

        // if cur path is home dir, return
        // Because it can't go back to the previous level
        if (curDirPath == HOME_DIR) {
            return HOME_DIR
        }

        // get last index
        val lastIndex = curDirPath!!.lastIndexOf("/")
        return if (lastIndex == 0) {
            HOME_DIR
        } else {
            curDirPath!!.substring(0, lastIndex)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <E : Any> callbackNormalSuccess(callBack: MyFTPCallback<E>?, response: E?) {
        val wrapper = MyFtpCallbackWrapper(callBack)
        wrapper.onSuccess(response)
    }

    @Suppress("UNCHECKED_CAST")
    private fun callbackNormalFail(callBack: MyFTPCallback<*>?, code: Int, msg: String) {
        val wrapper = MyFtpCallbackWrapper(callBack)
        wrapper.onFail(code, msg)
    }

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
        checkInit()
        Log.d(
            TAG,
            "connect ftp server : serverIp = $serverIp,port = $port,user = $userName,pw = $password"
        )
        taskHandler?.post {
            try {
                ftpClient?.connect(serverIp, port)
                ftpClient?.login(userName, password)
                getCurDirPath(null)
                callbackNormalSuccess(callBack, null)
            } catch (e: IOException) {
                e.printStackTrace()
                callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, "IOException")
            } catch (e: FTPIllegalReplyException) {
                callbackNormalFail(
                    callBack,
                    MyFtpResultCode.RESULT_FAIL,
                    "Read server response fail!"
                )
            } catch (e: FTPException) {
                callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.message!!)
            } catch (e: IllegalStateException) {
                callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.message!!)
            }
        }
    }

    override fun disconnect() {
        disconnect(null)
    }

    override fun disconnect(callBack: MyFTPCallback<Void>?) {
        checkInit()
        taskHandler?.post {
            try {
                ftpClient?.disconnect(true)
                callbackNormalSuccess(callBack, null)
                release()
            } catch (e: IOException) {
                callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, "IOException")
            } catch (e: FTPIllegalReplyException) {
                callbackNormalFail(
                    callBack,
                    MyFtpResultCode.RESULT_FAIL,
                    "Read server response fail!"
                )
            } catch (e: FTPException) {
                callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.message!!)
            } catch (e: IllegalStateException) {
                callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.message!!)
            }
        }
    }

    override fun isConnected(): Boolean {
        return ftpClient != null && ftpClient?.isConnected == true
    }

    override fun getCurDirFileList(callBack: MyFTPCallback<List<MyFtpFile>>?) {
        checkInit()
        taskHandler?.post {
            try {
                val ftpFiles = ftpClient?.list()
                val myFtpFiles = ftpFiles?.map { ftpFile ->
                    val dt = LocalDateTime.ofInstant(
                        ftpFile.modifiedDate.toInstant(),
                        ZoneId.systemDefault()
                    )
                    MyFtpFile(
                        ftpFile.name,
                        curDirPath,
                        ftpFile.type,
                        ftpFile.size,
                        dt
                    )
                }?.toList() ?: emptyList()
                callbackNormalSuccess(callBack, myFtpFiles)
            } catch (e: IOException) {
                e.printStackTrace()
                callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, "IOException")
            } catch (e: FTPIllegalReplyException) {
                callbackNormalFail(
                    callBack,
                    MyFtpResultCode.RESULT_FAIL,
                    "Read server response fail!"
                )
            } catch (e: FTPException) {
                callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.message!!)
            } catch (e: IllegalStateException) {
                callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.message!!)
            } catch (e: FTPDataTransferException) {
                callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.message!!)
            } catch (e: FTPAbortedException) {
                callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.message!!)
            } catch (e: FTPListParseException) {
                callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.message!!)
            }
        }
    }

    override fun getCurDirPath(callBack: MyFTPCallback<String>?) {
        checkInit()
        taskHandler?.post {
            try {
                val path = ftpClient?.currentDirectory()
                setCurDirPath(path!!)
                callbackNormalSuccess(callBack, path)
            } catch (e: IOException) {
                callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, "IOException")
            } catch (e: FTPIllegalReplyException) {
                callbackNormalFail(
                    callBack,
                    MyFtpResultCode.RESULT_FAIL,
                    "Read server response fail!"
                )
            } catch (e: FTPException) {
                callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.message!!)
            } catch (e: IllegalStateException) {
                callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.message!!)
            }
        }
    }

    override fun changeDirectory(path: String, callBack: MyFTPCallback<String>?) {
        checkInit()
        taskHandler?.post {
            try {
                if (path.isEmpty()) {
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_FAIL, "path is empty!")
                } else {
                    ftpClient?.changeDirectory(path)
                    setCurDirPath(path)
                    callbackNormalSuccess(callBack, path)
                }
            } catch (e: IOException) {
                callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, "IOException")
            } catch (e: FTPIllegalReplyException) {
                callbackNormalFail(
                    callBack,
                    MyFtpResultCode.RESULT_FAIL,
                    "Read server response fail!"
                )
            } catch (e: FTPException) {
                callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.message!!)
            } catch (e: IllegalStateException) {
                callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.message!!)
            }
        }
    }

    override fun moveUpDir(callBack: MyFTPCallback<String>?) {
        changeDirectory(getPrevPath()!!, callBack)
    }

    override fun backToHomeDir(callBack: MyFTPCallback<String>) {
        changeDirectory(HOME_DIR, callBack)
    }

    override fun downloadFile(
        remoteFile: MyFtpFile,
        localFilePath: String,
        callback: MyFTPTransferCallback?
    ) {
        checkInit()

        val localFile = File(localFilePath)
        val callbackWrapper = MyFtpTransferCallbackWrapper(callback)

        taskHandler?.post {
            try {
                ftpClient?.download(remoteFile.name, localFile, object : FTPDataTransferListener {
                    override fun started() {
                        callbackWrapper.onStateChanged(MyFTPTransferCallback.START)
                    }

                    override fun transferred(i: Int) {
                        callbackWrapper.onTransferDone(remoteFile.size, i)
                    }

                    override fun completed() {
                        callbackWrapper.onStateChanged(MyFTPTransferCallback.COMPLETED)
                    }

                    override fun aborted() {
                        callbackWrapper.onStateChanged(MyFTPTransferCallback.ABORTED)
                    }

                    override fun failed() {
                        callbackWrapper.onStateChanged(MyFTPTransferCallback.ERROR)
                        callbackWrapper.onErr(MyFtpResultCode.RESULT_FAIL, "Download file fail!")
                    }
                })
            } catch (e: IOException) {
                callbackWrapper.onStateChanged(MyFTPTransferCallback.ERROR)
                callbackWrapper.onErr(MyFtpResultCode.RESULT_EXCEPTION, "IOException")
            } catch (e: FTPIllegalReplyException) {
                callbackWrapper.onStateChanged(MyFTPTransferCallback.ERROR)
                callbackWrapper.onErr(
                    MyFtpResultCode.RESULT_EXCEPTION,
                    "Read server response fail!"
                )
            } catch (e: FTPException) {
                callbackWrapper.onStateChanged(MyFTPTransferCallback.ERROR)
                callbackWrapper.onErr(MyFtpResultCode.RESULT_EXCEPTION, e.message!!)
            } catch (e: FTPDataTransferException) {
                callbackWrapper.onStateChanged(MyFTPTransferCallback.ERROR)
                callbackWrapper.onErr(MyFtpResultCode.RESULT_EXCEPTION, e.message!!)
            } catch (e: FTPAbortedException) {
                callbackWrapper.onStateChanged(MyFTPTransferCallback.ABORTED)
            }
        }
    }

    override fun uploadFile(localFilePath: String, callback: MyFTPTransferCallback?) {
        checkInit()

        val localFile = File(localFilePath)
        val callbackWrapper = MyFtpTransferCallbackWrapper(callback)

        taskHandler?.post {
            try {
                ftpClient?.upload(localFile, object : FTPDataTransferListener {
                    override fun started() {
                        callbackWrapper.onStateChanged(MyFTPTransferCallback.START)
                    }

                    override fun transferred(i: Int) {
                        callbackWrapper.onTransferDone(localFile.length(), i)
                    }

                    override fun completed() {
                        callbackWrapper.onStateChanged(MyFTPTransferCallback.COMPLETED)
                    }

                    override fun aborted() {
                        callbackWrapper.onStateChanged(MyFTPTransferCallback.ABORTED)
                    }

                    override fun failed() {
                        callbackWrapper.onStateChanged(MyFTPTransferCallback.ERROR)
                        callbackWrapper.onErr(MyFtpResultCode.RESULT_FAIL, "Download file fail!")
                    }
                })
            } catch (e: IOException) {
                callbackWrapper.onStateChanged(MyFTPTransferCallback.ERROR)
                callbackWrapper.onErr(MyFtpResultCode.RESULT_EXCEPTION, "IOException")
            } catch (e: FTPIllegalReplyException) {
                callbackWrapper.onStateChanged(MyFTPTransferCallback.ERROR)
                callbackWrapper.onErr(
                    MyFtpResultCode.RESULT_EXCEPTION,
                    "Read server response fail!"
                )
            } catch (e: FTPException) {
                callbackWrapper.onStateChanged(MyFTPTransferCallback.ERROR)
                callbackWrapper.onErr(MyFtpResultCode.RESULT_EXCEPTION, e.message!!)
            } catch (e: FTPDataTransferException) {
                callbackWrapper.onStateChanged(MyFTPTransferCallback.ERROR)
                callbackWrapper.onErr(MyFtpResultCode.RESULT_EXCEPTION, e.message!!)
            } catch (e: FTPAbortedException) {
                callbackWrapper.onStateChanged(MyFTPTransferCallback.ABORTED)
            }
        }
    }

    override fun isCurDirHome(): Boolean {
        return curDirPath == HOME_DIR
    }
}