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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tyoma.testingzone.model.getLocalIpAddress
import com.tyoma.testingzone.vm.ServerViewModel

@Composable
fun ServerScreen(
    vModel: ServerViewModel = viewModel()
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Greeting("Server mode", Modifier.offset(0.dp, (-128).dp))

        DescriptionText(
            "Enter server parameters in the fields below:", Modifier.offset(0.dp, (-64).dp)
        )

        TextField(value = vModel.user.value, onValueChange = {
            vModel.onUserChanged(it)
        }, label = { Text("User") }, modifier = Modifier.padding(8.dp)
        )
        TextField(value = vModel.password.value, onValueChange = {
            vModel.onPasswordChanged(it)
        }, label = { Text("Password") }, modifier = Modifier.padding(8.dp)
        )
        TextField(value = vModel.address.value, onValueChange = {
            vModel.onAddressChanged(it)
        }, label = { Text("Path") }, modifier = Modifier.padding(8.dp)
        )
        TextField(value = vModel.port.value, onValueChange = {
            vModel.onPortChanged(it)
        }, label = { Text("Port") }, modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        val ip: String = getLocalIpAddress()!!
        Text(text = "Server will be hosted on: $ip")
        Spacer(modifier = Modifier.height(16.dp))

        if (vModel.isServerUp.value) {
            Toast.makeText(context, "FTP Server is in work!", Toast.LENGTH_LONG).show()
            CircularProgressIndicator()
        }

        Row {
            Button(onClick = vModel::startFtpServer) {
                Text(text = "Start FTP Server")
            }

            Spacer(modifier = Modifier.size(16.dp, 4.dp))

            Button(onClick = {
                if (vModel.isServerUp.value) {
                    vModel.stopFtpServer()
                } else {
                    Toast.makeText(context, "No FTP Server is present!", Toast.LENGTH_LONG).show()
                }
            }) {
                Text(text = "Stop FTP Server")
            }
        }
    }
}