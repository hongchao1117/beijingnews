package com.hongchao.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hongchao.R;
import com.hongchao.activity.MainActivity;
import com.hongchao.adapter.ContentFragmentAdapter;
import com.hongchao.base.BaseFragment;
import com.hongchao.base.BasePager;
import com.hongchao.pager.GovaffairPager;
import com.hongchao.pager.HomePager;
import com.hongchao.pager.NewsCenterPager;
import com.hongchao.pager.SettingsPager;
import com.hongchao.pager.SmartServicePager;
import com.hongchao.utils.LogUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 正文fragment
 */
public class ContentFragment extends BaseFragment {

    @ViewInject(R.id.viewpager)
    private ViewPager viewPager;

    @ViewInject(R.id.rg_main)
    private RadioGroup rg_main;

    //装5个页面的集合
    private ArrayList<BasePager> basePagers;

    @Override
    public View initView() {
        LogUtil.e("正文fragment视图被初始化了");
        View view = View.inflate(context, R.layout.fragment_content, null);
//               viewPager = view.findViewById(R.id.viewpager);
//        rg_main = view.findViewById(R.id.rg_main);

        //1.吧视图注入到框架中，ContentFragment.this和view关联起来
        x.view().inject(ContentFragment.this, view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("正文fragment数据被初始化了");
        //初始化五个页面，并且放入集合中
        basePagers = new ArrayList<>();
        basePagers.add(new HomePager(context));//主页面
        basePagers.add(new NewsCenterPager(context));//新闻中心主页面
        basePagers.add(new SmartServicePager(context));//智慧服务主页面
        basePagers.add(new GovaffairPager(context));//政要主页面
        basePagers.add(new SettingsPager(context));//设置中心主页面

        //设置viewpager的适配器
        viewPager.setAdapter(new ContentFragmentAdapter(basePagers));

        //设置RadioGroup的选中状态改变的监听
        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());

        //监听某个页面被选中，初始对应的页面的数据
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());

        //设置默认选择首页
        rg_main.check(R.id.rb_home);
        basePagers.get(0).initData();//初始化首页
        //设置slidingMenu不可以滑动
        isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
    }

    /**
     * 得到新闻中心
     * @return
     */
    public NewsCenterPager getNewsCenterPager() {
        return (NewsCenterPager) basePagers.get(1);
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener{

        /**
         * @param position
         * @param positionOffset
         * @param positionOffsetPixels
         */
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        /**
         * 当某个页面被选中的方法
         * @param position
         */
        @Override
        public void onPageSelected(int position) {
            BasePager basePager = basePagers.get(position);
            basePager.initData();
            // basePagers.get(position).initData();//等价于以上代码

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        /**
         * @param group     RadioGroup
         * @param checkedId 被选中的RadioGroup的id
         */
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_home://主页面
                    viewPager.setCurrentItem(0,false);//false：没有动画，默认有动画
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
                    break;
                case R.id.rb_newscenter://新闻中心
                    viewPager.setCurrentItem(1,false);
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    break;
                case R.id.rb_smartservice://指挥服务radiobutton的id
                    viewPager.setCurrentItem(2,false);
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
                    break;
                case R.id.rb_govaffair://政要
                    viewPager.setCurrentItem(3,false);
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
                    break;
                case R.id.rb_setting://设置中心
                    viewPager.setCurrentItem(4,false);
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
                    break;
            }
        }
    }

    /**
     * 根据传入的参数设置是否让slidingMenu可以滑动
     * @param touchModeFullScreen
     */
    private void isEnableSlidingMenu(int touchModeFullScreen) {
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.getSlidingMenu().setTouchModeAbove(touchModeFullScreen);
    }


}
