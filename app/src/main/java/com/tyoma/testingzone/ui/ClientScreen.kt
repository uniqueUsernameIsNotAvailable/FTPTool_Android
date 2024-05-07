package com.tyoma.testingzone.ui

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tyoma.testingzone.libs.EZFtpClient
import com.tyoma.testingzone.libs.EZFtpFile
import com.tyoma.testingzone.libs.callback.OnEZFtpCallBack
import com.tyoma.testingzone.model.downloadFile
import com.tyoma.testingzone.model.uploadFile
import java.io.File

const val SAVE_FILE_PATH = "/storage/emulated/0"



val catURI: Uri = Uri.parse("file:///storage/emulated/0/test.txt")

val file = File(catURI.path!!)

@Composable
@Preview
fun ClientScreen() {
    var user by remember { mutableStateOf("") }
    var pswd by remember { mutableStateOf("") }
    var addr by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("") }

    val ftpClient = remember { EZFtpClient() }

    var ftpServerStarted by remember { mutableStateOf(false) }
    var fList by remember { mutableStateOf(emptyList<EZFtpFile>()) }

    val mContext = LocalContext.current


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Greeting("Client mode")

        Spacer(modifier = Modifier.height(64.dp))

        DescriptionText("Enter connection parameters in the fields below:")

        TextField(
            user,
            onValueChange = { user = it },
            label = { Text("User") },
            modifier = Modifier.padding(8.dp)
        )
        TextField(
            pswd,
            onValueChange = { pswd = it },
            label = { Text("Password") },
            modifier = Modifier.padding(8.dp)
        )
        TextField(
            addr,
            onValueChange = { addr = it },
            label = { Text("Path") },
            modifier = Modifier.padding(8.dp)
        )
        TextField(
            port,
            onValueChange = { port = it },
            label = { Text("Port") },
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))



        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Button(onClick = {
                ftpClient.connect(addr, port.toInt(), user, pswd, object : OnEZFtpCallBack<Void?> {
                    override fun onSuccess(response: Void?) {
                        ftpClient.getCurDirFileList(object : OnEZFtpCallBack<List<EZFtpFile>?> {
                            override fun onSuccess(response: List<EZFtpFile>?) {
                                fList = response ?: emptyList()
                            }

                            override fun onFail(code: Int, msg: String) {}
                        })
                        ftpServerStarted = true
                    }

                    override fun onFail(code: Int, msg: String) {}
                })
            }) {
                Text(text = "Connect")
            }


            Button(onClick = { uploadFile(ftpClient) }) {
                Text(text = "Upload File")
            }

            Button(onClick = {
                if (ftpServerStarted) {
                    ftpClient.disconnect()
                    ftpServerStarted = false
                } else Toast.makeText(
                    mContext, "No FTP Connection is present!", Toast.LENGTH_LONG
                ).show()
            }) { Text(text = "Disconnect") }
        }

        FilesList(filesList = fList, ftpClient = ftpClient)
    }
}

@Composable
fun FilesList(filesList: List<EZFtpFile>, ftpClient: EZFtpClient) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    var state by remember { mutableStateOf(emptyList<EZFtpFile>()) }
    state = filesList

    LazyColumn {
        items(state) { file ->
            Card(
                onClick = {
                    ftpClient.changeDirectory( file.remotePath + file.name + '/', object : OnEZFtpCallBack<String> {
                        override fun onSuccess(response: String) {
                            ftpClient.getCurDirFileList(object : OnEZFtpCallBack<List<EZFtpFile>?> {
                                override fun onSuccess(response: List<EZFtpFile>?) {
                                    state = response ?: emptyList()
                                    Log.d("TAG", "TRANSPORTED $response")
                                }

                                override fun onFail(code: Int, msg: String) {}
                            })


                        }

                        override fun onFail(code: Int, msg: String?) {
                            Log.d("TAG", " DIED $code $msg" )
                        }

                    })
                }, Modifier.padding(4.dp, 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(screenWidth - 16.dp, 96.dp)
                        .padding(8.dp)
                ) {
                    Text(
                        text = buildString {
                            val fType = file.type
                            append(file.remotePath)
                            appendLine(file.name + " ")
                            append("Type: " + if (fType == 1) "Folder " else "File ")
                            if (fType == 0) {
                                append("= " + file.size.toString() + " Bytes")
                            }
                            appendLine(" ")
                            append("Modified: ")
                            append(file.modifiedDate.toLocalDate().toString() + " ")
                            append(file.modifiedDate.toLocalTime())
                        })
                    Button(
                        onClick = { downloadFile(ftpClient, file) },
                        Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text(
                            text = "Get"
                        )
                    }
                }
            }
        }
    }
}