package com.tyoma.testingzone.libs.main

// IMyFtpServer.kt
interface IMyFtpServer {
    fun start()
    fun stop()
    fun isStopped(): Boolean
}