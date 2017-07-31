package app.ivanasen.com.alphadownloader.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;
import android.widget.Toast;

import app.ivanasen.com.alphadownloader.R;
import app.ivanasen.com.alphadownloader.fragments.DownloadsFragment;
import app.ivanasen.com.alphadownloader.util.Utility;

public class BrowserActivity extends AppCompatActivity {

    private static final String TAG = BrowserActivity.class.getSimpleName();
    private static final String[] VIDEO_SUFFIXES = new String[]{
            ".mp4",
            ".flv",
            ".rm",
            ".rmvb",
            ".wmv",
            ".avi",
            ".mkv",
            ".webm"
    };

    private WebView mWebView;
    private EditText mSearchBox;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        configureSearchBox();
        configureWebView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_browser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_search:
                String url = Utility.formatUrl(mSearchBox.getText().toString());
                loadUrlPage(url);
                break;
            case R.id.action_settings:

                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void configureSearchBox() {
        mSearchBox = (EditText) findViewById(R.id.browser_searchbox);
        mSearchBox.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                hideSoftwareKeyboard();
                String url = Utility.formatUrl(textView.getText().toString());
                loadUrlPage(url);
                return true;
            }
            return false;
        });
    }

    private void hideSoftwareKeyboard() {
        try {
            InputMethodManager inputManager =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configureWebView() {
        mProgress = (ProgressBar) findViewById(R.id.progress);
        mWebView = (WebView) findViewById(R.id.browser_webview);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                getSupportActionBar().setDisplayShowCustomEnabled(false);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mProgress.setProgress(0);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap fav) {
                if (isVideo(url)) {
                    // If viewing a video, start download immediately
                    view.stopLoading();
                    downloadFile(url, null, null, null, 0);
                }
            }


        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgress.setProgress(newProgress);
            }
        });
        mWebView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            mWebView.stopLoading();
            Log.d(TAG, "userAgent: " + userAgent + "\n" +
                "contentDisposition: " + contentDisposition + "\n" +
                "mimeType: " + mimeType + "\n" +
                "contentLength: " + contentLength + "\n");
            downloadFile(url, userAgent, contentDisposition, mimeType, contentLength);
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        loadUrlPage(getString(R.string.home_page));
    }

    private void downloadFile(String url, String userAgent, String contentDisposition,
                              String mimeType, long contentLength) {
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
        Intent i = new Intent();
        i.setAction(MainActivity.INTENT_DOWNLOAD);
        i.setDataAndType(Uri.parse(url), "application/octet-stream");
        i.putExtra(DownloadsFragment.EXTRA_DOWNLOAD_NAME, fileName);
        Toast.makeText(this, "Downloading file...", Toast.LENGTH_LONG).show();
        startActivity(i);
        finish();
    }

    private static boolean isVideo(String url) {
        for (String suffix : VIDEO_SUFFIXES) {
            if (url.contains(suffix)) {
                return true;
            }
        }

        return false;
    }

    private void loadUrlPage(String url) {
        mWebView.loadUrl(url);
        mSearchBox.clearFocus();
    }
}
