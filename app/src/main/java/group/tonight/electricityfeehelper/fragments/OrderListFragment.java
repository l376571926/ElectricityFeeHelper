package group.tonight.electricityfeehelper.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import group.tonight.electricityfeehelper.MainApp;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.dao.DaoSession;
import group.tonight.electricityfeehelper.dao.Order;
import group.tonight.electricityfeehelper.dao.OrderDao;
import group.tonight.electricityfeehelper.dao.User;
import group.tonight.electricityfeehelper.dao.UserDao;
import group.tonight.electricityfeehelper.utils.MyUtils;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 首页
 */
public class OrderListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = OrderListFragment.class.getSimpleName();
    private int mColumnCount = 1;
    private RecyclerView mListView;
    private TextView mCountView;
    private RefreshLayout mRefreshLayout;

    public OrderListFragment() {
    }

    @SuppressWarnings("unused")
    public static OrderListFragment newInstance(int columnCount) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        mCountView = (TextView) view.findViewById(R.id.count);
        mRefreshLayout = (RefreshLayout) view.findViewById(R.id.smart_refresh_layout);
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore();
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh();
                OkGo.<List<User>>get(MyUtils.LATEST_ORDER_URL)
                        .execute(mAbsCallback);
            }
        });
        mListView = (RecyclerView) view.findViewById(R.id.list);

        setHasOptionsMenu(true);

        // Set the adapter
        Context context = view.getContext();

        mListView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        if (mColumnCount <= 1) {
            mListView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mListView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        mListView.setAdapter(mBaseQuickAdapter);
        loadQianFeiData();
        return view;
    }

    /**
     * 重写这个方法要先调用setHasOptionsMenu方法
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_order_list_fragment, menu);

        SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        mSearchView.setQueryHint("请输入用户编号或者用户名称");
//        mSearchView.setIconifiedByDefault(false);//false表示加载后默认为搜索框输入状态
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e(TAG, "onQueryTextSubmit: " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e(TAG, "onQueryTextChange: " + newText);
                if (getActivity() != null) {
                    DaoSession daoSession = MainApp.getDaoSession();
                    UserDao userDao = daoSession.getUserDao();

                    QueryBuilder<User> userQueryBuilder = userDao.queryBuilder()
                            .whereOr(UserDao.Properties.UserId.like("%" + newText + "%"), UserDao.Properties.UserName.like("%" + newText + "%"));

                    userQueryBuilder.where(UserDao.Properties.QianFeiSum.notEq(0));

                    List<User> list = userQueryBuilder
                            .limit(50)
                            .list();
                    mBaseQuickAdapter.replaceData(list);
                }
                return false;
            }
        });
    }

    private void loadQianFeiData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null) {
                    return;
                }
                UserDao userDao = MainApp.getDaoSession().getUserDao();

                final List<User> list = userDao.queryBuilder()
                        .where(UserDao.Properties.QianFeiSum.notEq(0))
                        .list();
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCountView.setText(list.size() + "");
                        mBaseQuickAdapter.replaceData(list);
                    }
                });
            }
        }).start();
    }

    private AbsCallback<List<User>> mAbsCallback = new AbsCallback<List<User>>() {
        @Override
        public void onSuccess(com.lzy.okgo.model.Response<List<User>> response) {
            loadQianFeiData();
        }

        @Override
        public List<User> convertResponse(Response response) throws Throwable {
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return null;
            }
            String json = responseBody.string();
            JSONArray jsonArray = new JSONArray(json);
            if (getActivity() == null) {
                return null;
            }
            DaoSession daoSession = MainApp.getDaoSession();
            OrderDao orderDao = daoSession.getOrderDao();
            UserDao userDao = daoSession.getUserDao();

            for (int i = 0; i < jsonArray.length(); i++) {
                String orderUrl = jsonArray.getString(i);
                Response execute = OkGo.<byte[]>get(orderUrl)
                        .execute();
                ResponseBody responseBody1 = execute.body();
                if (responseBody1 == null) {
                    return null;
                }
                byte[] bytes = responseBody1.bytes();
                byte[] decode = Base64.decode(bytes, Base64.DEFAULT);
                String orderJson = new String(decode);
                JSONArray data = new JSONArray(orderJson);

                for (int m = 0; m < data.length(); m++) {
                    JSONObject orderObj = data.getJSONObject(m);
                    String userId = orderObj.getString("用户编号");
                    String userName = orderObj.getString("用户名称");
                    String userAddress = orderObj.getString("用电地址");
                    String orderDate = orderObj.getString("年月");
                    String userPhone = orderObj.getString("电话");

                    double yingShou = orderObj.getDouble("应收电费");
                    double shiShou = orderObj.getDouble("实收电费");
                    double qianFei = orderObj.getDouble("欠费金额");
                    double yingShouWeiYue = orderObj.getDouble("应收违约金");
                    double shiShouWeiYue = orderObj.getDouble("实收违约金");
                    double qianJiaoWeiYue = orderObj.getDouble("欠交违约金");

                    String orderStatus = orderObj.getString("电费类别");

                    User user = userDao.queryBuilder()
                            .where(UserDao.Properties.UserId.eq(userId))
                            .unique();
                    if (user != null) {
                        Long id1 = user.getId();

                        Order unique = orderDao.queryBuilder()
                                .where(OrderDao.Properties.Uid.eq(id1), OrderDao.Properties.OrderDate.eq(orderDate))
                                .build()
                                .unique();
                        if (unique == null) {
                            unique = new Order();
                            unique.setUid(id1);
                            unique.setOrderDate(orderDate);

                            unique.setYingShou(yingShou);
                            unique.setShiShou(shiShou);
                            unique.setQianFei(qianFei);

                            unique.setYingShouWeiYue(yingShouWeiYue);
                            unique.setShiShouWeiYue(shiShouWeiYue);
                            unique.setQianJiaoWeiYue(qianJiaoWeiYue);
                            unique.setOrderStatus(orderStatus);

                            user.setYingShouSum(user.getYingShouSum() + yingShou);
                            user.setShiShouSum(user.getShiShouSum() + shiShou);
                            user.setQianFeiSum(user.getQianFeiSum() + qianFei);

                            orderDao.insert(unique);
                            userDao.update(user);
                        }
                    }
                }
            }
            return null;
        }
    };

    private BaseQuickAdapter<User, BaseViewHolder> mBaseQuickAdapter = new BaseQuickAdapter<User, BaseViewHolder>(R.layout.fragment_order) {
        @Override
        protected void convert(BaseViewHolder helper, User item) {
            helper.setText(R.id.id, item.getUserId());
            helper.setText(R.id.content, item.getUserName());
            helper.setText(R.id.phone, item.getUserPhone());
            helper.setText(R.id.address, item.getUserAddress());
            helper.setText(R.id.money, getString(R.string.qian_fei_sum_place_holder, item.getQianFeiSum() + ""));
        }
    };
}
