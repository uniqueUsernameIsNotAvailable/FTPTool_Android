package com.tyoma.testingzone

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

//@Composable
//fun Navigation() {
//    val nControl = rememberNavController()
//    NavHost(nControl, startDestination = Screen.FirstScreen.route) {
//        composable(route = Screen.FirstScreen.route) {
//            WelcomeScreen(nControl = nControl)
//        }
//
//        composable(route = Screen.SecondScreen.route) { entry ->
//            SecondScreen()
//        }
//    }
//}
//
//@Composable
//fun FirstScreen(nControl: NavController) {
//    Button(onClick = { nControl.navigate(Screen.SecondScreen.route) }) {
//        Text(text = "Yes")
//    }
//}
//
//@Composable
//fun SecondScreen() {
//    Text(text = "No")
//}