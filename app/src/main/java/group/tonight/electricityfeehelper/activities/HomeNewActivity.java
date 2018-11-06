package group.tonight.electricityfeehelper.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;


import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;
import com.socks.library.KLog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import group.tonight.electricityfeehelper.Application;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.dao.ListResponseBean;
import group.tonight.electricityfeehelper.dao.User;
import group.tonight.electricityfeehelper.fragments.AddUserFragment;

// TODO: 2018/10/22 0022 添加扫电能表条形码查询用户数据功能
public class HomeNewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TextView mCountView;
    private RecyclerView mRecyclerView;
    private List<User> mSrcUserList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mCountView = (TextView) findViewById(R.id.count);

        // Set the adapter
        final Context context = mRecyclerView.getContext();

        mRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(mBaseQuickAdapter);
        mBaseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                User item = mBaseQuickAdapter.getItem(position);
                if (item == null) {
                    return;
                }
                Intent intent = new Intent(view.getContext(), UserInfoActivity.class);
                intent.putExtra("data", item);
                startActivity(intent);
            }
        });
        OkGo.<ListResponseBean<User>>get(Application.BASE_HOST + "/feehelper/user/list")
                .cacheTime(24 * 3600 * 1000)
                .cacheMode(CacheMode.IF_NONE_CACHE_REQUEST)
                .execute(new AbsCallback<ListResponseBean<User>>() {
                    @Override
                    public void onSuccess(Response<ListResponseBean<User>> response) {
                        try {
                            mSrcUserList.clear();
                            mSrcUserList.addAll(response.body().getData());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        refreshData(response);
                    }

                    @Override
                    public void onCacheSuccess(Response<ListResponseBean<User>> response) {
                        super.onCacheSuccess(response);
                        try {
                            mSrcUserList.clear();
                            mSrcUserList.addAll(response.body().getData());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        refreshData(response);
                    }

                    @Override
                    public group.tonight.electricityfeehelper.dao.ListResponseBean<User> convertResponse(okhttp3.Response response) throws Throwable {
                        String json = response.body().string();
                        Type type = new TypeToken<ListResponseBean<User>>() {
                        }.getType();
                        return new Gson().fromJson(json, type);
                    }
                });
    }

    private void refreshData(Response<ListResponseBean<User>> response) {
        ListResponseBean<User> body = response.body();
        List<User> userList = body.getData();

        List<User> userArrayList = new ArrayList<>(userList);
        mCountView.setText(userArrayList.size() + "");
        mBaseQuickAdapter.replaceData(userArrayList);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_new, menu);

        SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        mSearchView.setQueryHint("电能表编号、姓名、手机、用户编号");
//        mSearchView.setIconifiedByDefault(false);//false表示加载后默认为搜索框输入状态
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                KLog.e(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                KLog.e(newText);
                List<User> resultList = new ArrayList<>();
                for (User user : mSrcUserList) {
                    String userId = user.getUserId();
                    String userName = user.getUserName();
                    String powerMeterId = user.getPowerMeterId();
                    String userPhone = user.getUserPhone();
                    if (userId.contains(newText) || userName.contains(newText) || powerMeterId.contains(newText) || userPhone.contains(newText)) {
                        resultList.add(user);
                    }
                }
                mBaseQuickAdapter.replaceData(resultList);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                break;
            case R.id.action_add_user:
                AddUserFragment.newInstance(new User()).show(getSupportFragmentManager(), "");
                break;
            case R.id.nav_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private BaseQuickAdapter<User, BaseViewHolder> mBaseQuickAdapter = new BaseQuickAdapter<User, BaseViewHolder>(R.layout.fragment_user) {
        @Override
        protected void convert(BaseViewHolder helper, User item) {
            helper.setText(R.id.power_meter_id, getString(R.string.power_meter_id_place_holder, item.getPowerMeterId()));
            helper.setText(R.id.id, getString(R.string.user_id_place_holder, item.getUserId()));
            helper.setText(R.id.content, getString(R.string.user_name_place_holder, item.getUserName()));
            helper.setText(R.id.phone, getString(R.string.user_phone_place_holder, item.getUserPhone()));
        }
    };
}
