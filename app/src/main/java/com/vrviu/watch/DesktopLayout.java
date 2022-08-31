package com.vrviu.watch;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DesktopLayout extends LinearLayout {
    private final View view;
    private final TextView time;
    private int index = 0;
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
}
