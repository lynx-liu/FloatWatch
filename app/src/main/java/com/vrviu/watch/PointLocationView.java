package com.vrviu.watch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class PointLocationView extends View {
	private static class PointerState {
		private boolean mCurDown;
		private int mCurX;
		private int mCurY;
		private float mCurPressure;
		private float mCurSize;
		private int mCurWidth;
	}

	private final Paint mTextPaint;
	private final Paint mTextBackgroundPaint;
	private final Paint mPaint;
	private final Paint mTargetPaint;
	private final FontMetricsInt mTextMetrics = new FontMetricsInt();
	private int mHeaderBottom;
	private boolean mCurDown;
	private int mCurNumPointers;
	private int mMaxNumPointers;
	private final ArrayList<PointerState> mPointers = new ArrayList<PointerState>();
	private CallBack mCallBack;

	public void setCallBack(CallBack callBack){
		this.mCallBack=callBack;
	}

	public interface CallBack{
		void finish();
	}

	public PointLocationView(Context context, AttributeSet paramAttributeSet) {
		super(context, paramAttributeSet);

		setFocusable(true);
		
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(10 * getResources().getDisplayMetrics().density);
		mTextPaint.setARGB(255, 0, 0, 0);

		mTextBackgroundPaint = new Paint();
		mTextBackgroundPaint.setAntiAlias(false);
		mTextBackgroundPaint.setARGB(128, 255, 255, 255);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setARGB(255, 255, 255, 255);
//		mPaint.setStyle(Paint.Style.STROKE);//空心
		mPaint.setStrokeWidth(1);

		mTargetPaint = new Paint();
		mTargetPaint.setAntiAlias(false);
		mTargetPaint.setARGB(255, 0, 0, 192);
	}

	public PointLocationView(Context context) {
		super(context);
		setFocusable(true);

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(10 * getResources().getDisplayMetrics().density);
		mTextPaint.setARGB(255, 0, 0, 0);

		mTextBackgroundPaint = new Paint();
		mTextBackgroundPaint.setAntiAlias(false);
		mTextBackgroundPaint.setARGB(128, 255, 255, 255);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setARGB(255, 255, 255, 255);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(1);

		mTargetPaint = new Paint();
		mTargetPaint.setAntiAlias(false);
		mTargetPaint.setARGB(255, 0, 0, 192);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mTextPaint.getFontMetricsInt(mTextMetrics);
		mHeaderBottom = -mTextMetrics.ascent + mTextMetrics.descent + 2;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		synchronized (mPointers) {
			final int w = getWidth();
			final int itemW = w / 7;
			final int base = -mTextMetrics.ascent + 1;
			final int bottom = mHeaderBottom;
			final int NP = mPointers.size();
			if (NP > 0) {
				canvas.drawRect(0, 0, itemW - 1, bottom, mTextBackgroundPaint);
				canvas.drawText("P: " + mCurNumPointers + " / " + mMaxNumPointers, 1, base, mTextPaint);
			}

			mPaint.setColor((mCurNumPointers > 0)? Color.BLUE:Color.RED);
			canvas.drawRect(new Rect(getWidth()/2-getHeight()/20,0,getWidth()/2+getHeight()/20,getHeight()/10),mPaint);

			for (int p = 0; p < NP; p++) {
				final PointerState ps = mPointers.get(p);

				if (mCurDown && ps.mCurDown) {
					canvas.drawLine(0, (int) ps.mCurY, getWidth(), (int) ps.mCurY, mTargetPaint);
					canvas.drawLine((int) ps.mCurX, 0, (int) ps.mCurX, getHeight(), mTargetPaint);
					int pressureLevel = (int) (ps.mCurPressure * 255);
					mPaint.setARGB(255, pressureLevel, 128, 255 - pressureLevel);
					canvas.drawPoint(ps.mCurX, ps.mCurY, mPaint);
//					canvas.drawCircle(ps.mCurX, ps.mCurY, ps.mCurWidth, mPaint);
					canvas.drawRect(new Rect(ps.mCurX-ps.mCurWidth,ps.mCurY-ps.mCurWidth,ps.mCurX+ps.mCurWidth,ps.mCurY+ps.mCurWidth),mPaint);
				}
			}
		}
	}

	public void addTouchEvent(MotionEvent event) {
		synchronized (mPointers) {
			int action = event.getAction();
			if (action == MotionEvent.ACTION_DOWN) {
				int NP = mPointers.size();
				for (int p = 0; p < NP; p++) {
					final PointerState ps = mPointers.get(p);
					ps.mCurDown = false;
				}

				PointerState ps = new PointerState();
				mPointers.add(ps);

				mPointers.get(0).mCurDown = true;
				mMaxNumPointers = 0;
			}

			if ((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN) {
				final int index = (action
						& MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				final int id = event.getPointerId(index);
				int NP = mPointers.size();
				while (NP <= id) {
					PointerState ps = new PointerState();
					mPointers.add(ps);
					NP++;
				}

				if (mPointers.size() > id) {
					final PointerState ps = mPointers.get(id);
					ps.mCurDown = true;
				}
			}

			final int NI = event.getPointerCount();

			mCurDown = action != MotionEvent.ACTION_UP && action != MotionEvent.ACTION_CANCEL;
			mCurNumPointers = mCurDown ? NI : 0;
			if (mMaxNumPointers < mCurNumPointers) {
				mMaxNumPointers = mCurNumPointers;
			}

			for (int i = 0; i < NI; i++) {
				final int id = event.getPointerId(i);
				if (mPointers.size() > id) {
					final PointerState ps = mPointers.get(id);
					ps.mCurX = (int) event.getX(i);
					ps.mCurY = (int) event.getY(i);
					ps.mCurPressure = event.getPressure(i);
					ps.mCurSize = event.getSize(i);
					ps.mCurWidth = (int) (ps.mCurSize * getHeight()/5);
				}
			}

			if ((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP) {
				final int index = (action
						& MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				final int id = event.getPointerId(index);
				if (mPointers.size() > id) {
					final PointerState ps = mPointers.get(id);
					ps.mCurDown = false;
				}
			}

			if (action == MotionEvent.ACTION_UP) {
				for (int i = 0; i < NI; i++) {
					final int id = event.getPointerId(i);
					if (mPointers.size() > id) {
						final PointerState ps = mPointers.get(id);
						if (ps.mCurDown) {
							ps.mCurDown = false;
						}
					}
				}

				if(mMaxNumPointers>=5) {
					if(mCallBack!=null){
						mCallBack.finish();
					}
				}
			}
			postInvalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d("onTouch","action:"+event.getAction());
		addTouchEvent(event);
		return true;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		Log.i("Pointer", "Trackball: " + event);
		return super.onTrackballEvent(event);
	}
}
