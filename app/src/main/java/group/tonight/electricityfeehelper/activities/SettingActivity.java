package group.tonight.electricityfeehelper.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import group.tonight.downloadmanagerhelper.DownloadManagerHelper;
import group.tonight.electricityfeehelper.R;
import group.tonight.myandroidlibrary.Release;
import group.tonight.myandroidlibrary.UpdateTask;

public class SettingActivity extends BackEnableActivity implements View.OnClickListener {
    private DownloadManagerHelper mDownloadManagerHelper;
    private ProgressDialog mProgressDialog;

    private String mNewApkUrl;
    private View mNewVersionLabel;

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


        mNewVersionLabel = findViewById(R.id.new_version);

        findViewById(R.id.update).setOnClickListener(this);
        findViewById(R.id.update_order).setOnClickListener(this);
        findViewById(R.id.update_user).setOnClickListener(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("用户数据下载中，请稍等");


        UpdateTask updateTask = new UpdateTask();
        updateTask.setCallback(new UpdateTask.ResultCallback() {
            @Override
            public void onFailure(Exception e) {

            }

            @Override
            public void onResponse(Release release) {
                String tag_name = release.getTag_name();
                String name = release.getName();
                String body = release.getBody();
                PackageManager packageManager = getPackageManager();
                try {
                    PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
                    String versionName = packageInfo.versionName;
                    if (!versionName.equals(tag_name)) {
                        mNewApkUrl = release.getAssets().get(0).getBrowser_download_url();
                        mNewVersionLabel.setVisibility(View.VISIBLE);
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        new Thread(updateTask).start();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.update:
                if (mNewApkUrl == null) {
                    Toast.makeText(getApplicationContext(), "已是最新版本", Toast.LENGTH_SHORT).show();
                } else {
                    versionUpdate(mNewApkUrl);
                }
                break;
            case R.id.update_order:
                break;
            case R.id.update_user:
                break;
            default:
                break;
        }
    }

}
