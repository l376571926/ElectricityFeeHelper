package group.tonight.electricityfeehelper.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import group.tonight.electricityfeehelper.MainApp;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.dao.DaoSession;
import group.tonight.electricityfeehelper.dao.User;
import group.tonight.electricityfeehelper.dao.UserDao;
import group.tonight.electricityfeehelper.interfaces.OnFragmentInteractionListener;

/**
 * 添加用电户
 */
public class AddUserFragment extends DialogFragment implements View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "userId";

    private long mUserId;

    private OnFragmentInteractionListener mListener;
    private EditText mUserIdEt;
    private EditText mUserNameEt;
    private EditText mDeviceIdEt;
    private EditText mAddressET;
    private EditText mPhoneEt;
    private TextView mSaveBtn;
    private TextView mDialogTitleTv;
    private EditText mPositionIdEt;
    private EditText mSerialIdEt;

    public AddUserFragment() {
        // Required empty public constructor
    }

    public static AddUserFragment newInstance(long userId) {
        AddUserFragment fragment = new AddUserFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getLong(ARG_PARAM1);
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
        mAddressET = (EditText) rootView.findViewById(R.id.address);
        mPhoneEt = (EditText) rootView.findViewById(R.id.phone);

        mDeviceIdEt = (EditText) rootView.findViewById(R.id.device_id);
        mPositionIdEt = (EditText) rootView.findViewById(R.id.position_id);
        mSerialIdEt = (EditText) rootView.findViewById(R.id.serial_id);

        View mUserIdVg = rootView.findViewById(R.id.user_id_vg);
        View mDeviceIdVg = rootView.findViewById(R.id.device_id_vg);
        View mSerialIdVg = rootView.findViewById(R.id.serial_id_vg);

        mSaveBtn = (TextView) rootView.findViewById(R.id.ok);

        rootView.findViewById(R.id.cancel).setOnClickListener(this);
        rootView.findViewById(R.id.ok).setOnClickListener(this);

        if (mUserId != 0) {
            mDialogTitleTv.setText("修改用户资料");
            mUserIdVg.setVisibility(View.GONE);
//            mDeviceIdVg.setVisibility(View.GONE);
            mSerialIdVg.setVisibility(View.GONE);
            if (getActivity() != null) {
                DaoSession daoSession = MainApp.getDaoSession();
                UserDao userDao = daoSession.getUserDao();
                User user = userDao.load(mUserId);
                if (user != null) {
                    mUserIdEt.setText(user.getUserId());
                    mUserNameEt.setText(user.getUserName());
                    mAddressET.setText(user.getUserAddress());
                    mPhoneEt.setText(user.getUserPhone());

                    mDeviceIdEt.setText(user.getPowerMeterId());
                    mPositionIdEt.setText(user.getMeterReadingId());
                    mSerialIdEt.setText(user.getPowerLineId());

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
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
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
                String address = mAddressET.getText().toString();
                String phone = mPhoneEt.getText().toString();

                String deviceId = mDeviceIdEt.getText().toString();
                String positionId = mPositionIdEt.getText().toString();
                String serialId = mSerialIdEt.getText().toString();

                UserDao userDao = MainApp.getDaoSession().getUserDao();
                User user = userDao.load(mUserId);
                if (user == null) {
                    user = new User();
                }
                if (TextUtils.isEmpty(userId)) {
                    Toast.makeText(v.getContext(), "用户编号未填写", Toast.LENGTH_SHORT).show();
                    return;
                }
                user.setUserId(userId);
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(v.getContext(), "用户姓名未填写", Toast.LENGTH_SHORT).show();
                    return;
                }
                user.setUserName(userName);
                user.setUserAddress(address);
                user.setUserPhone(phone);
                long currentTimeMillis = System.currentTimeMillis();
                user.setUpdateTime(currentTimeMillis);

                user.setPowerMeterId(deviceId);
                user.setMeterReadingId(positionId);
                user.setPowerLineId(serialId);

                if (mUserId == 0) {
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
