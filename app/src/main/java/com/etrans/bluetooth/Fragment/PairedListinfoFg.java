package com.etrans.bluetooth.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.etrans.bluetooth.BluetoothActivity;
import com.etrans.bluetooth.Goc.BlueToothPairedInfo;
import com.etrans.bluetooth.Goc.TransparentDialog;
import com.etrans.bluetooth.R;
import com.etrans.bluetooth.app.Myapplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/24.
 */

@SuppressLint("ValidFragment")
public class PairedListinfoFg extends Fragment {

    private Context context;
    private View view;
    public static final int MSG_PAIRED_DEVICE = 0;//搜索配对设备
    public static final int MSG_CONNECT_ADDRESS = 1;//连接成功获取蓝牙地址
    public static final int MSG_CONNECT_SUCCESS = 2;//连接成功时
    public static final int MSG_CONNECT_FAILE = 3;//断开连接时回调刷新列表
    public static final int MSG_CURRENT_STATUS = 4;
    public static final int MSG_HFP_STATUS = 5;
    private BluetoothActivity activity;
    private ListView lv_paired_list;
    private DeviceAdapter deviceAdapter;
    private String address = null;
    private boolean isConnecting = true;
    private boolean disConnecting = false;//判断断开回执
    private String Connectaddress = "";//判断断开回执
    //    private TransparentDialog dialog;//点击连接弹出框
    private TransparentDialog dialog;

    private List<BlueToothPairedInfo> btpi = new ArrayList<BlueToothPairedInfo>();
    private static Handler hand = null;
    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_PAIRED_DEVICE:
                    BlueToothPairedInfo pairedInfo = (BlueToothPairedInfo) msg.obj;
                    if (pairedInfo.index > 0) {
                        if (!btpi.contains(pairedInfo)) {
                            btpi.add(pairedInfo);
                        }
                    }
                    deviceAdapter.notifyDataSetChanged();
                    break;
                case MSG_CONNECT_ADDRESS:
                    address = (String) msg.obj;
                    break;
                case MSG_CONNECT_SUCCESS: //点击连接弹出框
                    isConnecting = true;
                    if (dialog != null) { //隐藏加载动画
                        dialog.dismiss();
                    }
                    Log.d("app", "connect success initData");
                    initData();
                    break;
                case MSG_CONNECT_FAILE: //点击连接弹出框
                    if (disConnecting) {
                        try {
                            Myapplication.getService().connectDevice(Connectaddress);
                            disConnecting = false;
                            showPopupWindow();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    isConnecting = false;
                    address = null;
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Log.d("app", "connect failed initData");
                    initData();
                    break;

                case MSG_CURRENT_STATUS: //当前状态
                    isConnecting = (Boolean) msg.obj;
                    break;

                case MSG_HFP_STATUS:
                    int status = (Integer) msg.obj;
                    if (status < 3) {
                        address = null;
                    }
                    initData();
                    break;
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
        view = inflater.inflate(R.layout.fg_paired, null);


        lv_paired_list = (ListView) view.findViewById(R.id.lv_paired_list);
        deviceAdapter = new DeviceAdapter();
        lv_paired_list.setAdapter(deviceAdapter);
        Log.d("app", "onCreateView initData");
        initData();
        lv_paired_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                BlueToothPairedInfo blueToothPairedInfo = deviceAdapter.getItem(position);
                if (!TextUtils.isEmpty(address)) {
//                    if (isConnecting && blueToothPairedInfo.address.equals(address)) { //点击已连接设备请求断开
                    if (blueToothPairedInfo.address.equals(address)) { //点击已连接设备请求断开

                        createDisContentBtDialog(blueToothPairedInfo.name, blueToothPairedInfo.address);

//                    } else if (isConnecting && !blueToothPairedInfo.address.equals(address)) {
                    } else if (!blueToothPairedInfo.address.equals(address)) {

                        createRemindDialog(blueToothPairedInfo.name, blueToothPairedInfo.address);

                    } else {
                        createContentBtDialog(blueToothPairedInfo.name,
                                blueToothPairedInfo.address);
                    }
                } else {
                    createContentBtDialog(blueToothPairedInfo.name,
                            blueToothPairedInfo.address);
                }
            }
        });
        lv_paired_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                BlueToothPairedInfo blueToothPairedInfo = deviceAdapter.getItem(position);


                if (blueToothPairedInfo != null) {
                    createdeletePairDialog(blueToothPairedInfo);//取消该设备配对
                }

                return true;
            }
        });
        hand = handler;
        return view;
    }


    private void initData() {
        //清理数据
        btpi.clear();
        //数据改变了，通知adapter
        deviceAdapter.notifyDataSetChanged();
        //请求数据，请求配对列表到了，添加数据，刷新数据
        //获取当前连接地址
        if (Myapplication.getService() != null) {
            try {
                Myapplication.getService().getPairList();
                Myapplication.getService().getCurrentDeviceAddr();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Myapplication.getService() != null) {
                        try {
                            Myapplication.getService().getPairList();
                            Myapplication.getService().getCurrentDeviceAddr();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, 500);
        }
    }

    private class DeviceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return btpi.size();
        }

        @Override
        public BlueToothPairedInfo getItem(int position) {
            return btpi.get(position);
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
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final BlueToothPairedInfo blueToothInfo = btpi.get(position);
            String name = null;
            if (TextUtils.isEmpty(blueToothInfo.name)) {
                name = "该设备无名称";
            } else {
                name = blueToothInfo.name;
            }
            String connectstate = "";
            if (!TextUtils.isEmpty(address) && blueToothInfo.address.equals(address)) {
                connectstate = "  已连接";
            } else {
                connectstate = "  连接断开";
            }
            holder.tv_name.setText(name + connectstate);
//            holder.iv_remove.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    btpi.remove(blueToothInfo);
//                    deviceAdapter.notifyDataSetChanged();
//                    try {
//                        MainActivity.getService().disconnect();
//                        MainActivity.getService().deletePair(blueToothInfo.address);
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
            return convertView;
        }
    }

    private static class ViewHolder {
        public TextView tv_name;
    }


    private void createDisContentBtDialog(String Name, final String address) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("确定要断开该设备吗?" + Name + ":" + address);
        builder.setTitle("提示");
        builder.setPositiveButton("确认",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                        try {
                            Myapplication.getService().disconnect();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    protected void createRemindDialog(String Name, final String address) {

        Myapplication.currentDeviceName = Name;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("确定要连接该设备吗?" + Name + ":" + address);
        builder.setTitle("提示");
        builder.setPositiveButton("确认",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
//                        showPopupWindow();
                        try {
                            Myapplication.getService().disconnect();
                            disConnecting = true;
                            Connectaddress = address;
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                    }
                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();


//        AlertDialog dialog = new  AlertDialog.Builder(activity)
//                .setTitle("提醒").
//                        setMessage("请先断开当前连接！").
//                        create();
//        dialog.setCancelable(true);
//        dialog.setButton(DialogInterface.BUTTON_POSITIVE,"确定",new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
    }

    private void createContentBtDialog(String Name, final String address) {
        Myapplication.currentDeviceName = Name;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("确定要连接该设备吗?" + Name + ":" + address);
        builder.setTitle("提示");
        builder.setPositiveButton("确认",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                        showPopupWindow();
                        try {
                            Myapplication.getService().connectDevice(address);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void createdeletePairDialog(final BlueToothPairedInfo blueToothPairedInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("确定要取消配对该设备吗?" + blueToothPairedInfo.name + ":" + blueToothPairedInfo.address);
        builder.setTitle("提示");
        builder.setPositiveButton("确认",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {


                        btpi.remove(blueToothPairedInfo);
                        deviceAdapter.notifyDataSetChanged();
                        try {
                            Myapplication.getService().deletePair(blueToothPairedInfo.address);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                    }
                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    protected void showPopupWindow() {
        dialog = new TransparentDialog(activity, R.style.transparentdialog);
        dialog.setCanceledOnTouchOutside(false);// 点击不消失
        dialog.show();
    }
}
