package com.tyoma.testingzone.libs.main

import android.os.Handler
import android.os.Looper
import com.tyoma.testingzone.libs.callback.MyFTPCallback

// MyFtpCallbackWrapper.kt
class MyFtpCallbackWrapper<E : Any>(private val onMyFtpCallBack: MyFTPCallback<E>?) {
    private val lock = Any()
    private val handler = Handler(Looper.getMainLooper())

    fun onSuccess(response: E?) {
        synchronized(lock) {
            handler.post {
                onMyFtpCallBack?.onSuccess(response)
            }
        }
    }

    fun onFail(code: Int, msg: String) {
        synchronized(lock) {
            handler.post {
                onMyFtpCallBack?.onFail(code, msg)
            }
        }
    }
}