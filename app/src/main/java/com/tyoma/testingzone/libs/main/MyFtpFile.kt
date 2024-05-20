package com.tyoma.testingzone.libs.main

import android.os.Parcel
import android.os.Parcelable
import java.time.LocalDateTime


class MyFtpFile(
    val name: String,
    val remotePath: String,
    val type: Int,
    val size: Long,
    val modifiedDate: LocalDateTime? = null
) : Parcelable {

    constructor(source: Parcel) : this(source.readString()!!,
        source.readString()!!,
        source.readInt(),
        source.readLong(),
        source.readString()?.let { LocalDateTime.parse(it) })

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(remotePath)
        dest.writeInt(type)
        dest.writeLong(size)
        dest.writeString(modifiedDate?.toString())
    }

    override fun toString(): String {
        return "MyFtpFile(name='$name', remotePath='$remotePath', type=$type, size=$size, modifiedDate=$modifiedDate)"
    }

    companion object CREATOR : Parcelable.Creator<MyFtpFile> {
        const val TYPE_FILE = 0
        const val TYPE_DIRECTORY = 1
        const val TYPE_LINK = 2

        override fun createFromParcel(parcel: Parcel): MyFtpFile {
            return MyFtpFile(parcel)
        }

        override fun newArray(size: Int): Array<MyFtpFile?> {
            return arrayOfNulls(size)
        }
    }
}