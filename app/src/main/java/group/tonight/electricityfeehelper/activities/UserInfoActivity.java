package group.tonight.electricityfeehelper.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.databinding.library.baseAdapters.BR;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;

import java.lang.reflect.Type;

import group.tonight.electricityfeehelper.App;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.dao.User;
import group.tonight.electricityfeehelper.fragments.AddUserFragment;
import group.tonight.electricityfeehelper.interfaces.OnFragmentInteractionListener;
import group.tonight.electricityfeehelper.model.DataResponseBean;

/**
 * 用户资料
 */
public class UserInfoActivity extends BackEnableActivity implements OnFragmentInteractionListener {

    private String mUserPhone;
    private User mUser;

    @Override
    protected int setChildLayoutId() {
        return R.layout.activity_user_info;
    }

    @Override
    protected String setActivityName() {
        return "用户资料";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = (User) getIntent().getSerializableExtra("data");
        mUserPhone = mUser.getUserPhone();
        setLayoutData(BR.dial, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mUserPhone)) {
                    return;
                }
                if (TextUtils.equals("0", mUserPhone)) {
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + mUserPhone);
                intent.setData(data);
                startActivity(intent);
            }
        });
        getUserById();
    }

    private void getUserById() {
        OkGo.<DataResponseBean<User>>post(App.BASE_HOST + "/user/findById")
                .params("id", mUser.getId())
                .execute(new AbsCallback<DataResponseBean<User>>() {
                    @Override
                    public void onSuccess(Response<DataResponseBean<User>> response) {
                        DataResponseBean<User> dataResponseBean = response.body();
                        if (dataResponseBean.getCode() == 0) {
                            User data = dataResponseBean.getData();
                            setLayoutData(BR.user, data);
                        } else {
                            Toast.makeText(UserInfoActivity.this, dataResponseBean.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public DataResponseBean<User> convertResponse(okhttp3.Response response) throws Throwable {
                        Type type = new TypeToken<DataResponseBean<User>>() {
                        }.getType();
                        return new Gson().fromJson(response.body().string(), type);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_info_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_user_info) {
            Intent intent = new Intent(this, AddUserActivity.class);
            intent.putExtra("data", mUser);
            startActivityForResult(intent, 0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        setResult(Activity.RESULT_OK);
        getUserById();
    }

    @Override
    public void onFragmentInteraction(int result) {
    }
}
