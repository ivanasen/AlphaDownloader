package app.ivanasen.com.alphadownloader.util;

import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;

import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadManager;
import com.aspsine.multithreaddownload.DownloadRequest;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by ivan on 7/30/2017.
 */

public class Download {
    private String mDownloadUrl;
    private File mDownloadFile;
    private boolean mFinished;
    private boolean mIsDownloading;
    private String mTitle;
    private DownloadManager mDownloadManager;
    private boolean canceled;

    private Download() {
        mDownloadManager = DownloadManager.getInstance();
        canceled = false;
    }

    public Download(String downloadUrl, String title) {
        this();
        mDownloadUrl = downloadUrl;
        mTitle = title;
        mFinished = false;
    }

    public Download(File file) {
        this();
        mDownloadFile = file;
        mTitle = file.getName();
        mFinished = true;
    }

    public void startDownloading(CallBack callBack) {
        if (mFinished) {
            return;
        }
        mFinished = false;
        mIsDownloading = false;

        DownloadRequest request = new DownloadRequest.Builder()
                .setFolder(Utility.getDownloadsDirectory())
                .setUri(mDownloadUrl)
                .setName(mTitle)
                .build();
        mDownloadManager.download(request, mTitle, callBack);
    }

    public void setFinished(boolean finished) {
        mFinished = finished;
        if (finished) {
            mDownloadFile = Utility.getDownloadsDirectory().listFiles((file, s) -> s.equals(mTitle))[0];
        }
    }

    public boolean isFinished() {
        return mFinished;
    }

    public void setDownloading(boolean isDownloading) {
        mIsDownloading = isDownloading;
    }

    public boolean isDownloading() {
        return this.mIsDownloading;
    }

    public File getDownloadFile() {
        return mDownloadFile;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean isCanceled() {
        return canceled;
    }
}
