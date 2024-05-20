package com.tyoma.testingzone.libs.main

// MyFtpFile.kt
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

    companion object {
        const val TYPE_FILE = 0
        const val TYPE_DIRECTORY = 1
        const val TYPE_LINK = 2

        @JvmField
        val CREATOR = object : Parcelable.Creator<MyFtpFile> {
            override fun createFromParcel(source: Parcel): MyFtpFile = MyFtpFile(source)
            override fun newArray(size: Int): Array<MyFtpFile?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readInt(),
        source.readLong(),
        source.readSerializable() as? LocalDateTime
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(remotePath)
        dest.writeInt(type)
        dest.writeLong(size)
        dest.writeSerializable(modifiedDate)
    }

    override fun toString(): String {
        return "MyFtpFile(name='$name', remotePath='$remotePath', type=$type, size=$size, modifiedDate=$modifiedDate)"
    }
}