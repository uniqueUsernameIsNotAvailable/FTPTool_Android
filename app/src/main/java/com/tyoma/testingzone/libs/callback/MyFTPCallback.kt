package com.tyoma.testingzone.libs.callback

// onRequest callback
interface MyFTPCallback<E> {
    fun onSuccess(response: E?)
    fun onFail(code: Int, msg: String)
}
