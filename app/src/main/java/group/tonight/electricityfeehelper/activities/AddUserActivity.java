package group.tonight.electricityfeehelper.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;

import group.tonight.electricityfeehelper.App;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.BR;
import group.tonight.electricityfeehelper.dao.User;
import group.tonight.electricityfeehelper.model.BaseResponseBean;

public class AddUserActivity extends BackEnableActivity {

    private User mCurrentUser;

    @Override
    protected int setChildLayoutId() {
        return R.layout.activity_add_user;
    }

    @Override
    protected String setActivityName() {
        if (getIntent().hasExtra("data")) {
            mCurrentUser = (User) getIntent().getSerializableExtra("data");
            System.out.println();
            return "修改用户资料";
        } else {
            System.out.println();
            System.out.println();
        }
        return "添加用户";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mCurrentUser == null) {
            mCurrentUser = new User();
        }
        setLayoutData(BR.data, mCurrentUser);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_user_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (TextUtils.isEmpty(mCurrentUser.getUserId())) {
                    Toast.makeText(this, "用户编号未填写", Toast.LENGTH_SHORT).show();
                } else {
                    if (mCurrentUser.getId() == 0) {//添加用户
                        OkGo.<BaseResponseBean>post(App.BASE_HOST + "/user/save")
                                .upJson(new Gson().toJson(mCurrentUser))
                                .execute(new AbsCallback<BaseResponseBean>() {
                                    @Override
                                    public void onSuccess(Response<BaseResponseBean> response) {
                                        BaseResponseBean baseResponseBean = response.body();
                                        Toast.makeText(AddUserActivity.this, baseResponseBean.getMsg(), Toast.LENGTH_SHORT).show();
                                        if (baseResponseBean.getCode() == 0) {
                                            setResult(Activity.RESULT_OK);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public BaseResponseBean convertResponse(okhttp3.Response response) throws Throwable {
                                        return new Gson().fromJson(response.body().string(), BaseResponseBean.class);
                                    }
                                });
                    } else {//修改用户
                        OkGo.<BaseResponseBean>post(App.BASE_HOST + "/user/updateUser")
                                .upJson(new Gson().toJson(mCurrentUser))
                                .execute(new AbsCallback<BaseResponseBean>() {
                                    @Override
                                    public void onSuccess(Response<BaseResponseBean> response) {
                                        BaseResponseBean baseResponseBean = response.body();
                                        Toast.makeText(AddUserActivity.this, baseResponseBean.getMsg(), Toast.LENGTH_SHORT).show();
                                        if (baseResponseBean.getCode() == 0) {
                                            setResult(Activity.RESULT_OK);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public BaseResponseBean convertResponse(okhttp3.Response response) throws Throwable {
                                        return new Gson().fromJson(response.body().string(), BaseResponseBean.class);
                                    }
                                });
                    }
                }

                break;
            case R.id.action_delete:
                if (mCurrentUser.getId() == 0) {
                    Toast.makeText(this, "当前用户无法删除", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(this)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle("提示")
                            .setMessage("删除后将无法恢复数据，请谨慎操作,是否确认删除数据？")
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    OkGo.<BaseResponseBean>post(App.BASE_HOST + "/user/deleteById")
                                            .params("id", mCurrentUser.getId())
                                            .execute(new AbsCallback<BaseResponseBean>() {
                                                @Override
                                                public void onSuccess(Response<BaseResponseBean> response) {
                                                    BaseResponseBean baseResponseBean = response.body();
                                                    if (baseResponseBean.getCode() == 0) {
                                                        setResult(Activity.RESULT_OK);
                                                        finish();
                                                    }
                                                    Toast.makeText(AddUserActivity.this, baseResponseBean.getMsg(), Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public BaseResponseBean convertResponse(okhttp3.Response response) throws Throwable {
                                                    return new Gson().fromJson(response.body().string(), BaseResponseBean.class);
                                                }
                                            });
                                }
                            })
                            .show();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
