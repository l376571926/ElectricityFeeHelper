package group.tonight.electricityfeehelper.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Base64;
import android.util.TypedValue;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import group.tonight.electricityfeehelper.MainApp;
import group.tonight.electricityfeehelper.dao.DaoSession;
import group.tonight.electricityfeehelper.dao.User;
import group.tonight.electricityfeehelper.dao.UserBean;
import group.tonight.electricityfeehelper.dao.UserDao;

/**
 * Created by liyiwei on 2018/2/21.
 */

public class MyUtils {
    public static final String LATEST_USER_URL = "https://raw.githubusercontent.com/l376571926/l376571926.github.io/master/nydlj_latest_user.json";
    public static final String LATEST_ORDER_URL = "https://raw.githubusercontent.com/l376571926/l376571926.github.io/master/nydlj_latest_order.json";
    public static final String LATEST_UPDATE_URL = "https://raw.githubusercontent.com/l376571926/l376571926.github.io/master/nydlj_latest_update.json";

    /**
     * 判断是否为整数
     *
     * @param str 传入的字符串
     * @return 是整数返回true, 否则返回false
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

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


    private static boolean sameUser(User user, UserBean userBean) {
        if (!TextUtils.equals(user.getUserId(), userBean.get用户编号())) {
            return false;
        }
        if (!TextUtils.equals(user.getUserName(), userBean.get用户名称())) {
            return false;
        }
        if (!TextUtils.equals(user.getUserPhone(), userBean.get联系方式())) {
            return false;
        }
        if (!TextUtils.equals(user.getPowerLineId(), userBean.get抄表段编号())) {
            return false;
        }
        if (!TextUtils.equals(user.getPowerLineName(), userBean.get抄表段名称())) {
            return false;
        }
        if (!TextUtils.equals(user.getMeterReadingDay(), userBean.get抄表例日())) {
            return false;
        }
        if (!TextUtils.equals(user.getMeterReader(), userBean.get抄表员())) {
            return false;
        }
        if (!TextUtils.equals(user.getMeasurementPointId(), userBean.get计量点编号())) {
            return false;
        }
        if (!TextUtils.equals(user.getMeterReadingId(), userBean.get抄表序号())) {
            return false;
        }
        if (!TextUtils.equals(user.getPowerMeterId(), userBean.get电能表编号())) {
            return false;
        }
        if (!TextUtils.equals(user.getPowerValueType(), userBean.get示数类型())) {
            return false;
        }
        if (!TextUtils.equals(user.getLastPowerValue(), userBean.get上次示数())) {
            return false;
        }
        if (!TextUtils.equals(user.getCurrentPowerValue(), userBean.get本次示数())) {
            return false;
        }
        if (!TextUtils.equals(user.getConsumePowerValue(), userBean.get抄见电量())) {
            return false;
        }
        if (!TextUtils.equals(user.getComprehensiveRatio(), userBean.get综合倍率())) {
            return false;
        }
        if (!TextUtils.equals(user.getMeterReadingNumber(), userBean.get抄表位数())) {
            return false;
        }
        if (!TextUtils.equals(user.getExceptionTypes(), userBean.get异常类型())) {
            return false;
        }
        if (!TextUtils.equals(user.getMeterReadingStatus(), userBean.get抄表状态())) {
            return false;
        }
        if (!TextUtils.equals(user.getPowerSupplyId(), userBean.get供电单位())) {
            return false;
        }
        if (!TextUtils.equals(user.getPowerSupplyName(), userBean.get供电所())) {
            return false;
        }
        if (!TextUtils.equals(user.getUserAddress(), userBean.get用户地址())) {
            return false;
        }
        return true;
    }

    public static int[] saveUserListToDb(Activity activity, byte[] base64bytes) {
        String json = new String(Base64.decode(base64bytes, Base64.DEFAULT));

        int mAddCount = 0;
        int mUpdateCount = 0;
        try {
            JSONArray jsonArray1 = new JSONArray(json);

            Type type = new TypeToken<List<UserBean>>() {
            }.getType();
            List<UserBean> userBeanList = new Gson().fromJson(jsonArray1.toString(), type);

            for (UserBean userBean : userBeanList) {
                int status = MyUtils.saveUserToDb(activity, userBean);
                if (status == 1) {
                    mAddCount++;
                } else if (status == 2) {
                    mUpdateCount++;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new int[]{mAddCount, mUpdateCount};
    }

    /**
     * @param activity
     * @param userBean
     * @return 1新增2更新
     */
    public static int saveUserToDb(Activity activity, UserBean userBean) {
        String 用户编号 = userBean.get用户编号();
        String 用户名称 = userBean.get用户名称();
        String 联系方式 = userBean.get联系方式();
        String 抄表段编号 = userBean.get抄表段编号();
        String 抄表段名称 = userBean.get抄表段名称();
        String 抄表例日 = userBean.get抄表例日();
        String 抄表员 = userBean.get抄表员();
        String 计量点编号 = userBean.get计量点编号();
        String 抄表序号 = userBean.get抄表序号();
        String 电能表编号 = userBean.get电能表编号();
        String 示数类型 = userBean.get示数类型();
        String 上次示数 = userBean.get上次示数();
        String 本次示数 = userBean.get本次示数();
        String 抄见电量 = userBean.get抄见电量();
        String 综合倍率 = userBean.get综合倍率();
        String 抄表位数 = userBean.get抄表位数();
        String 异常类型 = userBean.get异常类型();
        String 抄表状态 = userBean.get抄表状态();
        String 供电单位 = userBean.get供电单位();
        String 供电所 = userBean.get供电所();
        String 用户地址 = userBean.get用户地址();

        DaoSession daoSession = ((MainApp) activity.getApplication()).getDaoSession();
        UserDao userDao = daoSession.getUserDao();

        User unique = userDao.queryBuilder()
                .where(UserDao.Properties.UserId.eq(用户编号))
                .build()
                .unique();

        boolean isNew = false;
        if (unique == null) {
            isNew = true;
            unique = new User();
        } else {
            if (sameUser(unique, userBean)) {
                return 0;
            }
        }
        unique.setUserId(用户编号);
        unique.setUserName(用户名称);
        unique.setUserPhone(联系方式);
        unique.setPowerLineId(抄表段编号);
        unique.setPowerLineName(抄表段名称);
        unique.setMeterReadingDay(抄表例日);
        unique.setMeterReader(抄表员);
        unique.setMeasurementPointId(计量点编号);
        unique.setMeterReadingId(抄表序号);
        unique.setPowerMeterId(电能表编号);
        unique.setPowerValueType(示数类型);
        unique.setLastPowerValue(上次示数);
        unique.setCurrentPowerValue(本次示数);
        unique.setConsumePowerValue(抄见电量);
        unique.setComprehensiveRatio(综合倍率);
        unique.setMeterReadingNumber(抄表位数);
        unique.setExceptionTypes(异常类型);
        unique.setMeterReadingStatus(抄表状态);
        unique.setPowerSupplyId(供电单位);
        unique.setPowerSupplyName(供电所);
        unique.setUserAddress(用户地址);

        long currentTimeMillis = System.currentTimeMillis();
        unique.setUpdateTime(currentTimeMillis);
        if (isNew) {
            unique.setCreateTime(currentTimeMillis);
            userDao.insert(unique);
            return 1;
        } else {
            userDao.update(unique);
            return 2;
        }
    }
}
