package com.tyoma.testingzone.libs.callback

// callback of data transfer
interface MyFTPTransferCallback {
    fun onStateChanged(state: Int)

    fun onTransferDone(fileSize: Long, resultSize: Int)

    fun onErr(code: Int, msg: String)

    companion object {
        const val START: Int = 1
        const val TRANSFER: Int = 2
        const val COMPLETED: Int = 3
        const val ERROR: Int = 4
        const val ABORTED: Int = 5
    }
}
