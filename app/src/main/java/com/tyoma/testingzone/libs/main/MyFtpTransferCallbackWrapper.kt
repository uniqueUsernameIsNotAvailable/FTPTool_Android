package com.tyoma.testingzone.libs.main

import android.os.Handler
import android.os.Looper
import com.tyoma.testingzone.libs.callback.MyFTPTransferCallback

//transfer data cb wrapper, thread -> ui thread
internal class MyFtpTransferCallbackWrapper(private val callback: MyFTPTransferCallback?) :
    MyFTPTransferCallback {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val lock = Any()

    override fun onStateChanged(state: Int) {
        synchronized(lock) {
            mainHandler.post {
                callback?.onStateChanged(state)
            }
        }
    }

    override fun onTransferDone(fileSize: Long, resultSize: Int) {
        synchronized(lock) {
            mainHandler.post { callback?.onTransferDone(fileSize, resultSize) }
        }
    }

    override fun onErr(code: Int, msg: String) {
        synchronized(lock) {
            mainHandler.post { callback?.onErr(code, msg) }
        }
    }
}
