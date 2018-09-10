package group.tonight.electricityfeehelper.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.socks.library.KLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import group.tonight.electricityfeehelper.MainApp;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.crud.UserDatabase;
import group.tonight.electricityfeehelper.dao.User;
import group.tonight.electricityfeehelper.dao.UserBean;
import group.tonight.electricityfeehelper.fragments.UserListFragment;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class IndexActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "功能未实现", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frag_container, UserListFragment.newInstance(1))
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
//            moveTaskToBack(true);//主页按后退键转后台运行不退出
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.nav_camera:
                // Handle the camera action
                Toast.makeText(this, "导入", Toast.LENGTH_SHORT).show();
                new ExcelParseTask(this).execute();
                break;
            case R.id.nav_gallery:
                Toast.makeText(this, "画廊", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_slideshow:
                Toast.makeText(this, "幻灯片", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_manage:
                Toast.makeText(this, "工具", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_share:
                Toast.makeText(this, "分享", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_send:
                Toast.makeText(this, "发送", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Java读取Excel数据并转换成Json格式
     * https://blog.csdn.net/qq_27993003/article/details/52488403
     */
    public static class ExcelParseTask extends AsyncTask<Void, Void, Void> {
        private AssetManager mAssetManager;

        public ExcelParseTask(Context context) {
            mAssetManager = context.getApplicationContext().getAssets();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String[] fileNameArrays = mAssetManager.list("");
                List<String> fileNameList = new ArrayList<>();
                for (String fileName : fileNameArrays) {
                    if (fileName.endsWith(".xls")) {
                        fileNameList.add(fileName);
                    }
                }
                List<User> totalUserList = new ArrayList<>();
                for (String fileName : fileNameList) {
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
                                for (int m = 0; m < columns; m++) {
                                    //sheet.getCell(列，行);
                                    Cell cell = sheet.getCell(m, i);
                                    String contents = cell.getContents();
                                    jsonObject.put(keyList.get(m), contents);
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
                KLog.e();
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


            return null;
        }
    }
}
