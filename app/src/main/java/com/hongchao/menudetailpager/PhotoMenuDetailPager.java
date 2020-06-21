package com.hongchao.menudetailpager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.hongchao.base.MenuDetailBasePager;
import com.hongchao.utils.LogUtil;

/**
 * 作用：图组详情页面
 */
public class PhotoMenuDetailPager extends MenuDetailBasePager {


    private TextView textView;

    public PhotoMenuDetailPager(Context context) {
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

        LogUtil.e("图组详情页面数据被初始化了");
        textView.setText("设置图组详情页面内容");
    }
}
