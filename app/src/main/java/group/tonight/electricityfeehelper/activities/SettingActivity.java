package group.tonight.electricityfeehelper.activities;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;

import com.socks.library.KLog;

import group.tonight.downloadmanagerhelper.DownloadManagerHelper;
import group.tonight.electricityfeehelper.R;

public class SettingActivity extends BackEnableActivity {
    private DownloadManagerHelper mDownloadManagerHelper;

    @Override
    protected int setChildLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected String setActivityName() {
        return "设置";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDownloadManagerHelper = new DownloadManagerHelper(this);
    }

    public void versionUpdate(final String apkUrl) {
        new AlertDialog.Builder(this)
                .setMessage("发现新版本，是否立即下载？")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDownloadManagerHelper.enqueue(apkUrl);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

}
