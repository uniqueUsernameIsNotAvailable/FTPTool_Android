package com.tyoma.testingzone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tyoma.testingzone.ui.ClientScreen
import com.tyoma.testingzone.ui.ServerScreen
import com.tyoma.testingzone.ui.WelcomeScreen
import com.tyoma.testingzone.ui.theme.TestingZoneTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestingZoneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    MainComposition()
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainComposition() {
    val nControl = rememberNavController()
    TestingZoneTheme {
        NavHost(nControl, startDestination = Screen.FirstScreen.route) {
            composable(route = Screen.FirstScreen.route) {
                WelcomeScreen(nControl = nControl)
            }

            composable(route = Screen.SecondScreen.route) {
                ServerScreen()
            }

            composable(route = Screen.ThirdScreen.route) {
                ClientScreen()
            }
        }
    }
}