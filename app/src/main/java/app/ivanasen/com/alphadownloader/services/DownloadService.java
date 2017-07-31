package app.ivanasen.com.alphadownloader.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import app.ivanasen.com.alphadownloader.DownloadsAdapter;
import app.ivanasen.com.alphadownloader.util.Utility;

/**
 * Created by ivan on 7/31/2017.
 */

public class DownloadService extends Service {

    private Context mContext;
    private DownloadsAdapter mAdapter;
    private DownloadBinder mBinder;

    private final class DownloadHandler extends Handler {
        public DownloadHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mAdapter = new DownloadsAdapter(mContext, Utility.getDownloads(), false);
        mBinder = new DownloadBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public DownloadsAdapter getAdapter() {
        return mAdapter;
    }

    public class DownloadBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }
}
