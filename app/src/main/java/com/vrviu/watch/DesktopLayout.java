package com.vrviu.watch;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class DesktopLayout extends LinearLayout implements PointLocationView.CallBack {
    private final View view;
    private final TextView time;
    private int index = 0;
    private PointLocationView pv = null;
    private final int[] color = {Color.RED,Color.GREEN,Color.BLUE};

    public DesktopLayout(Context context) {
        super(context);
        setOrientation(LinearLayout.VERTICAL); //水平排列

        //设置宽高为包含内容
        this.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        view = LayoutInflater.from(context).inflate(R.layout.float_time,null);
        addView(view);

        time = (TextView) findViewById(R.id.textViewTime);
        pv = findViewById(R.id.pv_touch);
        pv.setCallBack(this);
    }

    public abstract void onHidePointLocation();

    public void showPointLocation() {
        GradientDrawable background = (GradientDrawable)getResources().getDrawable(R.drawable.shape);
        background.setColor(Color.WHITE);
        background.setAlpha(0xAA);
        view.setBackgroundDrawable(background);

        time.setTextColor(Color.BLACK);
        pv.setVisibility(View.VISIBLE);
    }

    public void hidePointLocation() {
        changeBackground();
        time.setTextColor(Color.WHITE);
        pv.setVisibility(View.GONE);
    }

    //对外接口用来改变显示内容
    public void setTextTime(CharSequence sysTime){
        time.setText(sysTime);
    }

    public void changeBackground() {
        GradientDrawable background = (GradientDrawable)getResources().getDrawable(R.drawable.shape);
        background.setColor(color[index++%color.length]);
        background.setAlpha(0xAA);
        view.setBackgroundDrawable(background);
    }

    @Override
    public void finish() {
        onHidePointLocation();
        hidePointLocation();
    }
}
