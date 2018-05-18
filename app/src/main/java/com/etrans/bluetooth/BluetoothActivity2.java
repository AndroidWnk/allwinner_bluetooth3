package com.etrans.bluetooth;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.etrans.bluetooth.Fragment.PairedListinfoFg;
import com.etrans.bluetooth.Fragment.SearchInfoFg;
import com.etrans.bluetooth.Fragment.SettingInfoFg;
import com.etrans.bluetooth.Goc.BootReceiver;
import com.etrans.bluetooth.Goc.GocsdkCallbackImp;

public class BluetoothActivity2 extends Activity implements View.OnClickListener {

    //goc///////////////////////////////////////////Service
    public static final int MSG_DEVICENAME = 11;
    public static final int MSG_DEVICEPINCODE = 12;
    public static final int MSG_CURRENT_CONNECT_DEVICE_NAME = 29;
    public static String mLocalName = "";
    public static String mPinCode = "";
    public static String currentDeviceName = "";
    public static GocsdkCallbackImp callback;
    //    private AudioManager mAudioManager;
    private BootReceiver receiver;
    private static Handler hand = null;
    public static Handler getHandler() {
        return hand;
    }
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case MSG_DEVICENAME:// 蓝牙设备名称
                    String name = (String) msg.obj;
                    mLocalName = name;
                    break;
                case MSG_DEVICEPINCODE:// 蓝牙设备的PIN码
                    String pincode = (String) msg.obj;
                    mPinCode = pincode;
                    break;
                case MSG_CURRENT_CONNECT_DEVICE_NAME:
                    currentDeviceName = (String) msg.obj;
                    break;

            }
        };
    };
    ////////////////////////////////////////////////////////////////////////////////////////////////


    private FrameLayout fl_content;
    private FragmentTransaction ft;
    private Fragment SetFg,PairedListFg,SearchFg;
    private ImageView iv_bt_name_setting,iv_bonded_device,iv_available_device,iv_back;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth2);
        //goc///////////////////////////////////////////////////Service
        // 注册开机广播接收者
        hand = handler;
////////////////////////////////////////////////////////////////////////////////////////////////
        initView();

        setclick();
    }





    @Override
    public void onClick(View v) {
        ft = getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        switch (v.getId()){
            case R.id.iv_bt_name_setting:


                iv_bt_name_setting.setImageResource(R.drawable.img_bt_name_setting_press);
                iv_available_device.setImageResource(R.drawable.img_available_nor);
                iv_bonded_device.setImageResource(R.drawable.img_bonded_nor);


                if (SetFg == null) {
                    SetFg = new SettingInfoFg();
                }
                ft.replace(R.id.fl_content, SetFg).commit();
                break;
            case R.id.iv_bonded_device:

                iv_bonded_device.setImageResource(R.drawable.img_bonded_press);
                iv_bt_name_setting.setImageResource(R.drawable.img_bt_name_setting_nor);
                iv_available_device.setImageResource(R.drawable.img_available_nor);
                if (PairedListFg == null) {
                    PairedListFg = new PairedListinfoFg();
                }
                ft.replace(R.id.fl_content, PairedListFg).commit();
                break;
            case R.id.iv_available_device:

                iv_available_device.setImageResource(R.drawable.img_available_press);
                iv_bt_name_setting.setImageResource(R.drawable.img_bt_name_setting_nor);
                iv_bonded_device.setImageResource(R.drawable.img_bonded_nor);
                if (SearchFg == null) {
                    SearchFg = new SearchInfoFg();
                }
                ft.replace(R.id.fl_content, SearchFg).commit();
                break;
            case R.id.iv_back:

                finish();
                break;
        }
    }

    private void initView() {
        iv_bt_name_setting = (ImageView) findViewById(R.id.iv_bt_name_setting);
        iv_bonded_device = (ImageView) findViewById(R.id.iv_bonded_device);
        iv_available_device = (ImageView) findViewById(R.id.iv_available_device);
        iv_back = (ImageView) findViewById(R.id.iv_back);


        fl_content = (FrameLayout) findViewById(R.id.fl_content);
        ft = getFragmentManager().beginTransaction();
        if (SetFg == null) {
            SetFg = new SettingInfoFg();
        }
        ft.replace(R.id.fl_content, SetFg).commit();
    }

    private void setclick(){
        iv_bt_name_setting.setOnClickListener(this);
        iv_bonded_device.setOnClickListener(this);
        iv_available_device.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }







    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Goc///////////////////////////////////////////////////Service

        // 注销蓝牙回调
//        try {
//            iGocsdkService.unregisterCallback(callback);
//
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        // 注销开机广播
//        unregisterReceiver(receiver);
//        // 解绑服务
//        unbindService(conn);
//        startService(gocsdkService);

        ///////////////////////////////////////////////////////////////////

    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}
