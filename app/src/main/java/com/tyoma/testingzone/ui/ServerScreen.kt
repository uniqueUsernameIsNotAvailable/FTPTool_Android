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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tyoma.testingzone.libs.main.EZFtpServer
import com.tyoma.testingzone.libs.user.EZFtpUser
import com.tyoma.testingzone.libs.user.EZFtpUserPermission

@Composable
fun ServerScreen() {
    var user by remember { mutableStateOf("") }
    var pswd by remember { mutableStateOf("") }
    var addr by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("") }




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

        var ftpUser: EZFtpUser
        lateinit var ftpServer: EZFtpServer
        var ftpServerStarted = false

        val mContext = LocalContext.current

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = {
                ftpUser = EZFtpUser(user, pswd, addr, EZFtpUserPermission.WRITE)
                ftpServer = EZFtpServer.Builder()
                    .addUser(ftpUser)
                    .setListenPort(port.toInt())
                    .create()
                ftpServer.start()
                ftpServerStarted = true
            }) {
                Text(text = "Start FTP Server")
            }

            Spacer(modifier = Modifier.size(16.dp, 4.dp))

            Button(onClick = {
                if (ftpServerStarted) {
                    ftpServer.stop()
                    ftpServerStarted = false
                } else Toast.makeText(
                    mContext, "No FTP Server is present!", Toast.LENGTH_LONG
                ).show()
            }) {
                Text(text = "Stop FTP Server")
            }
        }


//    Text(
//        text = "Welcome to our app!",
//        style = TextStyle(fontSize = 28.sp)
//    )
    }
}