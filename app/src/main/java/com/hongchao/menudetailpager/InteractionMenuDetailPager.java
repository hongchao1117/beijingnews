package com.hongchao.menudetailpager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.hongchao.base.MenuDetailBasePager;
import com.hongchao.utils.LogUtil;

/**
 * 作用：互动详情页面
 */
public class InteractionMenuDetailPager extends MenuDetailBasePager {


    private TextView textView;

    public InteractionMenuDetailPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        textView.setTextSize(25);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();

        LogUtil.e("互动详情页面数据被初始化了");
        textView.setText("设置互动详情页面内容");
    }
}
