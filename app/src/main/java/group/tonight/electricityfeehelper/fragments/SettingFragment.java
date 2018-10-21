package group.tonight.electricityfeehelper.fragments;

import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import group.tonight.downloadmanagerhelper.DownloadManagerHelper;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.activities.SettingActivity;
import group.tonight.myandroidlibrary.Release;
import group.tonight.myandroidlibrary.UpdateTask;

/**
 * 设置
 */
public class SettingFragment extends Fragment implements View.OnClickListener {
    private ProgressDialog mProgressDialog;

    private String mNewApkUrl;
    private View mNewVersionLabel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        mNewVersionLabel = view.findViewById(R.id.new_version);

        view.findViewById(R.id.update).setOnClickListener(this);
        view.findViewById(R.id.update_order).setOnClickListener(this);
        view.findViewById(R.id.update_user).setOnClickListener(this);

        mProgressDialog = new ProgressDialog(getContext());
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
                if (getActivity() == null) {
                    return;
                }
                PackageManager packageManager = getActivity().getPackageManager();
                try {
                    PackageInfo packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
                    String versionName = packageInfo.versionName;
                    if (!versionName.equals(tag_name)) {
                        Toast.makeText(getContext(), "发现新版本", Toast.LENGTH_SHORT).show();
                        mNewApkUrl = release.getAssets().get(0).getBrowser_download_url();
                        mNewVersionLabel.setVisibility(View.VISIBLE);
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        new Thread(updateTask).start();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.update:
                if (getActivity() != null) {
                    if (mNewApkUrl == null) {
                        Toast.makeText(getActivity().getApplicationContext(), "已是最新版本", Toast.LENGTH_SHORT).show();
                    } else {
                        if (getActivity() instanceof SettingActivity) {
                            SettingActivity homeActivity = (SettingActivity) getActivity();
                            homeActivity.versionUpdate(mNewApkUrl);
                        }
                    }
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

    @Override
    public void onPause() {
        super.onPause();
        mProgressDialog.dismiss();
    }
}
