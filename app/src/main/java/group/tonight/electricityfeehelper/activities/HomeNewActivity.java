package group.tonight.electricityfeehelper.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;


import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import group.tonight.electricityfeehelper.App;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.dao.PageUserListBean;
import group.tonight.electricityfeehelper.dao.User;

// TODO: 2018/10/22 0022 添加扫电能表条形码查询用户数据功能
public class HomeNewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TextView mCountView;
    private RecyclerView mRecyclerView;
    private List<User> mSrcUserList = new ArrayList<>();
    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
                startActivityForResult(intent, 0);
            }
        });
        mBaseQuickAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mCurrentPage++;
                getData();
            }
        }, mRecyclerView);
        getData();
    }

    private void getData() {
        OkGo.<PageUserListBean>get(App.BASE_HOST + "/user/findUserListByPage")
                .params("page", mCurrentPage)
                .execute(new AbsCallback<PageUserListBean>() {
                    @Override
                    public void onSuccess(Response<PageUserListBean> response) {
                        PageUserListBean body = response.body();
                        List<User> userList = body.getData();

                        if (mCurrentPage == 0) {
                            mBaseQuickAdapter.replaceData(userList);
                            mSrcUserList.clear();
                        } else {
                            mBaseQuickAdapter.addData(userList);
                        }
                        mSrcUserList.addAll(userList);

                        mCountView.setText(body.getTotalElements() + "");
                        if (userList.isEmpty()) {
                            mBaseQuickAdapter.loadMoreEnd();
                        } else {
                            mBaseQuickAdapter.loadMoreComplete();
                        }
                    }

                    @Override
                    public PageUserListBean convertResponse(okhttp3.Response response) throws Throwable {
                        return new Gson().fromJson(response.body().string(), PageUserListBean.class);
                    }
                });
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
                if (TextUtils.isEmpty(newText)) {
                    mBaseQuickAdapter.replaceData(mSrcUserList);
                    mBaseQuickAdapter.setEnableLoadMore(true);
                } else {
                    mBaseQuickAdapter.setEnableLoadMore(false);
                    OkGo.<PageUserListBean>get(App.BASE_HOST + "/user/searchUser")
                            .params("key", newText)
                            .execute(new AbsCallback<PageUserListBean>() {
                                @Override
                                public void onSuccess(Response<PageUserListBean> response) {
                                    mBaseQuickAdapter.replaceData(response.body().getData());
                                }

                                @Override
                                public PageUserListBean convertResponse(okhttp3.Response response) throws Throwable {
                                    return new Gson().fromJson(response.body().string(), PageUserListBean.class);
                                }
                            });
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_user:
                startActivityForResult(new Intent(this, AddUserActivity.class), 0);
                break;
            case R.id.nav_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        mCurrentPage = 0;
        getData();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

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
            helper.setText(R.id.meter_reading_id, getString(R.string.meter_reading_id_place_holder, item.getMeterReadingId()));
        }
    };
}
