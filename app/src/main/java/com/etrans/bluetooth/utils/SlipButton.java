package com.etrans.bluetooth.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewParent;

import com.etrans.bluetooth.R;


public class SlipButton extends View implements OnTouchListener {

	private boolean NowChoose = false;// 锟斤拷录锟斤拷前锟斤拷钮锟角凤拷锟�true为锟斤拷,flase为锟截憋拷

    private boolean isChecked;

    private boolean OnSlip = false;// 锟斤拷录锟矫伙拷锟角凤拷锟节伙拷锟斤拷锟侥憋拷锟斤拷

    private float DownX, NowX;// 锟斤拷锟斤拷时锟斤拷x,锟斤拷前锟斤拷x

    private Rect Btn_On, Btn_Off;// 锟津开和关憋拷状态锟斤拷,锟轿憋拷锟絉ect .

    private boolean isChgLsnOn = false;

    private OnChangedListener ChgLsn;

    private Bitmap bg_on, bg_off, slip_off , slip_on;

    public SlipButton(Context context) {
        super(context);
        init();
    }

    public SlipButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlipButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        bg_on = BitmapFactory.decodeResource(getResources(), R.drawable.switch_track_on);
        slip_on = BitmapFactory.decodeResource(getResources(), R.drawable.switch_thume_on4);
        
        bg_off = BitmapFactory.decodeResource(getResources(), R.drawable.switch_track_off);
        slip_off = BitmapFactory.decodeResource(getResources(), R.drawable.switch_thume_off3);
        Btn_On = new Rect(0, 0, slip_on.getWidth(), slip_on.getHeight());
        Btn_Off = new Rect(bg_off.getWidth() - slip_off.getWidth(), 0, bg_off.getWidth(), slip_off.getHeight());
        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        Matrix matrix = new Matrix();
        Paint paint = new Paint();
        float x;

        if (NowX < (bg_on.getWidth() / 2))
        {
            x = NowX - slip_off.getWidth() / 2;
            canvas.drawBitmap(bg_off, matrix, paint);
        }

        else {
            x = bg_on.getWidth() - slip_off.getWidth() / 2;
            canvas.drawBitmap(bg_on, matrix, paint);
        }

        if (OnSlip)

        {
            if (NowX >= bg_on.getWidth())

            x = bg_on.getWidth() - slip_off.getWidth() / 2;

            else if (NowX < 0) {
                x = 0;
            } else {
                x = NowX - slip_off.getWidth() / 2;
            }
        } else {

            if (NowChoose)
            {
                x = Btn_Off.left;
                canvas.drawBitmap(bg_on, matrix, paint);
            } else x = Btn_On.left;
        }
        if (isChecked) {
            canvas.drawBitmap(bg_on, matrix, paint);
            x = Btn_Off.left;
            isChecked = !isChecked;
        }

        if (x < 0)
        x = 0;
        else if (x > bg_on.getWidth() - slip_off.getWidth()) x = bg_on.getWidth()
                - slip_off.getWidth();
        
        if(!OnSlip) {
       	 if(!NowChoose) {
	        	canvas.drawBitmap(bg_off, matrix, paint);
	        	canvas.drawBitmap(slip_off, x, 0, paint);
	        }else {
	        	canvas.drawBitmap(bg_on, matrix, paint);
	        	canvas.drawBitmap(slip_on, x, 0, paint);
	        }
       }else {
       	 if (NowX < (bg_on.getWidth() / 2))
 	        {
       		 canvas.drawBitmap(slip_off, x, 0, paint);
 	        } else {
 	        	canvas.drawBitmap(slip_on, x, 0, paint);
 	        }
       }

    }
    
    

    @Override
	public boolean dispatchTouchEvent(MotionEvent event) {
        ViewParent parent = getParent();
    	parent.requestDisallowInterceptTouchEvent(true);
		return super.dispatchTouchEvent(event);
	}

	public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                NowX = event.getX();
                break;

            case MotionEvent.ACTION_DOWN:

                if (event.getX() > bg_on.getWidth() || event.getY() > bg_on.getHeight()) return false;
                OnSlip = true;
                DownX = event.getX();
                NowX = DownX;
                break;

            case MotionEvent.ACTION_CANCEL:

                OnSlip = false;
                boolean choose = NowChoose;
                if (NowX >= (bg_on.getWidth() / 2)) {
                    NowX = bg_on.getWidth() - slip_off.getWidth() / 2;
                    NowChoose = true;
                } else {
                    NowX = NowX - slip_off.getWidth() / 2;
                    NowChoose = false;
                }
                if (isChgLsnOn && (choose != NowChoose)) 
                ChgLsn.OnChanged(NowChoose);
                break;
            case MotionEvent.ACTION_UP:

                OnSlip = false;
                boolean LastChoose = NowChoose;

                if (event.getX() >= (bg_on.getWidth() / 2)) {
                    NowX = bg_on.getWidth() - slip_off.getWidth() / 2;
                    NowChoose = true;
                }

                else {
                    NowX = NowX - slip_off.getWidth() / 2;
                    NowChoose = false;
                }

                if (isChgLsnOn && (LastChoose != NowChoose)) 

                ChgLsn.OnChanged(NowChoose);
                break;
            default:
        }
        invalidate();
        return true;
    }

    public void SetOnChangedListener(OnChangedListener l) {
        isChgLsnOn = true;
        ChgLsn = l;
    }

    public interface OnChangedListener {

        abstract void OnChanged(boolean CheckState);
    }

    public void setCheck(boolean isChecked) {
        this.isChecked = isChecked;
        NowChoose = isChecked;
       this.postInvalidate();
    }
}
