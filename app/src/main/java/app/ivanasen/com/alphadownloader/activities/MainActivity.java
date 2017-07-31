package app.ivanasen.com.alphadownloader.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.aspsine.multithreaddownload.DownloadConfiguration;
import com.aspsine.multithreaddownload.DownloadManager;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

import app.ivanasen.com.alphadownloader.R;
import app.ivanasen.com.alphadownloader.fragments.DownloadsFragment;
import app.ivanasen.com.alphadownloader.util.AppRater;
import app.ivanasen.com.alphadownloader.util.Utility;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final long ID_BROWSER = 1;
    private static final long ID_SPEED_TEST = 2;
    private static final long ID_INVITE_FRIENDS = 3;
    private static final long ID_RATE = 4;
    private static final long ID_BOOKMARKS = 5;
    private static final long ID_CUSTOMER_CARE = 6;
    private static final long ID_PREFERENCES = 7;
    private static final long ID_DOWNLOADS = 8;
    public static final String INTENT_DOWNLOAD = "com.ivanasen.com.alphadownloader.download";

    private Drawer mDrawer;
    private DownloadsPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        onCreateDrawer(toolbar);

        //Tab navigation
        mPagerAdapter = new DownloadsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);

        initDownloader();
    }

    private void initDownloader() {
        DownloadConfiguration configuration = new DownloadConfiguration();
        configuration.setMaxThreadNum(10);
        configuration.setThreadNum(3);
        DownloadManager.getInstance().init(getApplicationContext(), configuration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onCreateDrawer(Toolbar toolbar) {
        List<IDrawerItem> drawerItems = new ArrayList<>();
        drawerItems.add(
                new PrimaryDrawerItem().withIdentifier(ID_DOWNLOADS).withName(R.string.downloads_title_drawer)
                        .withIcon(GoogleMaterial.Icon.gmd_file_download)
                        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                            Utility.startActivity(this, MainActivity.class);
                            mDrawer.closeDrawer();
                            return true;
                        })
        );
        drawerItems.add(
                new PrimaryDrawerItem().withIdentifier(ID_BROWSER).withName(R.string.browser_title)
                        .withIcon(GoogleMaterial.Icon.gmd_public)
                        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                            Utility.startActivity(this, BrowserActivity.class);
                            mDrawer.closeDrawer();
                            return true;
                        })
        );
        drawerItems.add(
                new PrimaryDrawerItem().withIdentifier(ID_SPEED_TEST).withName(R.string.speed_test_title)
                        .withIcon(GoogleMaterial.Icon.gmd_network_check)
                        .withOnDrawerItemClickListener((view, position, drawerItem) -> true)
        );
        drawerItems.add(new DividerDrawerItem());
        drawerItems.add(
                new PrimaryDrawerItem().withIdentifier(ID_INVITE_FRIENDS).withName(R.string.invite_friends_title)
                        .withIcon(GoogleMaterial.Icon.gmd_send)
                        .withOnDrawerItemClickListener((view, position, drawerItem) -> true)
        );
        drawerItems.add(
                new PrimaryDrawerItem().withIdentifier(ID_RATE).withName(R.string.rate_title)
                        .withIcon(GoogleMaterial.Icon.gmd_star)
                        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                            AppRater.rateApp(this);
                            return true;
                        })
        );
        drawerItems.add(
                new PrimaryDrawerItem().withIdentifier(ID_BOOKMARKS).withName(R.string.bookmarks_title)
                        .withIcon(GoogleMaterial.Icon.gmd_flag)
                        .withOnDrawerItemClickListener((view, position, drawerItem) -> true)
        );
        drawerItems.add(
                new PrimaryDrawerItem().withIdentifier(ID_CUSTOMER_CARE).withName(R.string.customer_care_title)
                        .withIcon(GoogleMaterial.Icon.gmd_question_answer)
                        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                            Utility.sendCustomerCareEmail(this);
                            return true;
                        })
        );
        drawerItems.add(new DividerDrawerItem());
        drawerItems.add(
                new PrimaryDrawerItem().withIdentifier(ID_PREFERENCES).withName(R.string.preferences_title)
                        .withIcon(GoogleMaterial.Icon.gmd_settings)
                        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                            Utility.startActivity(this, SettingsActivity.class);
                            return true;
                        })
        );

        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withTranslucentStatusBar(false)
                .withHeader(R.layout.drawer_header)
                .withDrawerItems(drawerItems)
                .build();
    }

    public class DownloadsPagerAdapter extends FragmentPagerAdapter {

        private Fragment mRunningDownloads;
        private Fragment mAllDownloads;

        public DownloadsPagerAdapter(FragmentManager fm) {
            super(fm);
            mRunningDownloads = DownloadsFragment.newInstance(DownloadsFragment.RUNNING_DOWNLOADS);
            mAllDownloads = DownloadsFragment.newInstance(DownloadsFragment.ALL_DOWNLOADS);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mRunningDownloads;
                case 1:
                    return mAllDownloads;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.running_downloads_title);
                case 1:
                    return getString(R.string.all_downloads_title);
                default:
                    return null;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        @SuppressLint("RestrictedApi")
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
}
