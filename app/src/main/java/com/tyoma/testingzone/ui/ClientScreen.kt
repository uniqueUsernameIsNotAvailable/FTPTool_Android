package com.tyoma.testingzone.ui

import android.content.Context
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
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tyoma.testingzone.R
import com.tyoma.testingzone.libs.main.MyFtpFile.Companion.TYPE_DIRECTORY
import com.tyoma.testingzone.model.FileUploadButton
import com.tyoma.testingzone.model.getRealPathFromURI
import com.tyoma.testingzone.utils.itemInfoBuilder
import com.tyoma.testingzone.utils.transformListToStringForward
import com.tyoma.testingzone.utils.transformStringToList
import com.tyoma.testingzone.vm.ClientViewModel

const val SAVE_FILE_PATH = "/storage/emulated/0"


@Composable
@Preview
fun ClientScreen(vModel: ClientViewModel = viewModel()) {

    val mContext = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Greeting("Client mode")

        Spacer(modifier = Modifier.height(64.dp))

        DescriptionText("Enter connection parameters in the fields below:")

        if (!vModel.ftpClientConnect.value) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { vModel.updateShowDialog(true) }) {
                Text(text = "Connect")
            }
        } else {
            FileListHeader(vModel, mContext)
        }
        if (vModel.showDialog.value) {
            ConnectionDialog(vModel)
        }

        if (vModel.showList.value) {
            FileList(vModel)
        }
    }
}

fun disconnection(vModel: ClientViewModel) {
    vModel.ftpClient.disconnect()
    vModel.updateList(emptyList())
    vModel.updateShowList(false)
    vModel.updateClientStatus(false)
}

@Composable
fun FileListHeader(vModel: ClientViewModel, mContext: Context) {
    Spacer(modifier = Modifier.height(16.dp))

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        FileUploadButton(onFileSelected = { uri ->
            vModel.uploadFile(getRealPathFromURI(mContext, uri!!)!!)
        })

        Button(onClick = {
            if (vModel.ftpClientConnect.value) {
                disconnection(vModel)
            } else Toast.makeText(
                mContext, "No FTP Connection is present!", Toast.LENGTH_LONG
            ).show()
        }) { Text(text = "Disconnect") }
    }
}

@Composable
fun FileList(vModel: ClientViewModel) {
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
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        if (file.type == TYPE_DIRECTORY) {

                            Icon(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .size(36.dp, 36.dp),
                                painter = painterResource(R.drawable.round_folder_24),
                                contentDescription = ""

                            )
                        }
                        Text(text = itemInfoBuilder(file))

                        Button(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            onClick = { vModel.downloadFile(file) }
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


@Composable
fun ConnectionDialog(vModel: ClientViewModel) {
    Dialog(onDismissRequest = { vModel.updateShowDialog(false) }) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                vModel.user.value,
                onValueChange = { vModel.updateUser(it) },
                label = { Text("User") },
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                vModel.password.value,
                onValueChange = { vModel.updatePassword(it) },
                label = { Text("Password") },
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                vModel.address.value,
                onValueChange = { vModel.updateAddress(it) },
                label = { Text("Path") },
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                vModel.port.value,
                onValueChange = { vModel.updatePort(it) },
                label = { Text("Port") },
                modifier = Modifier.padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                vModel.connectToFtpServer()

                vModel.updateShowDialog(false)
                vModel.updateShowList(true)
            }) {
                Text(text = "Connect")
            }
        }
    }
}

