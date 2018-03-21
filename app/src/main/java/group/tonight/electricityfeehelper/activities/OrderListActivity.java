package group.tonight.electricityfeehelper.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.List;

import group.tonight.electricityfeehelper.MainApp;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.dao.Order;
import group.tonight.electricityfeehelper.dao.OrderDao;
import group.tonight.electricityfeehelper.dao.User;
import group.tonight.electricityfeehelper.fragments.AddOrderFragment;
import group.tonight.electricityfeehelper.fragments.PayFragment;
import group.tonight.electricityfeehelper.interfaces.OnFragmentInteractionListener;
import group.tonight.electricityfeehelper.utils.BaseRecyclerAdapter;
import group.tonight.electricityfeehelper.utils.SmartViewHolder;

/**
 * 用户欠费记录
 */
public class OrderListActivity extends BackEnableActivity implements OnFragmentInteractionListener, View.OnClickListener, AdapterView.OnItemClickListener {

    private RecyclerView mOrderRv;
    private Long mFId;
    private TextView mUserNameTv;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserNameTv = (TextView) findViewById(R.id.user_name);
        mOrderRv = (RecyclerView) findViewById(R.id.order_rv);
        mOrderRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mOrderRv.setAdapter(mAdapter);

        findViewById(R.id.detail).setOnClickListener(this);

        mFId = getIntent().getLongExtra("_id", -1);

        mAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUser = MainApp.getDaoSession().getUserDao().load(mFId);
        if (mUser == null) {
            return;
        }
        String userName = mUser.getUserName();
        mUserNameTv.setText(userName);

        OrderDao orderDao = MainApp.getDaoSession().getOrderDao();
        List<Order> list = orderDao.queryBuilder()
                .where(OrderDao.Properties.Uid.eq(mFId))
                .list();
        mAdapter.refresh(list);
    }

    @Override
    protected int setChildLayoutId() {
        return R.layout.activity_user_detail;
    }

    @Override
    protected String setActivityName() {
        return "用户欠费记录";
    }


    private BaseRecyclerAdapter<Order> mAdapter = new BaseRecyclerAdapter<Order>(R.layout.list_item_user_detail) {
        @Override
        protected void onBindViewHolder(SmartViewHolder holder, Order model, int position) {
            long createTime = model.getCreateTime();
            double qianFei = model.getQianFei();
            String orderDate = model.getOrderDate();

            holder.text(R.id.date, orderDate);
            holder.text(R.id.money, getString(R.string.qian_fei_place_holder, qianFei + ""));
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_detail_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_order) {
            AddOrderFragment addOrderFragment = AddOrderFragment.newInstance(mFId + "", "");
            addOrderFragment.show(getSupportFragmentManager(), "");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(int result) {
        if (result == Activity.RESULT_OK) {
            OrderDao orderDao = MainApp.getDaoSession().getOrderDao();
            List<Order> list = orderDao.queryBuilder()
                    .where(OrderDao.Properties.Uid.eq(mFId))
                    .list();
            mAdapter.refresh(list);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail:
                Intent intent = new Intent(this, UserInfoActivity.class);
                intent.putExtra("_id", mFId);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object item = mAdapter.getItem(position);
        if (item instanceof Order) {
            Order order = (Order) item;
            double qianFei = order.getQianFei();
            if (qianFei != 0) {
                PayFragment payFragment = PayFragment.newInstance(order.getId() + "", "");
                payFragment.show(getSupportFragmentManager(), "");
            }
        }
    }
}
