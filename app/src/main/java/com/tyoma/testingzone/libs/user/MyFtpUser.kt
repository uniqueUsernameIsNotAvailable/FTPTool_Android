package com.tyoma.testingzone.libs.user

class MyFtpUser(
    val name: String,
    val password: String,
    val sharedPath: String,
    val permission: MyFtpUserPerm
) {
    override fun toString(): String {
        return "MyFtpUser{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", sharedPath='" + sharedPath + '\'' +
                ", permission=" + permission +
                '}'
    }
}
