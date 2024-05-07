package com.tyoma.testingzone

sealed class Screen(val route:String) {
    data object FirstScreen:Screen("first_screen")
    data object SecondScreen:Screen("second_screen")
    data object ThirdScreen:Screen("third_screen")
}