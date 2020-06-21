package com.hongchao.menudetailpager;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hongchao.R;
import com.hongchao.activity.MainActivity;
import com.hongchao.base.MenuDetailBasePager;
import com.hongchao.domain.NewsCenterPagerBean2;
import com.hongchao.menudetailpager.tabdetailpager.TabDetailPager;
import com.hongchao.utils.LogUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 作用：新闻详情页面
 */
public class NewsMenuDetailPager extends MenuDetailBasePager {
    @ViewInject(R.id.tabLayout)
    private TabLayout tableLayout;

    @ViewInject(R.id.viewpager)
    private ViewPager viewPager;

    @ViewInject(R.id.ib_tab_next)
    private ImageButton ib_tab_next;


    /**
     * 页签页面的数据的集合
     *
     * @param context
     */
    private List<NewsCenterPagerBean2.DetailPagerData.ChildrenData> children;
    //页签页面的集合-页面
    private ArrayList<TabDetailPager> tabDetailPagers;

    public NewsMenuDetailPager(Context context, NewsCenterPagerBean2.DetailPagerData detailPagerData) {
        super(context);
        children = detailPagerData.getChildren();
    }


    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.news_menu_detail_pager, null);
        x.view().inject(NewsMenuDetailPager.this, view);
        //设置点击事件，下一个页面
        ib_tab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
            }
        });
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("新闻详情页面的数据被初始化了");
        //准备新闻详情页面的数据
        tabDetailPagers = new ArrayList<>();
        for (int i = 0; i < children.size(); i++) {
            tabDetailPagers.add(new TabDetailPager(context,children.get(i)));
        }

        //设置viewpager的适配器
        viewPager.setAdapter(new MyNewsMenuDetailPagerAdapter());
        //viewpager 和 tablayout关联
        tableLayout.setupWithViewPager(viewPager);
        tableLayout.setTabTextColors(ColorStateList.valueOf(Color.RED));
        tableLayout.setSelectedTabIndicatorColor(Color.RED);
        tableLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        //主页以后监听页面的变化，tableLayout监听页面的变化
        tableLayout.addOnTabSelectedListener(new MyBaseOnTabSelectedListener());

    }
    /**
     * 根据传入的参数设置是否让slidingMenu可以滑动
     * @param touchModeFullScreen
     */
    private void isEnableSlidingMenu(int touchModeFullScreen) {
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.getSlidingMenu().setTouchModeAbove(touchModeFullScreen);
    }

    class MyBaseOnTabSelectedListener implements TabLayout.BaseOnTabSelectedListener {

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            if (tab.getPosition()==0){
                //slidingMenu可以全屏滑动
                isEnableSlidingMenu(SlidingMenu.TOUCHMODE_FULLSCREEN);
            }else{
                isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }




    class MyNewsMenuDetailPagerAdapter extends PagerAdapter{
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return children.get(position).getTitle();
        }

        @Override
        public int getCount() {
            return tabDetailPagers.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            TabDetailPager tabDetailPager = tabDetailPagers.get(position);
            View rootView = tabDetailPager.rootView;
            tabDetailPager.initData();//初始化数据
            container.addView(rootView);
            return rootView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

}
