package com.etrans.bluetooth.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

/**
 * Created by ping on 2016/8/29.
 * 音频频谱 控件
 */
public class SpectrumSurfaceView extends SurfaceView implements  SurfaceHolder.Callback{
	//绘制时间间隔
	private int mDrawtimes = 200;
	//绘制频谱buffer
	private byte[] mBuffer;
	private SurfaceHolder mHolder;
	private DrawThread mThread;
	private PaintFlagsDrawFilter mPaintFlag;
	//水平个数
	private int mHorCount = 26;
	//垂直个数
	private int mVerCount = 20;

	Random mRandom = new Random(System.currentTimeMillis());

	public SpectrumSurfaceView(Context context) {
		super(context);
		init();
	}

	/**
	 * 设置水平要绘制的矩形个数
	 * @param count
	 */
	public void setHorCount(int count){
		mHorCount = count;
	}

	/**
	 * 设置垂直绘制矩形个数
	 * @param count
	 */
	public void setVerCount(int count){
		mVerCount = count;
	}

	public SpectrumSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SpectrumSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private boolean mbRandom = false;
	public void setRandom(boolean bRandom){
		mbRandom = bRandom;
	}


	private void init(){
		mThread = new DrawThread();
		mThread.start();
		mPaintFlag = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mHolder = getHolder();
		mHolder.addCallback(this);
		setZOrderOnTop(true);
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
	}

	/**
	 * 释放资源
	 */
	public void destory(){
		mThread.exit();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mThread.running();
		bDestory = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		bDestory = true;
		mThread.waitting();
	}


	private synchronized void setBufferEx(byte [] buffer){
		mBuffer = buffer;
	}

	private  boolean bDestory = false;
	/**
	 * 设置频谱 buffer
	 *
	 * @param buffer
	 */
	public void setBuffer(byte[] buffer){
		if(bDestory){
			return;
		}

		if(mbRandom){
			return;
		}

		long curTime = System.currentTimeMillis();
		if(mLastTime != 0 && curTime - mLastTime < 150){
			return;
		}
		mLastTime = curTime;
		mBuffer = buffer;
	}

	private synchronized void draw(){
		Canvas canvas = null;
		canvas = mHolder.lockCanvas();
		if (canvas == null)
			return;
		canvas.setDrawFilter(mPaintFlag);
		int len = this.getWidth();
		int xStart = (len - mHorCount * 17) / 2;
		int cnt = 0;
		int x = xStart;
		int y = 0;
		int count = 0;
		for(int i = 0; i < mHorCount; i ++){
			y = 0;
			int startIdx = mVerCount;
			if(mBuffer != null && mBuffer.length > mHorCount) {
				cnt = mBuffer.length / mHorCount;
				int val = 0;
				int max = (i+1)*cnt - 1;
				for(int k = i*cnt; k < max; k ++){
					//全部取大于0的值
					val += mBuffer[k]+132 ;
				}
				startIdx = mVerCount - val/(cnt*13);
			}

			for(int j = 0; j < mVerCount; j++){
				count ++;
				y+= 3;
				Paint p = new Paint();
				p.setAntiAlias(true);
				if(j>startIdx) {
					p.setColor(Color.rgb(13,107,150));
				}else{
					p.setColor(Color.rgb(97, 97, 97));// 设置灰色
				}
				p.setStyle(Paint.Style.FILL);//设置填满
				canvas.drawRect(x, y, x+14, y+3, p);// 长方形
				y+=3;
			}
			x += 17;
		}

		mHolder.unlockCanvasAndPost(canvas);
	}

	/**
	 * 设置绘制时间间隔
	 * @param times
	 */
	public void setDrawTimes(int times){
		mDrawtimes = times;
	}

	/**
	 * 绘制线程
	 */
	private class DrawThread extends Thread{
		public void run(){
			while (!bQuit){
				synchronized (this) {
					while (bWatting) {
						try {
							this.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				if(bQuit){
					break;
				}

				if(!mbRandom){
					draw();
				}else{
					drawRandom();
				}

				try {
					Thread.sleep(mDrawtimes);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void exit(){
			bQuit = true;
			running();
		}

		private synchronized void waitting(){
			bWatting = true;
		}

		public synchronized void running(){
			bWatting = false;
			this.notifyAll();
		}

		private boolean bWatting = true;
		private boolean bQuit = false;

	}

	private boolean bStartDrawRandom = false;

	public void startDrawRandom(){
		bStartDrawRandom = true;
	}

	public void stopDrawRandom(){
		bStartDrawRandom = false;
	}

	private void drawRandom(){
		if(bStartDrawRandom){
			drawRandomWave();
		}else{
			drawBg();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void drawBg(){
		Canvas canvas = null;
		canvas = mHolder.lockCanvas();
		if (canvas == null)
			return;
		canvas.setDrawFilter(mPaintFlag);
		int x = 0;
		int y = 0;
		int count = 0;
		for(int i = 0; i < mHorCount; i ++){
			y = 0;
			for(int j = 0; j < mVerCount; j++){
				count ++;
				y+= 3;
				Paint p = new Paint();
				p.setAntiAlias(true);
				p.setColor(Color.rgb(97, 97, 97));// 设置灰色
				p.setStyle(Paint.Style.FILL);//设置填满
				canvas.drawRect(x, y, x+14, y+3, p);// 长方形
				y+=3;
			}
			x += 17;
		}
		mHolder.unlockCanvasAndPost(canvas);
	}

	private void drawRandomWave(){
		Canvas canvas = null;
		canvas = mHolder.lockCanvas();
		if (canvas == null)
			return;
		canvas.setDrawFilter(mPaintFlag);
		int x = 0;
		int y = 0;
		int count = 0;
		for(int i = 0; i < mHorCount; i ++){
			y = 0;

			count = mRandom.nextInt(mVerCount-1);

			int startIdx = mVerCount - count;

			for(int j = 0; j < mVerCount; j++){

				y+= 3;
				Paint p = new Paint();
				p.setAntiAlias(true);
				if(j >= startIdx){
					p.setColor(Color.rgb(13,107,150));
				}else {
					p.setColor(Color.rgb(97, 97, 97));// 设置灰色
				}
				p.setStyle(Paint.Style.FILL);//设置填满
				canvas.drawRect(x, y, x+14, y+3, p);// 长方形
				y+=3;
			}
			x += 17;
		}
		mHolder.unlockCanvasAndPost(canvas);
	}


	private int mBfferRandom[] = new int[mVerCount];
	private long mLastTime = 0;
}
