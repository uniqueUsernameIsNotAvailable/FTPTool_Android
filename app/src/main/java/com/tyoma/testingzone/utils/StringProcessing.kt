package com.tyoma.testingzone.utils

fun transformStringToList(input: String): List<String> {
    val result = mutableListOf<String>()
    var prevIndex = 0

    for (i in input.indices) {
        if (input[i] == '/') {
            if (i > prevIndex) {
                result.add(input.substring(prevIndex, i))
            }
            result.add("/")
            prevIndex = i + 1
        }
    }

    return result
}

fun transformListToString(list: List<String>, startIndex: Int): String {
    val sb = StringBuilder()
    for (i in startIndex downTo 0) {
        sb.append(list[i])
    }
    return sb.toString()
}

fun transformListToStringForward(list: List<String>, startIndex: Int): String {
    val sb = StringBuilder()
    for (i in  0..startIndex) {
        sb.append(list[i])
    }
    return sb.toString()
}

