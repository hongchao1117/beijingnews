package com.hongchao.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.hongchao.R;
import com.hongchao.fragment.ContentFragment;
import com.hongchao.fragment.LeftMenuFragment;
import com.hongchao.utils.DensityUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity {

    public static final String MAIN_CONTENT_TAG = "main_content_tag";
    public static final String LEFT_MENU_TAG = "left_menu_tag";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSlidingMenu();
        //初始化fragment
        initFragment();
    }

    private void initSlidingMenu() {
        //1、设置主页面
        setContentView(R.layout.activity_main);
        //2、设置左侧菜单
        setBehindContentView(R.layout.acticity_left_menu);
        //3、设置右侧菜单
        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setSecondaryMenu(R.layout.acticity_right_menu);//设置右侧菜单
        //4、设置显示的模式：左侧菜单+主页，左侧菜单+主页面+右侧菜单
        slidingMenu.setMode(SlidingMenu.LEFT);//只有滑动左侧有效果
        //5、设置滑动模式：滑动边缘，全屏滑动，不可以滑动
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //6、设置主页占据的宽度
        slidingMenu.setBehindOffset(DensityUtil.dip2px(MainActivity.this,200));
    }

    private void initFragment() {
        //1.得到fragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        //2.开启事务
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //3.替换
        fragmentTransaction.replace(R.id.fl_main_content,new ContentFragment(), MAIN_CONTENT_TAG);//主页
        fragmentTransaction.replace(R.id.fl_left_menu,new LeftMenuFragment(), LEFT_MENU_TAG);//主页
        //4.提交
        fragmentTransaction.commit();
    }
}
