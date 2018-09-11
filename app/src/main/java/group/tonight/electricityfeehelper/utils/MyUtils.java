package group.tonight.electricityfeehelper.utils;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by liyiwei on 2018/2/21.
 */

public class MyUtils {
//    public static final String LATEST_USER_URL = "https://raw.githubusercontent.com/l376571926/l376571926.github.io/master/nydlj_latest_user.json";
//    public static final String LATEST_ORDER_URL = "https://raw.githubusercontent.com/l376571926/l376571926.github.io/master/nydlj_latest_order.json";
//    public static final String LATEST_UPDATE_URL = "https://raw.githubusercontent.com/l376571926/l376571926.github.io/master/nydlj_latest_update.json";

    /**
     * GitHub Release Api
     */
    public static final String LATEST_GITHUB_UPDATE_URL = "https://api.github.com/repos/l376571926/ElectricityFeeHelper/releases/latest";

    private static DecimalFormat mDecimalFormat = new DecimalFormat("###,###,##0.00");

    public static String formatDecimal(double amount) {
        return mDecimalFormat.format(amount);
    }

    public static SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMM", Locale.getDefault());

    /**
     * 设置View为有水波纹点击效果
     *
     * @param itemView
     */
    public static void setBtnWaterBg(View itemView) {
        /**
         * 设置水波纹背景
         */
        if (itemView.getBackground() == null) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = itemView.getContext().getTheme();
            int top = itemView.getPaddingTop();
            int bottom = itemView.getPaddingBottom();
            int left = itemView.getPaddingLeft();
            int right = itemView.getPaddingRight();
            if (theme.resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true)) {
                itemView.setBackgroundResource(typedValue.resourceId);
            }
            itemView.setPadding(left, top, right, bottom);
        }
    }

}
