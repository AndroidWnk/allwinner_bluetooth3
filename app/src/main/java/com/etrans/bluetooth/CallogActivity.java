package com.etrans.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.view.PagerAdapter;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.etrans.bluetooth.Goc.GocsdkCallbackImp;
import com.etrans.bluetooth.app.Myapplication;
import com.etrans.bluetooth.domain.CallLogInfo;
import com.etrans.bluetooth.utils.NoScrollViewPager;
import com.etrans.bluetooth.utils.ToastFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单元名称:ContactActivity.java
 * Created by fuxiaolei on 2016/8/30.
 * 说明:
 * Last Change by fuxiaolei on 2016/8/30.
 */
public class CallogActivity extends Activity implements View.OnClickListener {

    private ImageView ib_call_in, ib_call_out, ib_call_missed;
    private FrameLayout fl_call_in, fl_call_out, fl_call_miss;
    private TextView tv_call_in, tv_call_out, tv_call_miss;
    private NoScrollViewPager vp_content;
    private String[] callLogString = {"拨出", "打进", "未接"};
    private static final int CALLLOG_IN = 3;
    private static final int CALLLOG_OUT = 1;
    private static final int CALLLOG_MISS = 2;
    private List<ListView> listViews = new ArrayList<ListView>();
    private View view;

    public List<Map<String, String>> call_log_in = new ArrayList<Map<String, String>>();
    public List<Map<String, String>> call_log_out = new ArrayList<Map<String, String>>();
    public List<Map<String, String>> call_log_miss = new ArrayList<Map<String, String>>();
    private SimpleAdapter mSimpleAdapterIn;
    private SimpleAdapter mSimpleAdapterOut;
    private SimpleAdapter mSimpleAdapterMiss;
    public final static int MSG_CALLLOG = 1;// 通话记录下载
    public final static int MSG_CALLLOG_DONE = 2;// 通话记录下载结束
    public final static int MSG_DEVICE_CONNECTED = 3;
    public final static int MSG_DEVICE_DISCONNECTED = 4;
    private static Handler hand = null;
    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CALLLOG: {
                    CallLogInfo info = (CallLogInfo) msg.obj;

                    Map<String, String> map = null;
                    switch (info.type) {
                        case 4: { //未接来电
                            if (call_log_miss.size() <= 100) {
                                map = new HashMap<String, String>();
                                String name = null;
                                if (TextUtils.isEmpty(info.name)) {
                                    name = "未知号码";
                                } else {
                                    name = info.name;
                                }
                                map.put("itemName", name);
                                map.put("itemNum", info.number);
                                call_log_miss.add(map);
                                mSimpleAdapterMiss.notifyDataSetChanged();
                            }
                            break;
                        }
                        case 3: { //去电
                            if (call_log_out.size() <= 100) {
                                map = new HashMap<String, String>();
                                String name = null;
                                if (TextUtils.isEmpty(info.name)) {
                                    name = "未知号码";
                                } else {
                                    name = info.name;
                                }
                                map.put("itemName", name);
                                map.put("itemNum", info.number);
                                call_log_out.add(map);
                                mSimpleAdapterOut.notifyDataSetChanged();
                            }
                            break;
                        }
                        case 5: { //来电
                            if (call_log_in.size() <= 100) {
                                map = new HashMap<String, String>();
                                String name = null;
                                if (TextUtils.isEmpty(info.name)) {
                                    name = "未知号码";
                                } else {
                                    name = info.name;
                                }
                                map.put("itemName", name);
                                map.put("itemNum", info.number);
                                call_log_in.add(map);
                                mSimpleAdapterIn.notifyDataSetChanged();
                            }
                            break;
                        }
                    }
                }
                case MSG_CALLLOG_DONE:
                    //Toast.makeText(activity, "当前通话记录下载完毕！", 0).show();
                    rl_downloading.setVisibility(View.GONE);
                    vp_content.setVisibility(View.VISIBLE);
                    break;
                case MSG_DEVICE_CONNECTED:
                    showConnect();
                    break;
                case MSG_DEVICE_DISCONNECTED:
                    showDisconnect();
                    break;
            }
        }

        ;
    };
    private Handler mainHandler;
    private ImageView image_animation;
    private RelativeLayout rl_downloading;
    private TextView tv_device_disconnected;
    private FrameLayout fl_content;
    private LinearLayout ll_title;

    public static Handler getHandler() {
        return hand;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        setContentView(R.layout.activity_callog);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏
        initView();
        initEvents();
        Log.d("app", "GocsdkCallbackImp.hfpStatus=" + GocsdkCallbackImp.hfpStatus);
//        if (GocsdkCallbackImp.hfpStatus >= 3) {
        showConnect();
//        } else {
//            showDisconnect();
//        }
        hand = handler;
    }

    private void initEvents() {
//        ib_call_in.setOnClickListener(this);
//        ib_call_out.setOnClickListener(this);
//        ib_call_missed.setOnClickListener(this);
        fl_call_in.setOnClickListener(this);
        fl_call_out.setOnClickListener(this);
        fl_call_miss.setOnClickListener(this);
    }

    private void showDisconnect() {
        ll_title.setVisibility(View.GONE);
        fl_content.setVisibility(View.GONE);
        tv_device_disconnected.setVisibility(View.VISIBLE);
    }

    private void showConnect() {
        ll_title.setVisibility(View.VISIBLE);
        fl_content.setVisibility(View.VISIBLE);
        tv_device_disconnected.setVisibility(View.GONE);
    }

    @SuppressLint("WrongViewCast")
    private void initView() {
        ll_title = (LinearLayout) findViewById(R.id.ll_title);
        fl_content = (FrameLayout) findViewById(R.id.fl_content);
        tv_device_disconnected = (TextView) findViewById(R.id.tv_device_disconnect);
        ib_call_in = (ImageView) findViewById(R.id.ib_call_in);
        ib_call_out = (ImageView) findViewById(R.id.ib_call_out);
        ib_call_missed = (ImageView) findViewById(R.id.ib_call_missed);
        fl_call_in = (FrameLayout) findViewById(R.id.fl_call_in);
        fl_call_out = (FrameLayout) findViewById(R.id.fl_call_out);
        fl_call_miss = (FrameLayout) findViewById(R.id.fl_call_miss);
        tv_call_in = (TextView) findViewById(R.id.tv_call_in);
        tv_call_out = (TextView) findViewById(R.id.tv_call_out);
        tv_call_miss = (TextView) findViewById(R.id.tv_call_miss);
        vp_content = (NoScrollViewPager) findViewById(R.id.vp_content);
        rl_downloading = (RelativeLayout) findViewById(R.id.rl_downloading);

        image_animation = (ImageView) findViewById(R.id.image_animation);
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.8f);
        animation.setDuration(1000);
        animation.setFillAfter(false);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        image_animation.startAnimation(animation);
        InitData();
        vp_content.setAdapter(new MyPagerAdapter());

        for (int i = 0; i < callLogString.length; i++) {
            ListView lv_item = new ListView(this);
            lv_item.setVerticalScrollBarEnabled(false);
            lv_item.setSelector(R.drawable.contact_list_item_selector);
//            lv_item.setSelector(R.color.item);
            paddingData(lv_item, i);
            listViews.add(lv_item);
        }

//        dataTest();//数据测试，模拟通话记录

    }

    private void InitData() {
        LoadIncomingData();
    }

    // 切换页面
    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_call_in:
                ib_call_in.setVisibility(View.VISIBLE);
                ib_call_out.setVisibility(View.INVISIBLE);
                ib_call_missed.setVisibility(View.INVISIBLE);
                tv_call_in.setTextColor(getResources().getColor(R.color.white));
                tv_call_out.setTextColor(getResources().getColor(R.color.blue));
                tv_call_miss.setTextColor(getResources().getColor(R.color.blue));

                LoadIncomingData();//正式
                ListView lv_in = listViews.get(1);
                paddingData(lv_in, 1);//正式
                vp_content.setCurrentItem(0, false);
                break;
            case R.id.fl_call_out:
                ib_call_in.setVisibility(View.INVISIBLE);
                ib_call_out.setVisibility(View.VISIBLE);
                ib_call_missed.setVisibility(View.INVISIBLE);
                tv_call_in.setTextColor(getResources().getColor(R.color.blue));
                tv_call_out.setTextColor(getResources().getColor(R.color.white));
                tv_call_miss.setTextColor(getResources().getColor(R.color.blue));

                LoadCalloutData();//正式
                ListView lv_callout = listViews.get(1);
                paddingData(lv_callout, 1);//正式
                vp_content.setCurrentItem(1, false);
                break;
            case R.id.fl_call_miss:
                ib_call_in.setVisibility(View.INVISIBLE);
                ib_call_out.setVisibility(View.INVISIBLE);
                ib_call_missed.setVisibility(View.VISIBLE);
                tv_call_in.setTextColor(getResources().getColor(R.color.blue));
                tv_call_out.setTextColor(getResources().getColor(R.color.blue));
                tv_call_miss.setTextColor(getResources().getColor(R.color.white));

                LoadMissedData();//正式
                ListView lv_missed = listViews.get(2);
                paddingData(lv_missed, 2);//正式
                vp_content.setCurrentItem(2, false);
                break;
        }
    }

    private void LoadMissedData() { //未知来电
//        if (GocsdkCallbackImp.hfpStatus >= 3) {
//            if (call_log_miss.size() == 0) {
        rl_downloading.setVisibility(View.VISIBLE);
        vp_content.setVisibility(View.GONE);
        if (mainHandler != null) {
            mainHandler.sendEmptyMessage(MainActivity.MSG_UPDATE_MISSED_CALLLOG);
        }
        call_log_miss.clear();
        try {
            Myapplication.getService().callLogstartUpdate(CALLLOG_MISS);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
//            }
//        }
    }

    private void LoadCalloutData() { //去电
//        if (GocsdkCallbackImp.hfpStatus >= 3) {
//            if (call_log_out.size() == 0) {
        rl_downloading.setVisibility(View.VISIBLE);
        vp_content.setVisibility(View.GONE);
        if (mainHandler != null) {
            mainHandler.sendEmptyMessage(MainActivity.MSG_UPDATE_CALLOUT_CALLLOG);
        }
        call_log_out.clear();
        try {
            Myapplication.getService().callLogstartUpdate(CALLLOG_OUT);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
//            }
//        }
    }

    private void LoadIncomingData() { //来电


//        if (call_log_in.size() == 0) {
        rl_downloading.setVisibility(View.VISIBLE);
        vp_content.setVisibility(View.GONE);


        if (mainHandler != null) {
            mainHandler.sendEmptyMessage(MainActivity.MSG_UPDATE_INCOMING_CALLLOG);
        }

        call_log_in.clear();
        try {
            Myapplication.getService().callLogstartUpdate(CALLLOG_IN);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
//        }
    }

    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ListView listView = listViews.get(position);
            ViewGroup parent = (ViewGroup) listView.getParent();
            if (parent != null) {
                parent.removeView(listView);
            }
            container.addView(listView);
            return listView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private void clickItemCall(SimpleAdapter mSimpleAdapter, int position) {

        if (mSimpleAdapter != null) {
            Map<String, String> map = (Map<String, String>) mSimpleAdapter.getItem(position);
            String Name = map.get("itemName");
            final String Num = map.get("itemNum");
            createCallOutDialog(Name, Num);
        }

    }

    private void createCallOutDialog(String Name, final String Num) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定要拨打吗?" + Name + ":" + Num);
        builder.setTitle("提示");
        builder.setPositiveButton("确认",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                        callOut(Num);
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


    private void callOut(String phoneNumber2) {
        placeCall(phoneNumber2);
    }

    // 给控件填充数据
    public void paddingData(ListView listView, int position) {

        switch (position) {
            case 0:
                mSimpleAdapterIn = new SimpleAdapter(this,
                        call_log_in,
                        R.layout.call_log_in_listview_item_view,
                        new String[]{"itemName", "itemNum", "itemTime"},
                        new int[]{R.id.tv_in_name, R.id.tv_in_number, R.id.tv_in_time});
                listView.setAdapter(mSimpleAdapterIn);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (GocsdkCallbackImp.hfpStatus >= 3) {
                            clickItemCall(mSimpleAdapterIn, position);
                        } else {
                            ToastFactory.showToast(CallogActivity.this, "请先连接设备");
                        }
                    }

                });
                break;
            case 1:
                mSimpleAdapterOut = new SimpleAdapter(this,
                        call_log_out,
                        R.layout.call_log_out_listview_item_view,
                        new String[]{"itemName", "itemNum", "itemTime"},
                        new int[]{R.id.tv_out_name, R.id.tv_out_number, R.id.tv_out_time});

                listView.setAdapter(mSimpleAdapterOut);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
//                        if (GocsdkCallbackImp.hfpStatus >= 3) {
                            clickItemCall(mSimpleAdapterOut, position);
//                        } else {
//                            ToastFactory.showToast(CallogActivity.this, "请先连接设备");
//                        }

                    }
                });
                break;
            case 2:
                mSimpleAdapterMiss = new SimpleAdapter(this,
                        call_log_miss,
                        R.layout.call_log_miss_listview_item_view,
                        new String[]{"itemName", "itemNum", "itemTime"},
                        new int[]{R.id.tv_miss_name, R.id.tv_miss_number, R.id.tv_miss_time});

                listView.setAdapter(mSimpleAdapterMiss);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (GocsdkCallbackImp.hfpStatus >= 3) {
                            clickItemCall(mSimpleAdapterMiss, position);
                        } else {
                            ToastFactory.showToast(CallogActivity.this, "请先连接设备");
                        }

                    }
                });
                break;
        }

    }

    // 拨打正确的电话
    private static void placeCall(String mLastNumber) {
        if (mLastNumber.length() == 0)
            return;
        if (PhoneNumberUtils.isGlobalPhoneNumber(mLastNumber)) {
            // place the call if it is a valid number
            Log.d("GocApp", "isGlobalPhoneNumber 3: " + mLastNumber);
            if (mLastNumber == null || !TextUtils.isGraphic(mLastNumber)) {
                // There is no number entered.
                return;
            }
            try {
                Myapplication.getService().phoneDail(mLastNumber);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    private void dataTest() {
        //测试////////////////////////////////////////
        Map<String, String> map = null;
        map = new HashMap<String, String>();
        String name = null;
        name = "周杰伦";
        map.put("itemName", name);
        map.put("itemNum", "13560226285");
        Map<String, String> map6 = null;
        map6 = new HashMap<String, String>();
        String name2 = null;
        name2 = "中国联通";
        map6.put("itemName", name2);
        map6.put("itemNum", "13560226285");
        call_log_in.add(map);
        call_log_in.add(map6);
        mSimpleAdapterIn.notifyDataSetChanged();
        rl_downloading.setVisibility(View.GONE);
        vp_content.setVisibility(View.VISIBLE);
        //
        Map<String, String> map1 = null;
        map1 = new HashMap<String, String>();
        String name1 = null;
        name1 = "范玮琪";
        map1.put("itemName", name1);
        map1.put("itemNum", "13565893882");
        Map<String, String> map7 = null;
        map7 = new HashMap<String, String>();
        String name7 = null;
        name7 = "张三";
        map7.put("itemName", name7);
        map7.put("itemNum", "10086");
        call_log_out.add(map1);
        call_log_out.add(map7);
        mSimpleAdapterOut.notifyDataSetChanged();
        rl_downloading.setVisibility(View.GONE);
        vp_content.setVisibility(View.VISIBLE);
        Map<String, String> map2 = null;
        map2 = new HashMap<String, String>();
        String name4 = null;
        name4 = "中国电信";
        map2.put("itemName", name4);
        map2.put("itemNum", "13538380413");
        Map<String, String> map11 = null;
        map11 = new HashMap<String, String>();
        String name11 = null;
        name11 = "王五";
        map11.put("itemName", name11);
        map11.put("itemNum", "10010");
        Map<String, String> map12 = null;
        map12 = new HashMap<String, String>();
        String name12 = null;
        name12 = "李四";
        map12.put("itemName", name12);
        map12.put("itemNum", "13789473827");
        call_log_miss.add(map12);
        call_log_miss.add(map11);
        call_log_miss.add(map2);
        mSimpleAdapterMiss.notifyDataSetChanged();
        rl_downloading.setVisibility(View.GONE);
        vp_content.setVisibility(View.VISIBLE);
        ////////////////////////////////////////
    }


}