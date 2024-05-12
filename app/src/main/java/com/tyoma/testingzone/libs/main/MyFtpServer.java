package com.tyoma.testingzone.libs.main;

import com.tyoma.testingzone.libs.user.MyFtpUser;

import java.util.ArrayList;
import java.util.List;

public final class MyFtpServer implements IMyFtpServer {

    private IMyFtpServer ftpServerImpl;

    private MyFtpServer(List<MyFtpUser> users, int port) {
        ftpServerImpl = new MyFtpServerImpl(users, port);
    }

    @Override
    public void start() {
        ftpServerImpl.start();
    }

    @Override
    public void stop() {
        ftpServerImpl.stop();
    }

    @Override
    public boolean isStopped() {
        return ftpServerImpl.isStopped();
    }

    public static final class Builder {
        private List<MyFtpUser> users = new ArrayList<>();
        private int port;

        public Builder addUser(MyFtpUser user) {
            users.add(user);
            return this;
        }

        public Builder setListenPort(int port) {
            this.port = port;
            return this;
        }

        public MyFtpServer create() {
            return new MyFtpServer(users, port);
        }
    }
}
