package group.tonight.electricityfeehelper.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.socks.library.KLog;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import group.tonight.electricityfeehelper.BR;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.databinding.ActivitySettingBinding;
import group.tonight.electricityfeehelper.utils.Utils;

public class SettingActivity extends BackEnableActivity implements View.OnClickListener {
    private String mUpdateJson;

    private ImageView mQrCodeImageView;

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
        setLayoutData(BR.handler, this);
        ActivitySettingBinding binding = (ActivitySettingBinding) getDataBinding();
        mQrCodeImageView = binding.qrCode;

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            setLayoutData(BR.data, packageInfo.versionName);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        OkGo.<String>get("https://api.github.com/repos/l376571926/ElectricityFeeHelper/releases/latest")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String json = response.body();
                            mUpdateJson = json;
                            JSONObject jsonObject = new JSONObject(json);
                            final String browser_download_url = jsonObject.getJSONArray("assets").getJSONObject(0).getString("browser_download_url");

                            Bitmap bitmap = CodeUtils.createImage(browser_download_url, 400, 400, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                            mQrCodeImageView.setImageBitmap(bitmap);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.update) {
            try {
                final PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                final String versionName = packageInfo.versionName;
                try {
                    JSONObject jsonObject = new JSONObject(mUpdateJson);
                    String tag_name = jsonObject.getString("tag_name");
                    String updateLog = jsonObject.getString("body");

                    JSONObject assets = jsonObject.getJSONArray("assets").getJSONObject(0);

                    final String browser_download_url = assets.getString("browser_download_url");
                    String apkSize = assets.getString("size");
                    String updated_at = assets.getString("updated_at");

                    if (versionName.equals(tag_name)) {
                        Toast.makeText(this, "已是最新版本", Toast.LENGTH_SHORT).show();
                        KLog.e("已是最新版本");
                        return;
                    }

                    String builder = "版本号：" + tag_name
                            + "\n"
                            + "安装包大小：" + Utils.getPrintSize(Long.parseLong(apkSize))
                            + "\n"
                            + "发布时间："
                            + "\n"
                            + updated_at
                            + "\n"
                            + "更新内容："
                            + "\n"
                            + updateLog;

                    new AlertDialog.Builder(this)
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
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(browser_download_url)));
                                }
                            })
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {

                                }
                            })
                            .show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
