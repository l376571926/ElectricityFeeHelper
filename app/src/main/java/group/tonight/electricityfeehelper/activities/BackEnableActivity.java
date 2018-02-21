package group.tonight.electricityfeehelper.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import group.tonight.electricityfeehelper.R;

/**
 * Created by liyiwei on 2018/2/21.
 */

public abstract class BackEnableActivity extends AppCompatActivity {
    protected TextView mTitleView;
    private ViewGroup mContentView;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_enable);

        mContentView = (ViewGroup) findViewById(R.id.child_layout_container);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitleView = (TextView) findViewById(R.id.toolbar_title);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(InfinityUtils.getRoleToolbarBgColor(this));
//        }
//        mToolbar.setBackgroundColor(InfinityUtils.getRoleToolbarBgColor(this));

        mToolbar.setTitle("");//必需手动清除默认标题文字（xml中填空都没用），否则会显示在ActionBar上
        setSupportActionBar(mToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(showBackArrow());
            String activityName = setActivityName();
            if (!TextUtils.isEmpty(activityName)) {
                mTitleView.setText(activityName);
            }
        }
        //填充子Activity布局
        getLayoutInflater().inflate(setChildLayoutId(), mContentView);

    }

    protected abstract int setChildLayoutId();

    protected abstract String setActivityName();

    /**
     * 如果继承的Activity不需要显示后退按钮，重写此方法返回false即可
     *
     * @return 是否显示标题栏的后退按钮
     */
    protected boolean showBackArrow() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
