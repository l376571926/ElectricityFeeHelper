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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import group.tonight.electricityfeehelper.MainApp;
import group.tonight.electricityfeehelper.MyOrderRecyclerViewAdapter;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.dao.DaoSession;
import group.tonight.electricityfeehelper.dao.Order;
import group.tonight.electricityfeehelper.dao.OrderDao;
import group.tonight.electricityfeehelper.dao.User;
import group.tonight.electricityfeehelper.dao.UserDao;
import group.tonight.electricityfeehelper.interfaces.OnListFragmentInteractionListener;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class OrderListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = OrderListFragment.class.getSimpleName();
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private MyOrderRecyclerViewAdapter mMyOrderRecyclerViewAdapter;
    private List<User> mUserList;
    private RecyclerView mListView;
    private TextView mCountView;
    private SearchView mSearchView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
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
        mSearchView = (SearchView) view.findViewById(R.id.search_view);
        mCountView = (TextView) view.findViewById(R.id.count);
        mListView = (RecyclerView) view.findViewById(R.id.list);

        mSearchView.setQueryHint("请输入用户编号或者用户名称");
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (getActivity() != null) {
                    DaoSession daoSession = ((MainApp) getActivity().getApplication()).getDaoSession();
                    UserDao userDao = daoSession.getUserDao();

                    QueryBuilder<User> userQueryBuilder = userDao.queryBuilder()
                            .whereOr(UserDao.Properties.UserId.like("%" + newText + "%"), UserDao.Properties.UserName.like("%" + newText + "%"));

                    userQueryBuilder.where(UserDao.Properties.QianFeiSum.notEq(0));

                    List<User> list = userQueryBuilder
                            .limit(50)
                            .list();
                    mUserList.clear();
                    mUserList.addAll(list);
                    mMyOrderRecyclerViewAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });

        // Set the adapter
        Context context = view.getContext();

        mListView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        if (mColumnCount <= 1) {
            mListView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mListView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        mUserList = new ArrayList<>();
        mMyOrderRecyclerViewAdapter = new MyOrderRecyclerViewAdapter(mUserList, mListener);
        mListView.setAdapter(mMyOrderRecyclerViewAdapter);

//        OkHttpUtils.get()
//                .url(MyUtils.LATEST_ORDER_URL)
//                .build()
//                .execute(mOrderListCallback);

        loadQianFeiData();


        return view;
    }

    private void loadQianFeiData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null) {
                    return;
                }
                UserDao userDao = ((MainApp) getActivity().getApplication()).getDaoSession().getUserDao();

                final List<User> list = userDao.queryBuilder()
                        .where(UserDao.Properties.QianFeiSum.notEq(0))
                        .list();
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mUserList.clear();
                        mUserList.addAll(list);
                        mCountView.setText(list.size() + "");
                        mMyOrderRecyclerViewAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private Callback<List<User>> mOrderListCallback = new Callback<List<User>>() {
        @Override
        public List<User> parseNetworkResponse(Response response, int id) throws Exception {
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return null;
            }
            String json = responseBody.string();
            JSONArray jsonArray = new JSONArray(json);
            if (getActivity() == null) {
                return null;
            }
            DaoSession daoSession = ((MainApp) getActivity().getApplication()).getDaoSession();
            OrderDao orderDao = daoSession.getOrderDao();
            UserDao userDao = daoSession.getUserDao();

            for (int i = 0; i < jsonArray.length(); i++) {
                String orderUrl = jsonArray.getString(i);
                Response execute = OkHttpUtils.get()
                        .url(orderUrl)
                        .build()
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

        @Override
        public void onError(Call call, Exception e, int id) {
            Log.e(TAG, "onError: " + e);
        }

        @Override
        public void onResponse(List<User> response, int id) {
//            if (response != null) {
//                if (mMyOrderRecyclerViewAdapter != null) {
//                    mCountView.setText(response.size() + "");
//                    mUserList.clear();
//                    mUserList.addAll(response);
//                    mMyOrderRecyclerViewAdapter.notifyDataSetChanged();
//                }
//            }
            loadQianFeiData();
        }
    };
}
