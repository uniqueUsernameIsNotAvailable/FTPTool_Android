package com.tyoma.testingzone.libs.main;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDateTime;

public final class MyFtpFile implements Parcelable {

    public static final int TYPE_FILE = 0;
    public static final int TYPE_DIRECTORY = 1;
    public static final int TYPE_LINK = 2;

    private String name;
    private String remotePath;
    private int type;
    private long size;
    private LocalDateTime modifiedDate = null;


    public MyFtpFile(String name, String remotePath, int type, long size, LocalDateTime modifiedDate) {
        this.name = name;
        this.remotePath = remotePath;
        this.type = type;
        this.size = size;
        this.modifiedDate = modifiedDate;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public int getType() {
        return type;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(remotePath);
        dest.writeInt(type);
        dest.writeLong(size);
        dest.writeSerializable(modifiedDate);
    }

    private static final Creator<MyFtpFile> CREATOR = new Creator<MyFtpFile>() {
        @Override
        public MyFtpFile createFromParcel(Parcel source) {
            return new MyFtpFile(source);
        }

        @Override
        public MyFtpFile[] newArray(int size) {
            return new MyFtpFile[size];
        }
    };

    private MyFtpFile(Parcel in) {
        this.name = in.readString();
        this.remotePath = in.readString();
        this.type = in.readInt();
        this.size = in.readLong();
        this.modifiedDate = (LocalDateTime) in.readSerializable();
    }

    @Override
    public String toString() {
        return "MyFtpFile{" + "name='" + name + '\'' + ", remotePath='" + remotePath + '\'' + ", type=" + type + ", size=" + size + ", modifiedDate=" + modifiedDate + '}';
    }
}
