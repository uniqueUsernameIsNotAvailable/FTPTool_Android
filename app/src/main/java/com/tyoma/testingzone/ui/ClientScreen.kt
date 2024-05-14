package com.tyoma.testingzone.ui

import android.net.Uri
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tyoma.testingzone.vm.ClientViewModel
import com.tyoma.testingzone.utils.itemInfoBuilder
import com.tyoma.testingzone.utils.transformListToStringForward
import com.tyoma.testingzone.utils.transformStringToList
import java.io.File

const val SAVE_FILE_PATH = "/storage/emulated/0"

val catURI: Uri = Uri.parse("file:///storage/emulated/0/test.txt")

val file = File(catURI.path!!)

@Composable
@Preview
fun ClientScreen(vModel: ClientViewModel = viewModel()) {
    var showDialog by remember { mutableStateOf(false) }
    var showList by remember { mutableStateOf(false) }

    val mContext = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Greeting("Client mode")

        Spacer(modifier = Modifier.height(64.dp))

        DescriptionText("Enter connection parameters in the fields below:")

        if (!vModel.ftpServerStarted.value) {
            Button(onClick = { showDialog = true }) {
                Text(text = "Connect")
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                Button(onClick = { vModel.uploadFile() }) {
                    Text(text = "Upload File")
                }

                Button(onClick = {
                    if (vModel.ftpServerStarted.value) {
                        vModel.ftpClient.disconnect()
                        vModel.updateList(emptyList())
                        vModel.updateServerStatus(false)
                    } else Toast.makeText(
                        mContext, "No FTP Connection is present!", Toast.LENGTH_LONG
                    ).show()
                }) { Text(text = "Disconnect") }
            }
        }
        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        vModel.user.value,
                        onValueChange = { vModel.updateUser(it) },
                        label = { Text("User") },
                        modifier = Modifier.padding(8.dp)
                    )
                    TextField(
                        vModel.password.value,
                        onValueChange = { vModel.updatePassword(it) },
                        label = { Text("Password") },
                        modifier = Modifier.padding(8.dp)
                    )
                    TextField(
                        vModel.address.value,
                        onValueChange = { vModel.updateAddress(it) },
                        label = { Text("Path") },
                        modifier = Modifier.padding(8.dp)
                    )
                    TextField(
                        vModel.port.value,
                        onValueChange = { vModel.updatePort(it) },
                        label = { Text("Port") },
                        modifier = Modifier.padding(8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = {
                        vModel.connectToFtpServer()
                        showDialog = false
                        showList = true
                    }) {
                        Text(text = "Connect")
                    }
                }
            }
        }

        if (showList) {
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp

            var curDir by remember { mutableStateOf(listOf("/")) }

            Button(onClick = {
                val prevDir = transformListToStringForward(curDir, curDir.size - (2 + 1))
                curDir = transformStringToList(prevDir)

                vModel.changeDirectory(prevDir)
            }) {
                Text(
                    text = "Up dir"
                )
            }

            Text(text = transformListToStringForward(curDir, curDir.size - 1))

            LazyColumn(Modifier.padding(8.dp)) {
                items(vModel.initialFList.value) { file ->
                    Card(
                        onClick = {
                            vModel.changeDirectory(file.remotePath + file.name + '/')
                            curDir = transformStringToList(file.remotePath + file.name + '/')
                        }, Modifier.padding(4.dp, 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(screenWidth - 16.dp, 96.dp)
                                .padding(8.dp)
                        ) {
                            Text(text = itemInfoBuilder(file))
                            Button(
                                onClick = { vModel.downloadFile(file) },
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
    }
}