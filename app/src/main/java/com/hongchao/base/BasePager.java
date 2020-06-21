package com.hongchao.base;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hongchao.R;
import com.hongchao.activity.MainActivity;

/**
 * 作用：基类或者公共类
 * homepager，newscenterPager，smartServicePager
 * GovaffairPager，settingsPager都继承于BasePager
 */
public class BasePager {
    //上下文（MainActivity）
    public final Context context;
    //视图，代表不同的页面
    public View rootView;
    //显示标题
    public TextView tv_title;
    //点击侧滑
    public ImageButton ib_menu;
    //加载各个子页面
    public FrameLayout fl_content;

    public BasePager(Context context) {
        this.context = context;
        //构造方法一执行，视图就开始被初始化了
        rootView = initView();
    }

    /**
     * 用于初始化公共部分视图，并且初始化加载子视图的frameLayout
     * @return
     */
    private View initView() {
        //基类的页面
        View view = View.inflate(context, R.layout.base_pager,null);
        tv_title = view.findViewById(R.id.tv_title);
        ib_menu = view.findViewById(R.id.ib_menu);
        fl_content = view.findViewById(R.id.fl_content);
        ib_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.getSlidingMenu().toggle();//关<-->开
            }
        });
        return view;
    }

    /**
     * 初始化数据，当孩子需要初始化数据，或者绑定数据，
     * 联网请求数据并且绑定的时候，重写该方法
     */
    public void initData(){

    }
}
