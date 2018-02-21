package group.tonight.electricityfeehelper.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.test.mock.MockApplication;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import group.tonight.electricityfeehelper.MainApp;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.dao.Order;
import group.tonight.electricityfeehelper.dao.OrderDao;
import group.tonight.electricityfeehelper.interfaces.OnFragmentInteractionListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PayFragment extends DialogFragment implements View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private EditText mShiShoutEt;
    private EditText mRemarksEt;

    public PayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PayFragment.
     */
    public static PayFragment newInstance(String param1, String param2) {
        PayFragment fragment = new PayFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_pay, container, false);

        mShiShoutEt = (EditText) rootView.findViewById(R.id.shi_shou);
        mRemarksEt = (EditText) rootView.findViewById(R.id.remarks);

        rootView.findViewById(R.id.cancel).setOnClickListener(this);
        rootView.findViewById(R.id.ok).setOnClickListener(this);

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
                String shiShouStr = mShiShoutEt.getText().toString();
                String remarks = mRemarksEt.getText().toString();
                if (!TextUtils.isEmpty(shiShouStr)) {
                    if (!shiShouStr.startsWith(".")) {
                        OrderDao orderDao = ((MainApp) getActivity().getApplication()).getDaoSession().getOrderDao();
                        Order order = orderDao.load(Long.parseLong(mParam1));
                        if (order != null) {
                            double yingShou = order.getYingShou();
                            double shiShou = order.getShiShou();
                            double qianFei = order.getQianFei();

                            order.setShiShou(shiShou + Double.parseDouble(shiShouStr));
                            order.setQianFei(qianFei - Double.parseDouble(shiShouStr));
                            if (!TextUtils.isEmpty(remarks)) {
                                order.setRemarks(remarks);
                            }

                            orderDao.update(order);

                            if (mListener != null) {
                                mListener.onFragmentInteraction(Activity.RESULT_OK);
                            }
                        }
                    }
                }
                dismiss();
                break;
        }
    }
}
