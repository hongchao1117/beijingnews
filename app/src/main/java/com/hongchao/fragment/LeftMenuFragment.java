package com.hongchao.fragment;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.hongchao.base.BaseFragment;
import com.hongchao.utils.LogUtil;

import org.w3c.dom.Text;


public class LeftMenuFragment extends BaseFragment {
    private TextView textView;

    @Override
    public View initView() {
        textView = new TextView(context);
        textView.setTextSize(23);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("左侧菜单数据被初始化了");
        textView.setText("左侧菜单页面");
    }
}
