package com.etrans.bluetooth.Fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.etrans.bluetooth.BluetoothActivity;
import com.etrans.bluetooth.Goc.BlueToothInfo;
import com.etrans.bluetooth.Goc.GocsdkCallbackImp;
import com.etrans.bluetooth.Goc.TransparentDialog;
import com.etrans.bluetooth.R;
import com.etrans.bluetooth.app.Myapplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/24.
 */

@SuppressLint("ValidFragment")
public class SearchInfoFg extends Fragment implements View.OnClickListener{

    private Context context;
    private View view;
    public static final int MSG_SEARCHE_DEVICE = 0;//搜索到设备添加列表
    public static final int MSG_SEARCHE_DEVICE_DONE = 1;
    public static final int MSG_DEVICE_NAME = 2;
    public static final int MSG_PIN_CODE = 3;
    public static final int MSG_CONNECT_SUCCESS = 4;
    public static final int MSG_CONNECT_FAILE = 5;//手机去掉配对返回刷新列表去掉加载动画
    public static final int MSG_HFP_STATUS = 6;
    public static final int MSG_CONNECT_ADDRESS = 1001;//连接成功获取蓝牙地址

    private BluetoothActivity activity;
    private TextView iv_search_bt;
    private ListView lv_device_list;
//    private Button btn_device_name;
//    private Button btn_pin_code;
    private List<BlueToothInfo> bts = new ArrayList<BlueToothInfo>();
    private MyAdapter adapter;
    private boolean isSearch = false;
    private TransparentDialog dialog;
    private String address = null;
    private boolean disConnecting = false;//判断断开回执
    private String Connectaddress = "";//判断断开回执
    private static Handler hand = null;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_SEARCHE_DEVICE: {
                    BlueToothInfo info = (BlueToothInfo) msg.obj;
                    bts.add(info);
                    adapter.notifyDataSetChanged();
                    isSearch = true;
                    break;
                }
                case MSG_SEARCHE_DEVICE_DONE: {
                    Toast.makeText(activity, "搜索完成", Toast.LENGTH_SHORT).show();
                    if (isAdded()) {
                        iv_search_bt.setText(getString(R.string.string_tips_bt_search_device));
                    }
//                    iv_anim.setVisibility(View.GONE);
//                    iv_anim.clearAnimation();
                    isSearch = false;
                    break;
                }
                case MSG_DEVICE_NAME: {
                    String deviceName = (String) msg.obj;
//                    btn_device_name.setText(deviceName);
                    break;
                }
                case MSG_PIN_CODE: {
                    String pinCode = (String) msg.obj;
//                    btn_pin_code.setText(pinCode);
                    break;
                }
                case MSG_CONNECT_SUCCESS:
                    try {
                        Myapplication.getService().getCurrentDeviceName();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
//                    showConnected(); //显示连接状态
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    break;
                case MSG_CONNECT_FAILE:
                    if (disConnecting) {
                        try {
                            Myapplication.getService().connectDevice(Connectaddress);
                            disConnecting = false;
                            showPopupWindow();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    address = null;
                    if (dialog != null) {
                        dialog.dismiss();
                    }
//                    showSearch(); //显示搜索列表
                    break;
                case MSG_HFP_STATUS:
                    int status = (Integer) msg.obj;
                    if(status>=3){
//                        showConnected();//显示连接状态
                    }else{
//                        showSearch();//显示搜索列表
                    }
                    break;
                case MSG_CONNECT_ADDRESS://获取已连接设备名称
                    address = (String) msg.obj;
                    break;

            }
        };
    };
    private ImageView iv_anim;
//    private TransparentDialog dialog;
    private LinearLayout ll_bt_left;
    private LinearLayout ll_connected;
    private TextView tv_content_name;

    public static Handler getHandler() {
        return hand;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (BluetoothActivity) getActivity();
        hand = handler;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fg_search, null);
        initView(view);

        return view;
    }

    private void initView(View view) {
//        ll_bt_left = (LinearLayout) view.findViewById(R.id.ll_bt_left);//隐藏界面
        iv_search_bt = (TextView) view.findViewById(R.id.btn_search_device); //搜索按钮
        lv_device_list = (ListView) view.findViewById(R.id.lv_device_list); //匹配列表
//        lv_device_list.setSelector(R.drawable.contact_list_item_selector);
//        btn_device_name = (Button) view.findViewById(R.id.btn_device_name);//设备名
//        btn_pin_code = (Button) view.findViewById(R.id.btn_pin_code);//设备号
//        iv_anim = (ImageView) view.findViewById(R.id.iv_anim);//加载动画
//        ll_connected = (LinearLayout) view.findViewById(R.id.ll_connected);//已连接设备
//        tv_content_name = (TextView) view.findViewById(R.id.tv_content_name);//已连接设备
        if (GocsdkCallbackImp.hfpStatus >= 3) {
//            showConnected();//显示连接状态
        } else {
//            showSearch();//显示搜索列表
        }
//        if (!TextUtils.isEmpty(XxBBApplication.mLocalName)) {
//            btn_device_name.setText(XxBBApplication.mLocalName);
//        } else {
//            btn_device_name.setText("hello");
//        }
//        if (!TextUtils.isEmpty(XxBBApplication.mPinCode)) {
//            btn_pin_code.setText(XxBBApplication.mPinCode);
//        } else {
//            btn_pin_code.setText("0000");
//        }
        iv_search_bt.setOnClickListener(this);
        adapter = new MyAdapter();
        lv_device_list.setAdapter(adapter);
        lv_device_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (GocsdkCallbackImp.hfpStatus >= 3) {
                    Toast.makeText(activity, "已有设备连接", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    try {
                        Myapplication.getService().stopDiscovery();

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    connectDevice(position);
                }
            }

        });
    }

    private void connectDevice(int position) {
//        showPopupWindow();
        BlueToothInfo blueToothInfo = adapter.getItem(position);
        if (TextUtils.isEmpty(blueToothInfo.name)) {
            Myapplication.currentDeviceName = blueToothInfo.address;
        } else {
            Myapplication.currentDeviceName = blueToothInfo.name;
        }
        if(!TextUtils.isEmpty(address)) {
            if (blueToothInfo.address.equals(address)) {

                Toast.makeText(activity,"已连接了该设备", Toast.LENGTH_SHORT).show();

            }else{
                showPopupWindow();
                try {
                    Myapplication.getService().disconnect();
                    disConnecting = true;
                    Connectaddress = address;
//                    BluetoothActivity.getService().connectDevice(blueToothInfo.address);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    protected void showPopupWindow() {
        dialog = new TransparentDialog(activity, R.style.transparentdialog);
        dialog.setCanceledOnTouchOutside(false);// 点击不消失
        dialog.show();
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search_device:
                if (isSearch) {
//                    iv_anim.setVisibility(View.GONE);
//                    iv_anim.clearAnimation();
                    iv_search_bt.setText(getString(R.string.string_tips_bt_search_device));
                    try {
                        Myapplication.getService().stopDiscovery();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    isSearch = false;
                } else {
//                    iv_anim.setVisibility(View.VISIBLE);//搜索动画
//                    RotateAnimation ra = new RotateAnimation(0.0f, 359.0f,
//                            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
//                            RotateAnimation.RELATIVE_TO_SELF, 0.5f);
//                    ra.setRepeatCount(-1);
//                    ra.setRepeatMode(Animation.RESTART);
//                    ra.setDuration(1000);
//                    LinearInterpolator li = new LinearInterpolator();
//                    ra.setInterpolator(li);
//                    iv_anim.startAnimation(ra);
                    iv_search_bt.setText(getString(R.string.string_tips_bt_searching_device));
                    bts.clear();
                    adapter.notifyDataSetChanged();
                    try {
                        Myapplication.getService().startDiscovery();
                        Myapplication.getService().getCurrentDeviceAddr();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return bts.size();
        }

        @Override
        public BlueToothInfo getItem(int position) {
            return bts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(activity,
                        R.layout.item_bt_connect_device, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
//            holder.tv_address = (TextView) convertView
//                    .findViewById(R.id.tv_address);
            BlueToothInfo blueToothInfo = bts.get(position);
            String name = null;
            if (TextUtils.isEmpty(blueToothInfo.name)) {
                name = "该设备无名称";
            } else {
                name = blueToothInfo.name;
            }
            holder.tv_name.setText(name);





//            String connectstate = "";
//            if (!TextUtils.isEmpty(address) && blueToothInfo.address.equals(address)) {
//                connectstate = "  已连接";
//            } else {
//                connectstate = "  ";
//            }
//            holder.tv_name.setText(name+connectstate);

//            holder.tv_address.setText(blueToothInfo.address);
            return convertView;
        }
    }

    private static class ViewHolder {
        public TextView tv_name;
//        public TextView tv_address;
    }
}
