package com.tyoma.testingzone.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tyoma.testingzone.vm.ServerViewModel

@Composable
fun ServerScreen(
    vModel: ServerViewModel = viewModel()
) {
    val user = rememberSaveable { mutableStateOf(vModel.user.value) }
    val password = rememberSaveable { mutableStateOf(vModel.password.value) }
    val address = rememberSaveable { mutableStateOf(vModel.address.value) }
    val port = rememberSaveable { mutableStateOf(vModel.port.value) }

    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Greeting("Server mode", Modifier.offset(0.dp, (-128).dp))

        DescriptionText(
            "Enter server parameters in the fields below:",
            Modifier.offset(0.dp, (-64).dp)
        )

        TextField(
            value = user.value,
            onValueChange = {
                user.value = it
                vModel.onUserChanged(it)
            },
            label = { Text("User") },
            modifier = Modifier.padding(8.dp)
        )
        TextField(
            value = password.value,
            onValueChange = {
                password.value = it
                vModel.onPasswordChanged(it)
            },
            label = { Text("Password") },
            modifier = Modifier.padding(8.dp)
        )
        TextField(
            value = address.value,
            onValueChange = {
                address.value = it
                vModel.onAddressChanged(it)
            },
            label = { Text("Path") },
            modifier = Modifier.padding(8.dp)
        )
        TextField(
            value = port.value,
            onValueChange = {
                port.value = it
                vModel.onPortChanged(it)
            },
            label = { Text("Port") },
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = vModel::startFtpServer) {
                Text(text = "Start FTP Server")
            }

            Spacer(modifier = Modifier.size(16.dp, 4.dp))

            Button(onClick = {
                vModel.stopFtpServer()
                if (!vModel.ftpServerStarted) {
                    Toast.makeText(context, "No FTP Server is present!", Toast.LENGTH_LONG).show()
                }
            }) {
                Text(text = "Stop FTP Server")
            }
        }
    }
}