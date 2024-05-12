package com.tyoma.testingzone.libs.main;


import com.tyoma.testingzone.libs.exceptions.MyFtpNoInitExc;
import com.tyoma.testingzone.libs.user.MyFtpUser;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;

import java.util.ArrayList;
import java.util.List;

final class MyFtpServerImpl implements IMyFtpServer {

    private static final String TAG = "MyFtpServerImpl";

    private FtpServer ftpServer;
    private final Object lock = new Object();
    private boolean isInit = false;

    MyFtpServerImpl(List<MyFtpUser> users, int port) {
        FtpServerFactory serverFactory = new FtpServerFactory();
        for (MyFtpUser user : users) {
            BaseUser baseUser = new BaseUser();
            baseUser.setName(user.getName());
            baseUser.setPassword(user.getPassword());
            baseUser.setHomeDirectory(user.getSharedPath());
            List<Authority> authorities = new ArrayList<>();
            authorities.add(user.getPermission().getAuthority());
            baseUser.setAuthorities(authorities);
            try {
                serverFactory.getUserManager().save(baseUser);
            } catch (FtpException e) {
                e.printStackTrace();
            }
        }

        ListenerFactory factory = new ListenerFactory();
        factory.setPort(port);
        serverFactory.addListener("default", factory.createListener());
        ftpServer = serverFactory.createServer();
        isInit = true;
    }


    private void checkInit() {
        synchronized (lock) {
            if (!isInit) {
                throw new MyFtpNoInitExc("MyFTP server: no init or released");
            }
        }
    }


    private void release() {
        synchronized (lock) {
            if (ftpServer != null && !ftpServer.isStopped()) {
                ftpServer.stop();
            }
            isInit = false;
        }
    }


    @Override
    public void start() {
        checkInit();
        try {
            ftpServer.start();
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void stop() {
        checkInit();
        ftpServer.stop();
    }

    @Override
    public boolean isStopped() {
        return ftpServer.isStopped();
    }
}
