package app.ivanasen.com.alphadownloader.util;

import android.content.ActivityNotFoundException;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.ivanasen.com.alphadownloader.BuildConfig;
import app.ivanasen.com.alphadownloader.R;

import static app.ivanasen.com.alphadownloader.util.Utility.FileType.APP;
import static app.ivanasen.com.alphadownloader.util.Utility.FileType.ARCHIVE;
import static app.ivanasen.com.alphadownloader.util.Utility.FileType.MUSIC;
import static app.ivanasen.com.alphadownloader.util.Utility.FileType.OFFICE;
import static app.ivanasen.com.alphadownloader.util.Utility.FileType.PDF;
import static app.ivanasen.com.alphadownloader.util.Utility.FileType.UNKNOWN;
import static app.ivanasen.com.alphadownloader.util.Utility.FileType.VIDEO;

/**
 * Created by ivan on 7/24/2017.
 */

public class Utility {

    //Not using an enum because enums are slow in Android
    public static class FileType {
        public static final int APP = 0;
        public static final int VIDEO = 1;
        public static final int OFFICE = 2;
        public static final int PDF = 3;
        public static final int MUSIC = 4;
        public static final int ARCHIVE = 5;
        public static final int UNKNOWN = 6;
    }

    public static String formatUrl(String search) {
        return "https://" + Uri.encode(search);
    }

    public static void startActivity(Context context, Class<?> activityClass) {
        Intent intent = new Intent(context, activityClass);
        context.startActivity(intent);
    }

    public static void openFragment(FragmentActivity activity, Fragment fragment, int fragmentContainerRes) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(fragmentContainerRes, fragment);
        ft.commit();
    }

    public static void sendCustomerCareEmail(Context context) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.support_email)});
        try {
            context.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public static File getDownloadsDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }

    public static List<Download> getDownloads() {
        File downloadsDir = getDownloadsDirectory();
        List<Download> downloads = new ArrayList<>();
        for (File file : Arrays.asList(downloadsDir.listFiles())) {
            downloads.add(new Download(file));
        }
        return downloads;
    }

    public static int getFileType(String file) {
        if (file.endsWith(".apk")) {
            return APP;
        } else if (file.endsWith(".mp3") || file.endsWith(".wav") || file.endsWith(".flac")) {
            return MUSIC;
        } else if (file.endsWith(".mp4") || file.endsWith(".mpeg") || file.endsWith(".rm") || file.endsWith(".rmvb")
                || file.endsWith(".flv") || file.endsWith(".webp")) {
            return VIDEO;
        } else if (file.endsWith(".doc") || file.endsWith(".docx") ||
                file.endsWith(".xls") || file.endsWith(".xlsx") ||
                file.endsWith(".ppt") || file.endsWith(".pptx")) {
            return OFFICE;
        } else if (file.endsWith(".pdf")) {
            return PDF;
        } else if (file.endsWith(".zip") || file.endsWith(".rar") || file.endsWith(".7z") || file.endsWith(".gz")
                || file.endsWith("tar") || file.endsWith(".bz")) {
            return ARCHIVE;
        } else {
            return UNKNOWN;
        }
    }

    public static int getIconForFileType(int type) {
        switch (type) {
            case APP:
                return R.drawable.android;
            case MUSIC:
                return R.drawable.music;
            case ARCHIVE:
                return R.drawable.archive;
            case VIDEO:
                return R.drawable.video;
            case OFFICE:
                return R.drawable.office;
            case PDF:
                return R.drawable.pdf;
            case UNKNOWN:
            default:
                return R.drawable.unknown;
        }
    }

    public static void openFile(Context context, File file) {
        try {
            Uri uri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (uri.toString().contains(".doc") || file.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if (file.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (file.toString().contains(".ppt") || file.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (file.toString().contains(".xls") || file.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (file.toString().contains(".zip") || file.toString().contains(".rar")) {
                // WAV audio file
                intent.setDataAndType(uri, "application/x-wav");
            } else if (file.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if (file.toString().contains(".wav") || file.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if (file.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (file.toString().contains(".jpg") || file.toString().contains(".jpeg") || file.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if (file.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if (file.toString().contains(".3gp") || file.toString().contains(".mpg") ||
                    file.toString().contains(".mpeg") || file.toString().contains(".mpe") || file.toString().contains(".mp4") || file.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else if (file.toString().contains(".apk")) {
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(uri, "*/*");
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_SHORT).show();
        }
    }
}
