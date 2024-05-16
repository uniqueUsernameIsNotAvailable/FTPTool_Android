package com.tyoma.testingzone.vm

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

import com.tyoma.testingzone.libs.main.MyFtpServer
import com.tyoma.testingzone.libs.user.MyFtpUser
import com.tyoma.testingzone.libs.user.MyFtpUserPerm


class ServerViewModel : ViewModel() {
    private val _user = mutableStateOf("")
    val user: State<String> = _user

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _address = mutableStateOf("")
    val address: State<String> = _address

    private val _port = mutableStateOf("")
    val port: State<String> = _port

    private var ftpUser: MyFtpUser? = null
    private var ftpServer: MyFtpServer? = null
    var ftpServerStarted = false

    fun onUserChanged(newUser: String) {
        _user.value = newUser
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
    }

    fun onAddressChanged(newAddress: String) {
        _address.value = newAddress
    }

    fun onPortChanged(newPort: String) {
        _port.value = newPort
    }

    fun startFtpServer() {
        if (!ftpServerStarted) {
            ftpUser = MyFtpUser(
                user.value,
                password.value,
                address.value,
                MyFtpUserPerm.WRITE
            )
            ftpServer = MyFtpServer.Builder()
                .addUser(ftpUser!!)
                .setListenPort(port.value.toIntOrNull() ?: 1234)
                .create()
            ftpServer?.start()
            ftpServerStarted = true
        }
    }

    fun stopFtpServer() {
        if (ftpServerStarted) {
            ftpServer?.stop()
            ftpServerStarted = false
        }
    }
}