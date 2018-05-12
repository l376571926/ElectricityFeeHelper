package group.tonight.electricityfeehelper.activities;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;

import com.socks.library.KLog;

import group.tonight.electricityfeehelper.R;

public class SettingActivity extends BackEnableActivity {
    private IntentFilter mDownloadIntentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
    private long mDownloadId;
    private DownloadManager mDownloadManager;

    @Override
    protected int setChildLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected String setActivityName() {
        return "设置";
    }

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
