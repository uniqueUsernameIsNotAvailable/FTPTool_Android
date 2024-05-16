package com.tyoma.testingzone.libs.user

import org.apache.ftpserver.ftplet.Authority
import org.apache.ftpserver.usermanager.impl.WritePermission

enum class MyFtpUserPerm(val authority: Authority) {
    WRITE(WritePermission())
}
