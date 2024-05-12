package com.tyoma.testingzone.libs.main;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tyoma.testingzone.libs.callback.MyFTPCallback;
import com.tyoma.testingzone.libs.callback.MyFTPTransferCallback;
import com.tyoma.testingzone.libs.exceptions.MyFtpNoInitExc;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

final class MyFtpClientImpl implements IMyFtpClient {

    private static final String TAG = "MyFtpClientImpl";
    private static final String HOME_DIR = "/";

    private FTPClient ftpClient;
    private HandlerThread taskThread = new HandlerThread("ftp-task");
    private Handler taskHandler;
    private final Object lock = new Object();
    private boolean isInit = false;
    private String curDirPath;

    MyFtpClientImpl() {
        init();
    }

    private void setCurDirPath(String path) {
        synchronized (lock) {
            this.curDirPath = path;
        }
    }

    // init client
    private void init() {
        synchronized (lock) {
            //init work thread
            final HandlerThread temp = taskThread;
            if (!temp.isAlive()) {
                temp.start();
                taskHandler = new Handler(temp.getLooper());
            }
            //create ftp client object
            ftpClient = new FTPClient();
            ftpClient.setPassive(true);
            ftpClient.setType(FTPClient.TYPE_BINARY);
            isInit = true;
        }
    }

    // release client
    @Override
    public void release() {
        synchronized (lock) {
            //disconnect if it is currently connected
            if (ftpClient != null && isConnected()) {
                disconnect();
            }
            //release work thread
            final HandlerThread temp = taskThread;
            if (temp.isAlive()) {
                temp.quit();
            }
            //clear message queue
            if (taskHandler != null) {
                taskHandler.removeCallbacksAndMessages(null);
            }
            isInit = false;
        }
    }

    private void checkInit() {
        if (!isInit) {
            throw new MyFtpNoInitExc("MyFTPClient: not init/released！");
        }
    }


    private @Nullable
    String getPrevPath() {
        if (TextUtils.isEmpty(curDirPath)) {
            return null;
        }

        //if cur path is home dir,return
        //Because it can't go back to the previous level
        if (TextUtils.equals(curDirPath, HOME_DIR)) {
            return HOME_DIR;
        }

        //get last index
        final int lastIndex = curDirPath.lastIndexOf("/");
        if (lastIndex == 0) {
            return HOME_DIR;
        }
        return curDirPath.substring(0, lastIndex);

    }

    @SuppressWarnings("unchecked")
    private void callbackNormalSuccess(@Nullable final MyFTPCallback callBack, @Nullable final Object response) {
        MyFtpCallbackWrapper wrapper = new MyFtpCallbackWrapper(callBack);
        wrapper.onSuccess(response);
    }

    @SuppressWarnings("unchecked")
    private void callbackNormalFail(@Nullable final MyFTPCallback callBack, final int code, final String msg) {
        MyFtpCallbackWrapper wrapper = new MyFtpCallbackWrapper(callBack);
        wrapper.onFail(code, msg);
    }


    @Override
    public void connect(@NonNull final String serverIp, @NonNull final int port, @NonNull final String userName, @NonNull final String password) {
        connect(serverIp, port, userName, password, null);
    }

    @Override
    public void connect(@NonNull final String serverIp, @NonNull final int port, @NonNull final String userName, @NonNull final String password, @Nullable final MyFTPCallback<Void> callBack) {
        checkInit();
        Log.d(TAG, "connect ftp server : serverIp = " + serverIp + ",port = " + port
                + ",user = " + userName + ",pw = " + password);
        taskHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ftpClient.connect(serverIp, port);
                    ftpClient.login(userName, password);
                    getCurDirPath(null);
                    callbackNormalSuccess(callBack, null);
                } catch (IOException e) {
                    e.printStackTrace();
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, "IOException");
                } catch (FTPIllegalReplyException e) {
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_FAIL, "Read server response fail!");
                } catch (FTPException e) {
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.getMessage());
                } catch (IllegalStateException e) {
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.getMessage());
                }
            }
        });
    }

    @Override
    public void disconnect() {
        disconnect(null);
    }

    @Override
    public void disconnect(@Nullable final MyFTPCallback<Void> callBack) {
        checkInit();
        taskHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ftpClient.disconnect(true);
                    callbackNormalSuccess(callBack, null);
                    release();
                } catch (IOException e) {
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, "IOException");
                } catch (FTPIllegalReplyException e) {
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_FAIL, "Read server response fail!");
                } catch (FTPException e) {
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.getMessage());
                } catch (IllegalStateException e) {
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean isConnected() {
        return ftpClient != null && ftpClient.isConnected();
    }

    @Override
    public void getCurDirFileList(@Nullable final MyFTPCallback<List<MyFtpFile>> callBack) {
        checkInit();
        taskHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    FTPFile[] ftpFiles = ftpClient.list();
                    List<MyFtpFile> myFtpFiles = new ArrayList<>();
                    for (FTPFile ftpFile : ftpFiles) {
                        LocalDateTime dt = LocalDateTime.ofInstant(ftpFile.getModifiedDate().toInstant(), ZoneId.systemDefault());
                        myFtpFiles.add(
                                new MyFtpFile(
                                        ftpFile.getName(),
                                        curDirPath,
                                        ftpFile.getType(),
                                        ftpFile.getSize(),
                                        dt
                                ));
                    }
                    callbackNormalSuccess(callBack, myFtpFiles);
                } catch (IOException e) {
                    e.printStackTrace();
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, "IOException");
                } catch (FTPIllegalReplyException e) {
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_FAIL, "Read server response fail!");
                } catch (FTPException e) {
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.getMessage());
                } catch (IllegalStateException e) {
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.getMessage());
                } catch (FTPDataTransferException e) {
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.getMessage());
                } catch (FTPAbortedException e) {
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.getMessage());
                } catch (FTPListParseException e) {
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.getMessage());
                }
            }
        });
    }

    @Override
    public void getCurDirPath(@Nullable final MyFTPCallback<String> callBack) {
        checkInit();
        taskHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    final String path = ftpClient.currentDirectory();
                    setCurDirPath(path);
                    callbackNormalSuccess(callBack, path);
                } catch (IOException e) {
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, "IOException");
                } catch (FTPIllegalReplyException e) {
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_FAIL, "Read server response fail!");
                } catch (FTPException e) {
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.getMessage());
                } catch (IllegalStateException e) {
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.getMessage());
                }
            }
        });
    }

    @Override
    public void changeDirectory(@Nullable final String path, @Nullable final MyFTPCallback<String> callBack) {
        checkInit();
        taskHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (TextUtils.isEmpty(path)) {
                        callbackNormalFail(callBack, MyFtpResultCode.RESULT_FAIL, "path is empty!");
                    } else {
                        ftpClient.changeDirectory(path);
                        setCurDirPath(path);
                        callbackNormalSuccess(callBack, path);
                    }
                } catch (IOException e) {
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, "IOException");
                } catch (FTPIllegalReplyException e) {
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_FAIL, "Read server response fail!");
                } catch (FTPException e) {
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.getMessage());
                } catch (IllegalStateException e) {
                    callbackNormalFail(callBack, MyFtpResultCode.RESULT_EXCEPTION, e.getMessage());
                }
            }
        });
    }

    @Override
    public void moveUpDir(@Nullable final MyFTPCallback<String> callBack) {
        changeDirectory(getPrevPath(), callBack);
    }

    @Override
    public void backToHomeDir(MyFTPCallback<String> callBack) {
        changeDirectory(HOME_DIR, callBack);
    }

    @Override
    public void downloadFile(@NonNull final MyFtpFile remoteFile, @NonNull String localFilePath, @Nullable MyFTPTransferCallback callback) {
        checkInit();

        final File localFile = new File(localFilePath);
        final MyFtpTransferCallbackWrapper callbackWrapper
                = new MyFtpTransferCallbackWrapper(callback);

        taskHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ftpClient.download(remoteFile.getName(), localFile, new FTPDataTransferListener() {
                        @Override
                        public void started() {
                            callbackWrapper.onStateChanged(MyFTPTransferCallback.START);
                        }

                        @Override
                        public void transferred(int i) {
                            callbackWrapper.onTransferDone(remoteFile.getSize(), i);
                        }

                        @Override
                        public void completed() {
                            callbackWrapper.onStateChanged(MyFTPTransferCallback.COMPLETED);
                        }

                        @Override
                        public void aborted() {
                            callbackWrapper.onStateChanged(MyFTPTransferCallback.ABORTED);
                        }

                        @Override
                        public void failed() {
                            callbackWrapper.onStateChanged(MyFTPTransferCallback.ERROR);
                            callbackWrapper.onErr(MyFtpResultCode.RESULT_FAIL, "Download file fail!");
                        }
                    });
                } catch (IOException e) {
                    callbackWrapper.onStateChanged(MyFTPTransferCallback.ERROR);
                    callbackWrapper.onErr(MyFtpResultCode.RESULT_EXCEPTION, "IOException");
                } catch (FTPIllegalReplyException e) {
                    callbackWrapper.onStateChanged(MyFTPTransferCallback.ERROR);
                    callbackWrapper.onErr(MyFtpResultCode.RESULT_EXCEPTION, "Read server response fail!");
                } catch (FTPException e) {
                    callbackWrapper.onStateChanged(MyFTPTransferCallback.ERROR);
                    callbackWrapper.onErr(MyFtpResultCode.RESULT_EXCEPTION, e.getMessage());
                } catch (FTPDataTransferException e) {
                    callbackWrapper.onStateChanged(MyFTPTransferCallback.ERROR);
                    callbackWrapper.onErr(MyFtpResultCode.RESULT_EXCEPTION, e.getMessage());
                } catch (FTPAbortedException e) {
                    callbackWrapper.onStateChanged(MyFTPTransferCallback.ABORTED);
                }
            }
        });
    }

    @Override
    public void uploadFile(@NonNull final String localFilePath, @Nullable MyFTPTransferCallback callback) {
        checkInit();

        final File localFile = new File(localFilePath);
        final MyFtpTransferCallbackWrapper callbackWrapper
                = new MyFtpTransferCallbackWrapper(callback);

        taskHandler.post(() -> {
            try {
                ftpClient.upload(localFile, new FTPDataTransferListener() {
                    @Override
                    public void started() {
                        callbackWrapper.onStateChanged(MyFTPTransferCallback.START);
                    }

                    @Override
                    public void transferred(int i) {
                        callbackWrapper.onTransferDone(localFile.length(), i);
                    }

                    @Override
                    public void completed() {
                        callbackWrapper.onStateChanged(MyFTPTransferCallback.COMPLETED);
                    }

                    @Override
                    public void aborted() {
                        callbackWrapper.onStateChanged(MyFTPTransferCallback.ABORTED);
                    }

                    @Override
                    public void failed() {
                        callbackWrapper.onStateChanged(MyFTPTransferCallback.ERROR);
                        callbackWrapper.onErr(MyFtpResultCode.RESULT_FAIL, "Download file fail!");
                    }
                });
            } catch (IOException e) {
                callbackWrapper.onStateChanged(MyFTPTransferCallback.ERROR);
                callbackWrapper.onErr(MyFtpResultCode.RESULT_EXCEPTION, "IOException");
            } catch (FTPIllegalReplyException e) {
                callbackWrapper.onStateChanged(MyFTPTransferCallback.ERROR);
                callbackWrapper.onErr(MyFtpResultCode.RESULT_EXCEPTION, "Read server response fail!");
            } catch (FTPException e) {
                callbackWrapper.onStateChanged(MyFTPTransferCallback.ERROR);
                callbackWrapper.onErr(MyFtpResultCode.RESULT_EXCEPTION, e.getMessage());
            } catch (FTPDataTransferException e) {
                callbackWrapper.onStateChanged(MyFTPTransferCallback.ERROR);
                callbackWrapper.onErr(MyFtpResultCode.RESULT_EXCEPTION, e.getMessage());
            } catch (FTPAbortedException e) {
                callbackWrapper.onStateChanged(MyFTPTransferCallback.ABORTED);
            }
        });
    }

    @Override
    public boolean isCurDirHome() {
        return TextUtils.equals(curDirPath, HOME_DIR);
    }
}
