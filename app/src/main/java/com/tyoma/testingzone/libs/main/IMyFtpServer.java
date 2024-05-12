package com.tyoma.testingzone.libs.main;

//FTP server interface
interface IMyFtpServer {

    void start();

    void stop();

    boolean isStopped();
}
