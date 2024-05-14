package com.tyoma.testingzone.libs.callback;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//callback for speed measurement
public abstract class MyFTPSpeedCallback implements MyFTPTransferCallback {

    private static final long CALC_TIME = 1000;

    private long startTime, endTime;
    private long totalSize, tempTotalSize;
    private ScheduledExecutorService executors = Executors.newSingleThreadScheduledExecutor();
    private boolean isFinish = false;

    private Runnable calcSpeedTask = new Runnable() {
        @Override
        public void run() {
            final long totalSize1 = totalSize;
            final long transferredSize = totalSize1 - tempTotalSize;
            final double speed = transferredSize / 1024.0 / 1000;
            double avgSpeed = 0.00d;
            if (isFinish) {
                final long transferredTime = endTime - startTime;
                avgSpeed = totalSize1 / 1024.0 / transferredTime / 1000;
            }

            onTransferSpeed(isFinish, startTime, endTime, speed, avgSpeed);

            tempTotalSize = totalSize1;
        }
    };

    @Override
    public void onStateChanged(int state) {
        switch (state) {
            case MyFTPTransferCallback.START:
                startTime = System.currentTimeMillis();
                executors.scheduleWithFixedDelay(
                        calcSpeedTask,
                        CALC_TIME,
                        CALC_TIME,
                        TimeUnit.MILLISECONDS);
                break;
            case MyFTPTransferCallback.ERROR:
            case MyFTPTransferCallback.COMPLETED:
            case MyFTPTransferCallback.ABORTED:
                isFinish = true;
                endTime = System.currentTimeMillis();
                executors.shutdown();
                break;
            default:
                break;
        }
    }

    @Override
    public void onTransferDone(long fileSize, int resultSize) {
        totalSize += resultSize;
    }

    @Override
    public void onErr(int code, String msg) {

    }

    public abstract void onTransferSpeed(boolean isFinished, long startTime, long endTime, double speed, double averageSpeed);

}
