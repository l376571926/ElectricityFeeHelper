package group.tonight.electricityfeehelper.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.socks.library.KLog;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

import group.tonight.electricityfeehelper.BR;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.dao.DownloadFirImBean;
import group.tonight.electricityfeehelper.dao.UrlBean;
import group.tonight.electricityfeehelper.utils.Utils;

public class SettingActivity extends BackEnableActivity implements View.OnClickListener {

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
        View mNewVersionLabel = findViewById(R.id.new_version);

        findViewById(R.id.update).setOnClickListener(this);

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            setLayoutData(BR.data, packageInfo.versionName);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.update:
                checkUpdate(this, true);
                break;
            default:
                break;
        }
    }

    public static void checkUpdate(final Context activity, final boolean showToast) {
        try {
            final PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            final int versionCode = packageInfo.versionCode;
            final String versionName = packageInfo.versionName;

            OkGo.<DownloadFirImBean>get("https://download.fir.im/b6wk")
                    .execute(new AbsCallback<DownloadFirImBean>() {
                        @Override
                        public void onSuccess(Response<DownloadFirImBean> response) {
                            DownloadFirImBean downloadFirImBean = response.body();
                            System.out.println();
                            DownloadFirImBean.AppBean appBean = downloadFirImBean.getApp();
                            final DownloadFirImBean.AppBean.ReleasesBean.MasterBean masterBean = appBean.getReleases().getMaster();

                            final String build = masterBean.getBuild();
                            final String version = masterBean.getVersion();
                            if (String.valueOf(versionCode).equals(build) && versionName.equals(version)) {
                                if (showToast) {
                                    Toast.makeText(activity, "已是最新版本", Toast.LENGTH_SHORT).show();
                                }
                                KLog.e("已是最新版本");
                                return;
                            }
                            OkGo.<UrlBean>post("https://download.fir.im/apps/" + appBean.getId() + "/install")
                                    .params("download_token", appBean.getToken())
                                    .params("release_id", masterBean.getId())
                                    .execute(new AbsCallback<UrlBean>() {
                                        @Override
                                        public void onSuccess(Response<UrlBean> response) {
                                            UrlBean urlBean = response.body();
                                            final String url = urlBean.getUrl();//https://pro-bd.fir.im/e0e3d98fe4968f9483195df70d215b62bef3f557.apk?auth_key=1545722213-0-9b1583649b4a4026bdaa53857e31cd6e-261661ae3e7eb73273851b93b26405d6
                                            System.out.println();

                                            String builder = "版本号：" + version
                                                    + "\n"
                                                    + "安装包大小：" + Utils.getPrintSize(masterBean.getFsize())
                                                    + "\n"
                                                    + "发布时间："
                                                    + "\n"
                                                    + DateFormat.getDateTimeInstance().format(new Date(masterBean.getCreated_at() * 1000))
                                                    + "\n"
                                                    + "更新内容："
                                                    + "\n"
                                                    + masterBean.getChangelog();

                                            new AlertDialog.Builder(activity)
                                                    .setTitle("发现新版本")
                                                    .setIcon(R.mipmap.ic_launcher)
                                                    .setMessage(builder)
                                                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    })
                                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            String fileName = null;
                                                            if (url.contains("?")) {
                                                                if (url.contains("/")) {
                                                                    fileName = url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"));
                                                                    System.out.println();
                                                                }
                                                            }
                                                            final ProgressDialog mProgressDialog = new ProgressDialog(activity);
                                                            mProgressDialog.setMessage("下载中。。。");
                                                            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                                            mProgressDialog.setMax(100);
                                                            mProgressDialog.show();
                                                            OkGo.<File>get(url)
                                                                    .execute(new FileCallback(Environment.getExternalStorageDirectory().getPath(), fileName == null ? "app-release.apk" : fileName) {
                                                                        @Override
                                                                        public void downloadProgress(Progress progress) {
                                                                            super.downloadProgress(progress);
                                                                            mProgressDialog.setProgress((int) (progress.fraction * 100));
                                                                        }

                                                                        @Override
                                                                        public void onSuccess(Response<File> response) {
                                                                            mProgressDialog.dismiss();
                                                                            Utils.installApk(activity, response.body().getPath());
                                                                        }
                                                                    });
                                                        }
                                                    })
                                                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                        @Override
                                                        public void onDismiss(DialogInterface dialog) {

                                                        }
                                                    })
                                                    .show();
                                        }

                                        @Override
                                        public UrlBean convertResponse(okhttp3.Response response) throws Throwable {
                                            return new Gson().fromJson(response.body().string(), UrlBean.class);
                                        }
                                    });
                        }

                        @Override
                        public DownloadFirImBean convertResponse(okhttp3.Response response) throws Throwable {
                            return new Gson().fromJson(response.body().charStream(), DownloadFirImBean.class);
                        }
                    });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
