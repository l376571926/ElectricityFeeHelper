package group.tonight.electricityfeehelper.utils;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import group.tonight.electricityfeehelper.crud.UserDatabase;
import group.tonight.electricityfeehelper.dao.User;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * Java读取Excel数据并转换成Json格式
 * https://blog.csdn.net/qq_27993003/article/details/52488403
 */
public class ExcelParseTask implements Runnable, GenericLifecycleObserver {
    private AssetManager mAssetManager;
    private OnProgressListener onProgressListener;
    private Lifecycle mLifecycle;

    public ExcelParseTask(Context context) {
        mAssetManager = context.getApplicationContext().getAssets();
        if (context instanceof LifecycleOwner) {
            mLifecycle = ((LifecycleOwner) context).getLifecycle();
            mLifecycle.addObserver(this);
        }
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    @Override
    public void run() {
        try {
            String[] fileNameArrays = mAssetManager.list("");
            List<String> fileNameList = new ArrayList<>();
            for (String fileName : fileNameArrays) {
                if (fileName.endsWith(".xls")) {
                    fileNameList.add(fileName);
                }
            }
            List<User> totalUserList = new ArrayList<>();
            int size = fileNameList.size();
            for (int m = 0; m < size; m++) {
                if (mLifecycle == null) {
                    break;
                }
                if (onProgressListener != null) {
                    onProgressListener.onProgress(size, m + 1);
                }
                String fileName = fileNameList.get(m);
                Workbook workbook = Workbook.getWorkbook(mAssetManager.open(fileName));
                int numberOfSheets = workbook.getNumberOfSheets();
                if (numberOfSheets != 0) {
                    Sheet sheet = workbook.getSheet(0);
                    int rows = sheet.getRows();//获取总共多少行
                    int columns = sheet.getColumns();//获取总共多少列
                    List<String> keyList = new ArrayList<>();
                    keyList.add("userId");
                    keyList.add("userName");
                    keyList.add("userPhone");
                    keyList.add("powerLineId");
                    keyList.add("powerLineName");
                    keyList.add("meterReadingDay");
                    keyList.add("meterReader");
                    keyList.add("measurementPointId");
                    keyList.add("meterReadingId");
                    keyList.add("powerMeterId");
                    keyList.add("powerValueType");
                    keyList.add("lastPowerValue");
                    keyList.add("currentPowerValue");
                    keyList.add("consumePowerValue");
                    keyList.add("comprehensiveRatio");
                    keyList.add("meterReadingNumber");
                    keyList.add("exceptionTypes");
                    keyList.add("meterReadingStatus");
                    keyList.add("powerSupplyId");
                    keyList.add("powerSupplyName");
                    keyList.add("userAddress");

                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < rows; i++) {
                        if (i == 0) {
//                            for (int m = 0; m < columns; m++) {
//                                //sheet.getCell(列，行);
//                                Cell cell = sheet.getCell(m, i);
//                                String contents = cell.getContents();
//                                keyList.add(contents);
//                            }
                            continue;
                        } else {
                            //循环读取第i行的所有列的数据
                            JSONObject jsonObject = new JSONObject();
                            for (int n = 0; n < columns; n++) {
                                //sheet.getCell(列，行);
                                Cell cell = sheet.getCell(n, i);
                                String contents = cell.getContents();
                                jsonObject.put(keyList.get(n), contents);
                            }
                            jsonArray.put(jsonObject);
                        }
                    }
                    Type type = new TypeToken<List<User>>() {
                    }.getType();
                    List<User> beanList = new Gson().fromJson(jsonArray.toString(), type);
                    totalUserList.addAll(beanList);
                }
            }
            UserDatabase.get()
                    .getUserDao()
                    .insert(totalUserList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            System.out.println(event);
            if (mLifecycle != null) {
                mLifecycle.removeObserver(this);
                mLifecycle = null;
            }
        }
    }

    public interface OnProgressListener {
        void onProgress(int total, int progress);
    }
}