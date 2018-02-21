package group.tonight.electricityfeehelper.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import group.tonight.electricityfeehelper.MainApp;
import group.tonight.electricityfeehelper.dao.DaoSession;
import group.tonight.electricityfeehelper.dao.UserDao;
import group.tonight.electricityfeehelper.interfaces.OnFragmentInteractionListener;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.dao.User;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddUserFragment extends DialogFragment implements View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private EditText mUserIdEt;
    private EditText mUserNameEt;
    private EditText mOrderDateEt;
    private EditText mAddressET;
    private EditText mPhoneEt;
    private TextView mSaveBtn;
    private TextView mDialogTitleTv;

    public AddUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddUserFragment.
     */
    public static AddUserFragment newInstance(String param1, String param2) {
        AddUserFragment fragment = new AddUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_add_user, container, false);

        mDialogTitleTv = (TextView) rootView.findViewById(R.id.dialog_title);

        mUserIdEt = (EditText) rootView.findViewById(R.id.user_id);
        mUserNameEt = (EditText) rootView.findViewById(R.id.user_name);
        mOrderDateEt = (EditText) rootView.findViewById(R.id.device_id);
        mAddressET = (EditText) rootView.findViewById(R.id.address);
        mPhoneEt = (EditText) rootView.findViewById(R.id.phone);
        mSaveBtn = (TextView) rootView.findViewById(R.id.ok);

        rootView.findViewById(R.id.cancel).setOnClickListener(this);
        rootView.findViewById(R.id.ok).setOnClickListener(this);


        if (!TextUtils.isEmpty(mParam1)) {
            mDialogTitleTv.setText("修改用户资料");
            if (getActivity() != null) {
                DaoSession daoSession = ((MainApp) getActivity().getApplication()).getDaoSession();
                UserDao userDao = daoSession.getUserDao();
                User user = userDao.load(Long.parseLong(mParam1));
                if (user != null) {
                    mUserIdEt.setText(user.getAccountId() + "");
                    mUserNameEt.setText(user.getUserName());
//                    mOrderDateEt.setText(user.getOrderDate());
                    mAddressET.setText(user.getAddress());
                    mPhoneEt.setText(user.getPhone());

                    mSaveBtn.setText("保存");
                }
            }
        }
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.ok:
                String userId = mUserIdEt.getText().toString();
                String userName = mUserNameEt.getText().toString();
                String orderDate = mOrderDateEt.getText().toString();
                String address = mAddressET.getText().toString();
                String phone = mPhoneEt.getText().toString();

                UserDao userDao = ((MainApp) getActivity().getApplication()).getDaoSession().getUserDao();
                User user = userDao.load(Long.parseLong(mParam1));
                if (user == null) {
                    user = new User();
                }
                if (TextUtils.isEmpty(userId)) {
                    Toast.makeText(v.getContext(), "用户编号未填写", Toast.LENGTH_SHORT).show();
                    return;
                }
                user.setAccountId(Long.parseLong(userId));
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(v.getContext(), "用户姓名未填写", Toast.LENGTH_SHORT).show();
                    return;
                }
                user.setUserName(userName);
//                    user.setOrderDate(orderDate);
                user.setAddress(address);
                if (!TextUtils.isEmpty(phone)) {
                    Toast.makeText(v.getContext(), "电话号码未填写", Toast.LENGTH_SHORT).show();
                    return;
                }
                user.setPhone(phone);
                long currentTimeMillis = System.currentTimeMillis();
                user.setUpdateTime(currentTimeMillis);
                if (TextUtils.isEmpty(mParam1)) {
                    user.setCreateTime(currentTimeMillis);
                    userDao.insert(user);
                } else {
                    userDao.update(user);
                }
                if (mListener != null) {
                    mListener.onFragmentInteraction(Activity.RESULT_OK);
                }

                dismiss();
                break;
            default:
                break;
        }
    }
}
