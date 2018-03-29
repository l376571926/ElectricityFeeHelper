package group.tonight.electricityfeehelper.activities;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.socks.library.KLog;

import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.dao.User;
import group.tonight.electricityfeehelper.fragments.OrderListFragment;
import group.tonight.electricityfeehelper.fragments.SettingFragment;
import group.tonight.electricityfeehelper.fragments.UserListFragment;
import group.tonight.electricityfeehelper.interfaces.OnListFragmentInteractionListener;

public class HomeActivity extends AppCompatActivity implements OnListFragmentInteractionListener {
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mToolbar.setTitle("首页");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frag_container, OrderListFragment.newInstance(1))
                            .commit();
                    return true;
                case R.id.navigation_dashboard:
                    mToolbar.setTitle("用户库");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frag_container, UserListFragment.newInstance(1))
                            .commit();
                    return true;
                case R.id.navigation_notifications:
                    mToolbar.setTitle("设置");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frag_container, SettingFragment.newInstance())
                            .commit();
                    return true;
            }
            return false;
        }
    };
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("首页");
        setSupportActionBar(mToolbar);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frag_container, OrderListFragment.newInstance(1))
                .commit();
    }

    @Override
    public void onListFragmentInteraction(User item) {
        Intent intent = new Intent(this, OrderListActivity.class);
        intent.putExtra("_id", item.getId());
        startActivity(intent);
    }

    private IntentFilter mDownloadIntentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mDownloadReceiver, mDownloadIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mDownloadReceiver);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        moveTaskToBack(true);//主页按后退键转后台运行不退出
    }

    private long mDownloadId;
    private DownloadManager mDownloadManager;

    public void versionUpdate(final String apkUrl) {
        new AlertDialog.Builder(this)
                .setMessage("发现新版本，是否立即下载？")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mDownloadManager == null) {
                            mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        }
                        mDownloadId = mDownloadManager.enqueue(new DownloadManager.Request(Uri.parse(apkUrl)));
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private BroadcastReceiver mDownloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                if (downId == mDownloadId) {
                    if (mDownloadManager != null) {
                        Uri uri = mDownloadManager.getUriForDownloadedFile(downId);
                        try {
                            Intent apkIntent = new Intent();
                            apkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            apkIntent.setAction(Intent.ACTION_VIEW);
                            apkIntent.setDataAndType(uri, "application/vnd.android.package-archive");
                            startActivity(apkIntent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            KLog.e(e.getMessage());
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            //Caused by: android.content.ActivityNotFoundException: No Activity found to handle Intent { act=android.intent.action.VIEW dat=content://downloads/my_downloads/1 typ=application/vnd.android.package-archive flg=0x10000000 }
                        }
                    }
                }
            }
        }
    };
}
