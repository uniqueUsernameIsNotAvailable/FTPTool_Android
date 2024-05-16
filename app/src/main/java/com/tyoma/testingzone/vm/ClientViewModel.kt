package com.tyoma.testingzone.vm

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.tyoma.testingzone.libs.main.MyFtpFile
import com.tyoma.testingzone.libs.callback.MyFTPSpeedCallback
import com.tyoma.testingzone.libs.callback.MyFTPCallback
import com.tyoma.testingzone.libs.main.MyFtpClient
import com.tyoma.testingzone.ui.SAVE_FILE_PATH
import com.tyoma.testingzone.ui.file
import it.sauronsoftware.ftp4j.FTPException


class ClientViewModel : ViewModel() {
    // State variables
    private val _user = mutableStateOf("")
    val user: State<String> = _user

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _address = mutableStateOf("")
    val address: State<String> = _address

    private val _port = mutableStateOf("")
    val port: State<String> = _port

    private val _ftpClient = MyFtpClient()
    val ftpClient: MyFtpClient = _ftpClient

    private val _ftpServerStarted = mutableStateOf(false)
    val ftpServerStarted: State<Boolean> = _ftpServerStarted

    private val _initialFList = mutableStateOf(emptyList<MyFtpFile>())
    val initialFList: State<List<MyFtpFile>> = _initialFList

    // Functions for updating state
    fun updateUser(newUser: String) {
        _user.value = newUser
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun updateAddress(newAddress: String) {
        _address.value = newAddress
    }

    fun updatePort(newPort: String) {
        _port.value = newPort
    }

    fun updateList(newList: List<MyFtpFile>) {
        _initialFList.value = newList
    }

    fun updateServerStatus(newStatus: Boolean) {
        _ftpServerStarted.value = newStatus
    }

    private fun fetchInitialFileList() {
        ftpClient.getCurDirFileList(object : MyFTPCallback<List<MyFtpFile>> {
            override fun onSuccess(response: List<MyFtpFile>?) {
                _initialFList.value = response ?: emptyList()
            }

            override fun onFail(code: Int, msg: String) {
                // Handle failure to fetch file list
            }
        })
    }
    fun connectToFtpServer() {
        ftpClient.connect(
            address.value,
            port.value.toIntOrNull() ?: 0,
            user.value,
            password.value,
            object : MyFTPCallback<Void> {
                override fun onSuccess(response: Void?) {
                    fetchInitialFileList()
                    _ftpServerStarted.value = true
                }

                override fun onFail(code: Int, msg: String) {
                    // Handle connection failure
                }
            }
        )
    }

    fun changeDirectory(newDirectory: String) {
        ftpClient.changeDirectory(newDirectory, object : MyFTPCallback<String> {
            override fun onSuccess(response: String?) {
                fetchCurrentDirectoryFileList()
            }

            override fun onFail(code: Int, msg: String) {}
        })
    }

    private fun fetchCurrentDirectoryFileList() {
        ftpClient.getCurDirFileList(object : MyFTPCallback<List<MyFtpFile>> {
            override fun onSuccess(response: List<MyFtpFile>?) {
                _initialFList.value = response ?: emptyList()
            }

            override fun onFail(code: Int, msg: String) {}
        })
    }

    fun uploadFile() {
        try {
            ftpClient.uploadFile(
                file.absolutePath,
                object : MyFTPSpeedCallback() {
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
                    }
                }
            )
        } catch (msg: FTPException) {
            Log.e("FFFF", msg.toString())
        }

    }

    fun downloadFile(file: MyFtpFile) {
        val saveLocalPath: String = SAVE_FILE_PATH + "/" + file.name
        ftpClient.downloadFile(
            file,
            saveLocalPath,
            object : MyFTPSpeedCallback() {
                override fun onTransferSpeed(
                    isFinished: Boolean,
                    startTime: Long,
                    endTime: Long,
                    speed: Double,
                    averageSpeed: Double
                ) {
                }

                override fun onStateChanged(state: Int) {
                    super.onStateChanged(state)
                }

                override fun onTransferDone(fileSize: Long, resultSize: Int) {
                    super.onTransferDone(fileSize, resultSize)
                }

                override fun onErr(code: Int, msg: String) {
                    super.onErr(code, msg)
                }
            })
    }
}
