package com.tyoma.testingzone.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tyoma.testingzone.Screen

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        modifier = modifier,
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center
    )
}

@Composable
fun DescriptionText(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center
    )
}


@Composable
fun WelcomeScreen(nControl: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Greeting("FTP Android application", Modifier.offset(0.dp, (-128).dp))
        DescriptionText(
            "Choose one of the option with buttons below:", Modifier.offset(0.dp, (-64).dp)
        )
        Button(onClick = { nControl.navigate(Screen.SecondScreen.route) }, Modifier.padding(8.dp)) {
            Text(text = "Server Mode")
        }

        Button(onClick = { nControl.navigate(Screen.ThirdScreen.route) }, Modifier.padding(8.dp)) {
            Text(text = "Client Mode")
        }
    }
}