package com.etrans.bluetooth.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoScrollViewPager extends ViewPager {

	private boolean noScroll = true;

	public NoScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NoScrollViewPager(Context context) {
		super(context);
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		if(noScroll){
			return false;//不拦截子控件的滑动事�?
		}else{
			return super.onInterceptTouchEvent(ev);
		}
		

	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		if(noScroll){
			return true;//让该ViewPager不能滑动
		}else{
			return super.onTouchEvent(ev);
		}

	}
}
