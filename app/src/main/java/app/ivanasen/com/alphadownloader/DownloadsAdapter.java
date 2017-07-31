package app.ivanasen.com.alphadownloader;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadException;

import java.util.List;

import app.ivanasen.com.alphadownloader.util.Download;
import app.ivanasen.com.alphadownloader.util.Utility;

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.ViewHolder> {
    private final Context mContext;
    private List<Download> mDownloads;

    public DownloadsAdapter(Context context, List<Download> downloads, boolean showOnlyActive) {
        mContext = context;
        if (showOnlyActive) {
            for (Download download : downloads) {
                if (!download.isFinished() && !download.isCanceled()) {
                    mDownloads.add(download);
                }
            }
        } else {
            mDownloads = downloads;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolderLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_download, parent, false);
        return new ViewHolder(viewHolderLayout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Download currentDownload = mDownloads.get(position);
        String title = currentDownload.getTitle();
        holder.mFileTitleTextView.setText(title);
        int fileType = Utility.getFileType(title);
        holder.mFileTypeImageView.setImageResource(Utility.getIconForFileType(fileType));

        if (currentDownload.isFinished()) {
            holder.mItemView.setOnClickListener(view ->
                    Utility.openFile(mContext, currentDownload.getDownloadFile()));
        } else if (!currentDownload.isCanceled() && !currentDownload.isDownloading()) {
            holder.mItemView.setOnClickListener(null);
            currentDownload.setDownloading(true);

            //Build notification
            NotificationManager manager = (NotificationManager)
                    mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                    .setContentTitle(currentDownload.getTitle())
                    .setContentText(mContext.getString(R.string.download_notification_text))
                    .setSmallIcon(R.mipmap.ic_launcher);

            currentDownload.startDownloading(new CallBack() {
                @Override
                public void onStarted() {
                    builder.setProgress(100, 0, true);
                    Notification n = builder.build();
                    n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
                    manager.notify(currentDownload.hashCode(), n);
                }

                @Override
                public void onConnecting() {
                    builder.setProgress(100, 0, true);
                    Notification n = builder.build();
                    n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
                    manager.notify(currentDownload.hashCode(), n);
                }

                @Override
                public void onConnected(long total, boolean isRangeSupport) {
                    builder.setProgress(100, 0, false);
                    Notification n = builder.build();
                    n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
                    manager.notify(currentDownload.hashCode(), n);
                }

                @Override
                public void onProgress(long finished, long total, int progress) {
                    builder.setProgress(100, progress, false);
                    Notification n = builder.build();
                    n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
                    manager.notify(currentDownload.hashCode(), n);
                }

                @Override
                public void onCompleted() {
                    currentDownload.setFinished(true);
                    holder.mItemView.setOnClickListener(view ->
                            Utility.openFile(mContext, currentDownload.getDownloadFile())
                    );
                    builder.setContentText(mContext.getString(R.string.download_notification_completed_text))
                            .setProgress(0, 0, false);
                    Notification n = builder.build();
                    n.flags = 0;
                    manager.notify(currentDownload.hashCode(), builder.build());
                    currentDownload.setDownloading(false);
                }

                @Override
                public void onDownloadPaused() {
                    builder.setContentText(
                            mContext.getString(R.string.download_notification_paused_text)
                    );
                    manager.notify(currentDownload.hashCode(), builder.build());
                    currentDownload.setDownloading(false);
                }

                @Override
                public void onDownloadCanceled() {
                    builder.setContentText(
                            mContext.getString(R.string.download_notification_canceled_text)
                    ).setProgress(0, 0, false);
                    manager.notify(currentDownload.hashCode(), builder.build());
                    currentDownload.setCanceled(true);
                    currentDownload.setDownloading(false);
                }

                @Override
                public void onFailed(DownloadException e) {
                    builder.setContentText(
                            mContext.getString(R.string.download_notification_failed_text)
                    ).setProgress(0, 0, false);
                    manager.notify(currentDownload.hashCode(), builder.build());
                    currentDownload.setCanceled(true);
                    currentDownload.setDownloading(false);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDownloads.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mItemView;
        public ImageView mFileTypeImageView;
        public TextView mFileTitleTextView;
        public TextView mDownloadUrlTextView;
        public ProgressBar mDownloadProgressBar;
        public ImageButton mDownloadControlBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            mFileTypeImageView = itemView.findViewById(R.id.download_file_type_imageview);
            mFileTitleTextView = itemView.findViewById(R.id.download_file_title_textview);
            mDownloadUrlTextView = itemView.findViewById(R.id.download_file_url_textview);
            mDownloadProgressBar = itemView.findViewById(R.id.download_file_progress_bar);
            mDownloadControlBtn = itemView.findViewById(R.id.download_control_btn);
            mItemView = itemView;
        }
    }

    public void addDownload(String downloadUrl, String title) {
        mDownloads.add(new Download(downloadUrl, title));
        notifyDataSetChanged();
    }
}
