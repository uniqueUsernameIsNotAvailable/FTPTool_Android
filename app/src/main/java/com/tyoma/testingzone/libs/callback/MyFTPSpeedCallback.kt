package com.tyoma.testingzone.libs.callback

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

//callback for speed measurement
abstract class MyFTPSpeedCallback : MyFTPTransferCallback {
    private var startTime: Long = 0
    private var endTime: Long = 0
    private var totalSize: Long = 0
    private var tempTotalSize: Long = 0
    private val executors: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var isFinish = false

    private val calcSpeedTask = Runnable {
        val totalSize1 = totalSize
        val transferredSize = totalSize1 - tempTotalSize
        val speed = transferredSize / 1024.0 / 1000
        var avgSpeed = 0.00
        if (isFinish) {
            val transferredTime = endTime - startTime
            avgSpeed = totalSize1 / 1024.0 / transferredTime / 1000
        }

        onTransferSpeed(isFinish, startTime, endTime, speed, avgSpeed)
        tempTotalSize = totalSize1
    }

    override fun onStateChanged(state: Int) {
        when (state) {
            MyFTPTransferCallback.START -> {
                startTime = System.currentTimeMillis()
                executors.scheduleWithFixedDelay(
                    calcSpeedTask,
                    CALC_TIME,
                    CALC_TIME,
                    TimeUnit.MILLISECONDS
                )
            }

            MyFTPTransferCallback.ERROR, MyFTPTransferCallback.COMPLETED, MyFTPTransferCallback.ABORTED -> {
                isFinish = true
                endTime = System.currentTimeMillis()
                executors.shutdown()
            }

            else -> {}
        }
    }

    override fun onTransferDone(fileSize: Long, resultSize: Int) {
        totalSize += resultSize.toLong()
    }

    override fun onErr(code: Int, msg: String) {
    }

    abstract fun onTransferSpeed(
        isFinished: Boolean,
        startTime: Long,
        endTime: Long,
        speed: Double,
        averageSpeed: Double
    )

    companion object {
        private const val CALC_TIME: Long = 1000
    }
}
