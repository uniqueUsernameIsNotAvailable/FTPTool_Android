package com.tyoma.testingzone.model

import android.util.Log
import com.tyoma.testingzone.libs.EZFtpClient
import com.tyoma.testingzone.libs.EZFtpFile
import com.tyoma.testingzone.libs.callback.EZFtpTransferSpeedCallback
import com.tyoma.testingzone.ui.SAVE_FILE_PATH
import com.tyoma.testingzone.ui.file
import it.sauronsoftware.ftp4j.FTPException

fun uploadFile(ftpClient: EZFtpClient) {
    try {
        ftpClient.uploadFile(
            file.absolutePath,
            object : EZFtpTransferSpeedCallback() {
                override fun onTransferSpeed(
                    isFinished: Boolean,
                    startTime: Long,
                    endTime: Long,
                    speed: Double,
                    averageSpeed: Double
                ) {
                    if (isFinished) {

                        Log.d("TG", "TRANSMISSION DONE")
                    }
                    // Handle transfer speed updates if needed
                    // You can update a progress indicator or show a toast message here
                }
            }
        )
    } catch (msg: FTPException) {
        Log.e("FFFF", msg.toString())
    }

}

fun downloadFile(ftpClient: EZFtpClient, file: EZFtpFile) {
    val saveLocalPath: String = SAVE_FILE_PATH + "/" + file.name
    ftpClient.downloadFile(
        file,
        saveLocalPath,
        object : EZFtpTransferSpeedCallback() {
            override fun onTransferSpeed(
                isFinished: Boolean,
                startTime: Long,
                endTime: Long,
                speed: Double,
                averageSpeed: Double
            ) {
                //Async callback
                //UI Thread
            }

            override fun onStateChanged(state: Int) {
                super.onStateChanged(state)
            }

            override fun onTransferred(fileSize: Long, transferredSize: Int) {
                super.onTransferred(fileSize, transferredSize)
            }

            override fun onErr(code: Int, msg: String) {
                super.onErr(code, msg)
            }
        })
}