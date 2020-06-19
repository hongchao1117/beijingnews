package com.hongchao.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import com.hongchao.base.BasePager;
import com.hongchao.utils.LogUtil;

/**
 * 作用：智慧服务
 */
public class SmartServicePager extends BasePager {
    public SmartServicePager(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("智慧服务页面数据被初始化了...");

        //1、设置标题
        tv_title.setText("智慧服务");
        //2、联网请求，得到数据，创建视图
        TextView textView = new TextView(context);
        textView.setText("智慧服务内容");
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        textView.setTextSize(25);
        //3、把子视图添加到BasePager的frameLayout
        fl_content.addView(textView);
        //4、绑定数据
        textView.setText("智慧服务内容");




    }
}
