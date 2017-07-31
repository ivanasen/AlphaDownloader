package app.ivanasen.com.alphadownloader.fragments;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import app.ivanasen.com.alphadownloader.DownloadsAdapter;
import app.ivanasen.com.alphadownloader.R;
import app.ivanasen.com.alphadownloader.services.DownloadService;

public class DownloadsFragment extends Fragment {

    private static final String TAG = DownloadsFragment.class.getSimpleName();
    private static final int PERMISSION_STORAGE = 1;
    public static final int RUNNING_DOWNLOADS = 0;
    public static final int ALL_DOWNLOADS = 1;
    public static final String EXTRA_DOWNLOAD_NAME = "download_name";

    private Context mContext;
    private View mRootView;
    private RecyclerView mDownloadsView;
    private AlphaServiceConnection mServiceConnection;

    public DownloadsFragment() {
    }

    public static DownloadsFragment newInstance(int downloadsType) {
        DownloadsFragment fragment = new DownloadsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_downloads, container, false);

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            onRequestPermissions();
        } else {
            onDownloadServiceInit();
        }

        return mRootView;
    }

    private void onDownloadServiceInit() {
        Intent intent = new Intent(mContext, DownloadService.class);
        mContext.startService(intent);
        mServiceConnection = new AlphaServiceConnection();
        mContext.bindService(intent, mServiceConnection, 0);
    }

    private void onDownloadsInit(DownloadsAdapter adapter) {
        mDownloadsView = mRootView.findViewById(R.id.downloads_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mDownloadsView.setLayoutManager(layoutManager);
        mDownloadsView.setAdapter(adapter);

        Uri downloadUrl = getActivity().getIntent().getData();
        if (downloadUrl != null) {
            String downloadTitle = getActivity().getIntent().getStringExtra(DownloadsFragment.EXTRA_DOWNLOAD_NAME);
            if (downloadTitle == null || downloadTitle.equals("")) {
                downloadTitle = "pesho123";
            }
            adapter.addDownload(downloadUrl.toString(), downloadTitle);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_STORAGE: {
                if (grantResults.length > 0) {
                    onDownloadServiceInit();
                } else {
                    Toast.makeText(mContext, "Alpha Downloader can't work without storage permission",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void onRequestPermissions() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_STORAGE);
    }

    public class AlphaServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            DownloadService service = ((DownloadService.DownloadBinder) iBinder).getService();
            onDownloadsInit(service.getAdapter());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Toast.makeText(mContext, "Download done", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mServiceConnection != null) {
            mContext.unbindService(mServiceConnection);
        }
    }
}