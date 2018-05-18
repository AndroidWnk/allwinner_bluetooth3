package com.etrans.bluetooth.Fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.etrans.bluetooth.BluetoothActivity;
import com.etrans.bluetooth.MainActivity;
import com.etrans.bluetooth.R;
import com.etrans.bluetooth.app.Myapplication;
import com.etrans.bluetooth.event.AutoConnectAcceptEvent;
import com.etrans.bluetooth.utils.SlipButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2017/5/24.
 */

@SuppressLint("ValidFragment")
public class SettingInfoFg extends Fragment implements View.OnClickListener {
    private static final String TAG = "SettingInfoFg";


    public static final int MSG_DEVICE_NAME = 0;
    public static final int MSG_PIN_CODE = 1;
    public static final int MSG_AUTO_STATUS = 2;

    private Context context;
    private View view;
    private SlipButton mSpliBluetoothBtn;
    private SlipButton auto_connect_switch;
    private SlipButton auto_answer_switch;
    private TextView mTvBluetoothState,tv_bluetooth_connectstate, tv_bluetooth_answerstate, et_bluetooth_imei;
    private EditText et_pin_code, et_device_name;
    private Button btn_commit, btn_pincommit,btn_phone;
    private BluetoothActivity activity;
    private static Handler hand = null;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DEVICE_NAME:
                    String deviceName = (String) msg.obj;
                    et_device_name.setText(deviceName);
                    et_device_name.setSelection(et_device_name.getText().length(), et_device_name.getText().length());
                    break;
                case MSG_PIN_CODE:
                    String pinCode = (String) msg.obj;
                    et_pin_code.setText(pinCode);
                    et_pin_code.setSelection(et_pin_code.getText().length(), et_pin_code.getText().length());
                    break;
//                case MSG_AUTO_STATUS: //开关状态
//                    String autoStatus = (String) msg.obj;
//                    if (autoStatus.charAt(0) != '0') {
//                        auto_connect_switch.setCheck(true);
//
////                        auto_connect_switch.setImageResource(R.drawable.ico_4157_kai);
//                    } else {
//                        auto_connect_switch.setCheck(false);
////                        auto_connect_switch.setImageResource(R.drawable.ico_4158_guan);
//                    }
////                    tv_bluetooth_connectstate.setText(autoStatus.charAt(0) != '0' ? getString(R.string.string_tips_bt_connectstate_on) : getString(R.string.string_tips_bt_connectstate_off));
//                    if (autoStatus.charAt(1) != '0') {
//                        auto_answer_switch.setCheck(true);
////                        auto_answer_switch.setImageResource(R.drawable.ico_4157_kai);
//                    } else {
//                        auto_answer_switch.setCheck(false);
////                        auto_answer_switch.setImageResource(R.drawable.ico_4158_guan);
//                    }
////                    tv_bluetooth_answerstate.setText(autoStatus.charAt(1) != '0' ? getString(R.string.string_tips_bt_answerstate_on) : getString(R.string.string_tips_bt_answerstate_off));
//
//                    break;
            }
        }

        ;
    };

    public static Handler getHandler() {
        return hand;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (BluetoothActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fg_setting, null);
        initView(view);
        hand = handler;
        EventBus.getDefault().register(this);
        initData();
        return view;
    }


    private void initView(View view) {
//        mTvBluetoothState = (TextView) view.findViewById(R.id.tv_bluetooth_state);
        tv_bluetooth_connectstate = (TextView) view.findViewById(R.id.tv_bluetooth_connectstate);
        tv_bluetooth_answerstate = (TextView) view.findViewById(R.id.tv_bluetooth_answerstate);
        et_bluetooth_imei = (TextView) view.findViewById(R.id.et_bluetooth_imei);
        setImei();
        et_pin_code = (EditText) view.findViewById(R.id.et_pin_code);
        et_device_name = (EditText) view.findViewById(R.id.et_device_name);
        et_device_name.setSelection(et_device_name.getText().toString().length());
        if (!TextUtils.isEmpty(Myapplication.mLocalName)) {
            et_device_name.setText(Myapplication.mLocalName);
        } else {
            et_device_name.setText("hello");
        }
        if (!TextUtils.isEmpty(Myapplication.mPinCode)) {
            et_pin_code.setText(Myapplication.mPinCode);
        } else {
            et_pin_code.setText("0000");
        }
        btn_commit = (Button) view.findViewById(R.id.btn_commit);
        btn_commit.setOnClickListener(this);
        btn_pincommit = (Button) view.findViewById(R.id.btn_pincommit);
        btn_pincommit.setOnClickListener(this);
        btn_phone = (Button) view.findViewById(R.id.btn_phone);
        btn_phone.setOnClickListener(this);
//        mSpliBluetoothBtn = (SlipButton) view.findViewById(R.id.slip_bluetooth_btn);
//        mSpliBluetoothBtn.setEnabled(true);
//        mSpliBluetoothBtn.SetOnChangedListener(new SlipButton.OnChangedListener() {
//            @Override
//            public void OnChanged(boolean CheckState) {
//                Log.i(TAG, "OnChanged: " + CheckState);
//
//                if (CheckState) {
//                    defaultAdapter.enable();
//                } else {
//                    defaultAdapter.disable();
//                }
//                mTvBluetoothState.setText(CheckState ? getString(R.string.string_tips_bt_turn_on) : getString(R.string.string_tips_bt_turn_off));
//            }
//        });
        auto_connect_switch = (SlipButton) view.findViewById(R.id.auto_connect_switch);
        auto_connect_switch.setEnabled(true);
        auto_connect_switch.SetOnChangedListener(new SlipButton.OnChangedListener() {
            @Override
            public void OnChanged(boolean CheckState) {
                Log.i(TAG, "OnChanged: " + CheckState);

                if (CheckState) {
                    try {
                        Myapplication.getService().setAutoConnect();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
//                    defaultAdapter.enable();
                } else {
                    try {
                        Myapplication.getService().cancelAutoConnect();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
//                    defaultAdapter.disable();
                }
//                tv_bluetooth_connectstate.setText(CheckState ? getString(R.string.string_tips_bt_connectstate_on) : getString(R.string.string_tips_bt_connectstate_off));
            }
        });
        auto_answer_switch = (SlipButton) view.findViewById(R.id.auto_answer_switch);
        auto_answer_switch.setEnabled(true);
        auto_answer_switch.SetOnChangedListener(new SlipButton.OnChangedListener() {
            @Override
            public void OnChanged(boolean CheckState) {
                Log.i(TAG, "OnChanged: " + CheckState);

                if (CheckState) {
                    try {
                        Myapplication.getService().setAutoAnswer();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
//                    defaultAdapter.enable();
                } else {
                    try {
                        Myapplication.getService().cancelAutoAnswer();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
//                    defaultAdapter.disable();
                }
//                tv_bluetooth_answerstate.setText(CheckState ? getString(R.string.string_tips_bt_answerstate_on) : getString(R.string.string_tips_bt_answerstate_off));
            }
        });
    }


    public void initData(){


        try {
            if(Myapplication.getService() != null){
                Myapplication.getService().getAutoConnectAnswer();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }


//        if (defaultAdapter == null) {
//            defaultAdapter = BluetoothAdapter.getDefaultAdapter();
//        }
//        mTvBluetoothState.setText(defaultAdapter.isEnabled() ? getString(R.string.string_tips_bt_turn_on) : getString(R.string.string_tips_bt_turn_off));
//        mSpliBluetoothBtn.setCheck(defaultAdapter.isEnabled());
    }

    private void setImei() {
        try {
            TelephonyManager telephonyManager = ((TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE));
            if (telephonyManager != null) {
                String imei = telephonyManager.getDeviceId();
                if (imei != null) et_bluetooth_imei.setText(imei);
            }
        } catch (SecurityException exception) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_commit:


//                try {
//                    BluetoothActivity.getService().phoneDail("1008619");
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }


                String newName = et_device_name.getText().toString();
                if (!TextUtils.isEmpty(newName)) {
                    if (Myapplication.mLocalName.equals(newName)) {
                        Toast.makeText(activity, getString(R.string.btearphone_setting_failure), Toast.LENGTH_LONG).show();
                        return;
                    }
                    try {
                        Myapplication.getService().setLocalName(newName);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    Handler handler = SearchInfoFg.getHandler();
                    if (handler != null) {
                        Message msg = Message.obtain();
                        msg.what = SearchInfoFg.MSG_DEVICE_NAME;
                        msg.obj = newName;
                        handler.sendMessage(msg);
                    }

                    Toast.makeText(activity, getString(R.string.btearphone_setting_succ), Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btn_pincommit:
                String newpinName = et_pin_code.getText().toString();
                if (!TextUtils.isEmpty(newpinName)) {
                    if (Myapplication.mPinCode.equals(newpinName)) {
                        Toast.makeText(activity, getString(R.string.btearphone_setting_failure), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        Myapplication.getService().setPinCode(newpinName);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    Handler handler = SearchInfoFg.getHandler();
                    if (handler != null) {
                        Message msg = Message.obtain();
                        msg.what = SearchInfoFg.MSG_PIN_CODE;
                        msg.obj = newpinName;
                        handler.sendMessage(msg);
                    }

//                defaultAdapter.setName(newName);
                    Toast.makeText(activity, getString(R.string.btearphone_setting_succ), Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.btn_phone:

                startActivity(new Intent(activity, MainActivity.class));


                break;
        }
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AutoConnectAcceptEvent status) { ////开关状态

        if (status.autoConnect) {
            auto_connect_switch.setCheck(true);
        } else {
            auto_connect_switch.setCheck(false);
        }
        if (status.autoAccept) {
            auto_answer_switch.setCheck(true);
        } else {
            auto_answer_switch.setCheck(false);
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
