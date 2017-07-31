package app.ivanasen.com.alphadownloader.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DownloadsManager {
    private static final String TAG = DownloadsManager.class.getSimpleName();
    private Context mContext;

    public DownloadsManager(Context context) {
        mContext = context;
    }



    public Context getContext() {
        return mContext;
    }
}
