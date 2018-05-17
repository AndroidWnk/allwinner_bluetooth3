package com.etrans.bluetooth.Fragment;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.etrans.bluetooth.A2dpStatus;
import com.etrans.bluetooth.MainActivity;
import com.etrans.bluetooth.R;
import com.etrans.bluetooth.event.A2dpStatusEvent;
import com.etrans.bluetooth.event.MusicInfoEvent;
import com.etrans.bluetooth.utils.SpectrumSurfaceView;
import com.etrans.bluetooth.utils.XxCircleRotateView;
import com.goodocom.gocsdk.IGocsdkService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MusicFragment extends Fragment implements View.OnClickListener{
    private MainActivity activity;
    private  View mview;
    //winner音乐\
    ImageView mBtnPlayPause;
    SeekBar mSeekBar;
    TextView mTvSongName;
    ImageView mBtnForward;
    ImageView mBtnBackward;
    TextView mCurrTime;
    TextView mTotalTime;
    private XxCircleRotateView mMusic_album;
    private SpectrumSurfaceView mSpectrumSurfaceView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载View，并返回
        mview = inflater.inflate(R.layout.musicfragment, null);
        activity = (MainActivity) getActivity();
        // 用来接收消息的
        EventBus.getDefault().register(this);
        initView();
        initListener();
        return mview;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.i("stateNK","刷新数据");
//            if (GocsdkCallbackImp.hfpStatus > 0) {
                try {
                    IGocsdkService service = MainActivity.getService();
                    if(service != null)service.getMusicInfo();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
//            }
        }
    }

    private void initView() {
        mSeekBar = (SeekBar) mview.findViewById(R.id.seekBar);
        mSeekBar.setEnabled(false);
        mCurrTime = (TextView) mview.findViewById(R.id.currTime);
        mTotalTime = (TextView) mview.findViewById(R.id.totalTime);
        mTvSongName = (TextView) mview.findViewById(R.id.tv_song_name);

        mBtnPlayPause = (ImageView) mview.findViewById(R.id.btn_play_pause);
        mBtnForward = (ImageView) mview.findViewById(R.id.btn_forward);
        mBtnBackward = (ImageView) mview.findViewById(R.id.btn_backward);

        mMusic_album = (XxCircleRotateView) mview.findViewById(R.id.music_album);
        mMusic_album.setImageBitmap(
                BitmapFactory.decodeResource(this.getResources(), R.drawable.bg_musicalbum_default));
        mSpectrumSurfaceView = (SpectrumSurfaceView) mview.findViewById(R.id.waveform_view);
        mSpectrumSurfaceView.setRandom(true);

    }
    private void initListener() {
        mBtnPlayPause.setOnClickListener(this);
        mBtnForward.setOnClickListener(this);
        mBtnBackward.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play_pause:

//                if (GocsdkCallbackImp.hfpStatus < 3) {
//                    ToastFactory.showToast(activity, "请先连接设备");
//                    return;
//                } else {
                    Log.d("app", "click play image!");
                    try {
                        if (MainActivity.getService() != null) {
                            MainActivity.getService().musicPlayOrPause();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
//                    }
                }

                break;
            case R.id.btn_forward://下一首
                try {
                    if(MainActivity.getService() != null){
                        MainActivity.getService().musicNext();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mCurrTime.setText("00:00");
                mSeekBar.setProgress(0);
                break;
            case R.id.btn_backward://上一首
                Log.d("app", "click previous");
                try {
                    if(MainActivity.getService() != null){
                        MainActivity.getService().musicPrevious();
                    }
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
                mCurrTime.setText("00:00");
                mSeekBar.setProgress(0);
                break;


        }
    }

    // 接收音乐信息方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MusicInfoEvent info) { //接收音乐信息歌名歌手
        mTvSongName.setText(info.name);
//        tv_music_artist.setText(info.artist);//歌手
        //将毫秒转化为秒
        int duration = info.duration / 1000;
        int min = duration / 60;
        int sec = duration % 60;

        String totalTime = "";
        if(min < 10)totalTime += "0";
        totalTime += min;
        totalTime += ":";
        if(sec < 10) totalTime += "0";
        totalTime += sec;

        mTotalTime.setText(totalTime);
//        tv_music_posandtotal.setText(info.pos + "/" + info.total);

//        tv_currenttime.setText("00:00");
//        sb_progress.setProgress(0);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(A2dpStatusEvent event){
        if(event.status <= A2dpStatus.CONNECTED){
            mBtnPlayPause.setImageResource(R.drawable.selector_bluetooth_music_play_nor);
            mSpectrumSurfaceView.stopDrawRandom();
//            iv_pause.setVisibility(View.VISIBLE);
//            iv_play.setVisibility(View.GONE);
        }else{
            mBtnPlayPause.setImageResource(R.drawable.selector_bluetooth_music_pause_nor);
            mSpectrumSurfaceView.startDrawRandom();
//            iv_pause.setVisibility(View.VISIBLE);
//            iv_play.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();

    }
}
