package com.tyoma.testingzone.libs.user;

public final class MyFtpUser {
    private String name;
    private String password;
    private String sharedPath;
    private MyFtpUserPerm permission;

    public MyFtpUser(String name, String password, String sharedPath, MyFtpUserPerm permission) {
        this.name = name;
        this.password = password;
        this.sharedPath = sharedPath;
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getSharedPath() {
        return sharedPath;
    }

    public MyFtpUserPerm getPermission() {
        return permission;
    }

    @Override
    public String toString() {
        return "MyFtpUser{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", sharedPath='" + sharedPath + '\'' +
                ", permission=" + permission +
                '}';
    }
}
