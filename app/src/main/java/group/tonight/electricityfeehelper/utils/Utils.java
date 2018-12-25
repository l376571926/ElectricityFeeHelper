package group.tonight.electricityfeehelper.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.text.DecimalFormat;

import group.tonight.electricityfeehelper.R;

public class Utils {
    private static DecimalFormat mDecimalFormat = new DecimalFormat("0.00");//格式化小数

    /**
     * https://blog.csdn.net/snow_ice_yang/article/details/78131819
     *
     * @param size
     * @return
     */
    public static String getPrintSize(long size) {
        //获取到的size为：1705230
        int GB = 1024 * 1024 * 1024;//定义GB的计算常量
        int MB = 1024 * 1024;//定义MB的计算常量
        int KB = 1024;//定义KB的计算常量

        String resultSize;
        if (size / GB >= 1) {
            //如果当前Byte的值大于等于1GB
            resultSize = mDecimalFormat.format(size / (float) GB) + "GB";
        } else if (size / MB >= 1) {
            //如果当前Byte的值大于等于1MB
            resultSize = mDecimalFormat.format(size / (float) MB) + "MB";
        } else if (size / KB >= 1) {
            //如果当前Byte的值大于等于1KB
            resultSize = mDecimalFormat.format(size / (float) KB) + "KB";
        } else {
            resultSize = size + "B";
        }
        return resultSize;
    }

    /**
     * 安装apk
     *
     * @param context
     * @param apkPath
     */
    public static void installApk(Context context, String apkPath) {
        try {
            /**
             * provider
             * 处理android 7.0 及以上系统安装异常问题
             */
            File file = new File(apkPath);
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String fileType = "application/vnd.android.package-archive";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(context, context.getString(R.string.file_provider_authorities), file);//在AndroidManifest中的android:authorities值
                Log.d("======", "apkUri=" + apkUri);
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                install.setDataAndType(apkUri, fileType);
            } else {
                install.setDataAndType(Uri.fromFile(file), fileType);
            }
            context.startActivity(install);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("======", e.getMessage());
        }
    }
}
