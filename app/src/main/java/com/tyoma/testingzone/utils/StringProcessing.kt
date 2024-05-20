package com.tyoma.testingzone.utils

import com.tyoma.testingzone.libs.main.MyFtpFile

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
    for (i in 0..startIndex) {
        sb.append(list[i])
    }
    return sb.toString()
}

fun itemInfoBuilder(file: MyFtpFile): String {
    val info = buildString {
        val fType = file.type
        val fNameLen = file.name.length
        appendLine(
            file.name.substring(
                0..kotlin.math.min(
                    fNameLen - 1, 12
                )
            ) + if (fNameLen > 16) "..." else " "
        )
        append("Type: " + if (fType == 1) "Folder " else "File ")
        if (fType == 0) {
            append("= " + file.size.toString() + " Bytes")
        }
        appendLine(" ")
        append("Modified: ")
        append(file.modifiedDate?.toLocalDate().toString() + " ")
        append(file.modifiedDate?.toLocalTime() ?: 0 )
    }
    return info
}

