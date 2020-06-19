package com.hongchao.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hongchao.R;
import com.hongchao.SplashActivity;
import com.hongchao.utils.CacheUtils;
import com.hongchao.utils.DensityUtil;

import java.util.ArrayList;

public class GuideActivity extends AppCompatActivity {
    private static final String TAG = "GuideActivity";
    private ViewPager viewPager;
    private Button btn_start_main;
    private LinearLayout ll_point_group;
    private ImageView iv_red_point;

    private ArrayList<ImageView> imageViews;
    //两点的距离
    private int leftMax;
    private int widthDpi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        viewPager = findViewById(R.id.viewpager);
        btn_start_main = findViewById(R.id.btn_start_main);
        ll_point_group = findViewById(R.id.ll_point_group);
        iv_red_point = findViewById(R.id.iv_red_point);
        //准备数据
        int[] ids = new int[]{
                R.drawable.guide_1,
                R.drawable.guide_2,
                R.drawable.guide_3
        };

        //转换像素
        widthDpi = DensityUtil.dip2px(this,10);
        Log.e(TAG, "onCreate: "+widthDpi );

        imageViews = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            ImageView imageView = new ImageView(this);
            //设置背景
            imageView.setBackgroundResource(ids[i]);
            //添加到集合中
            imageViews.add(imageView);

            //创建点 添加到线性布局里去
            ImageView point = new ImageView(this);
            point.setBackgroundResource(R.drawable.point_normal);
            //单位是像素
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthDpi, widthDpi);
            if (i != 0) {
                //不包括第0个，所有的点距离左边有20个像素
                params.leftMargin = widthDpi;
            }
            point.setLayoutParams(params);
            //添加到线性布局里去
            ll_point_group.addView(point);
        }

        //设置viewpager的适配器
        viewPager.setAdapter(new MyPagerAdapter());

        //根据view的生命周期，当视图执行到onLayout或者onDraw的时候，视图的高和宽，边距都有了
        iv_red_point.getViewTreeObserver().addOnGlobalLayoutListener(new MyOnGlobalLayoutListener());

        //得到屏幕滑动的百分比
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());

        //设置btn_start_main按钮的点击事件
        btn_start_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1、保持曾经进入过主页面
                CacheUtils.putBoolean(GuideActivity.this, SplashActivity.START_MAIN,true);
                //2、跳转到主页面
                Intent intent = new Intent(GuideActivity.this,MainActivity.class);
                startActivity(intent);
                //3、关闭引导页面
//                finish();
            }
        });
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener{

        /**
         * 当页面滚动了 会回调这个方法
         * @param position 当前滑动页面的位置
         * @param positionOffset 页面滑动 的百分比
         * @param positionOffsetPixels 滑动的像素
         */
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //两点间移动的距离 = 屏幕滑动百分比 * 间距
//            int leftMargin = (int) (positionOffset*leftMax);
//            Log.d(TAG, "position = "+position +",positionOffset = "+positionOffset+",positionOffsetPixels = "+positionOffsetPixels);
            //两点间滑动距离对应的坐标 = 原来的其实坐标 + 两点间的移动距离
            int leftMargin = (int) (position*leftMax + (positionOffset*leftMax));
            //params.leftMargin = 两点间滑动距离对应的坐标
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_red_point.getLayoutParams();
            params.leftMargin = leftMargin;
            iv_red_point.setLayoutParams(params);
        }

        /**
         * 当页面被选中的时候，回调这个方法
         * @param position 被选中页面的对应的位置
         */
        @Override
        public void onPageSelected(int position) {
            if (position==imageViews.size()-1){
                btn_start_main.setVisibility(View.VISIBLE);
            }else{
                btn_start_main.setVisibility(View.GONE);
            }
        }

        /**
         * 当viewpager页面滑动状态发生变化的时候
         * @param state
         */
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    class MyOnGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

        @Override
        public void onGlobalLayout() {
            //执行不止一次
            iv_red_point.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            //间距 = 第1个点距离左边的距离 - 第0个点距离左边的距离
            leftMax = ll_point_group.getChildAt(1).getLeft() - ll_point_group.getChildAt(0).getLeft();

        }
    }

    class MyPagerAdapter extends PagerAdapter {

        /**
         * 返回数据的总个数
         *
         * @return
         */
        @Override
        public int getCount() {
            return imageViews.size();
        }

        /**
         * 判断
         *
         * @param view   当前创建的视图
         * @param object 下面instantiateItem 返回的结果值
         * @return
         */
        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        /**
         * 作用类似 getView
         *
         * @param container viewpager
         * @param position  要创建页面的位置
         * @return 返回和创建当前页面有关系的值
         */
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = imageViews.get(position);
            //添加到容器中
            container.addView(imageView);
            return imageView;
        }

        /**
         * 销毁
         *
         * @param container viewpager
         * @param position  要销毁页面的位置
         * @param object    要销毁的页面
         */
        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//            super.destroyItem(container, position, object);
            container.removeView((View) object);
        }
    }
}
