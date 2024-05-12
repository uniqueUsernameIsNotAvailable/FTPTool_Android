package com.tyoma.testingzone.libs.user;

import androidx.annotation.NonNull;

import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.usermanager.impl.WritePermission;


public enum MyFtpUserPerm {

    WRITE(new WritePermission());

    private Authority authority;

    public Authority getAuthority() {
        return authority;
    }

    MyFtpUserPerm(@NonNull Authority authority) {
        this.authority = authority;
    }
}
