package com.hongchao.menudetailpager.tabdetailpager;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;
import com.hongchao.R;
import com.hongchao.base.MenuDetailBasePager;
import com.hongchao.domain.NewsCenterPagerBean2;
import com.hongchao.domain.TabDetailPagerBean;
import com.hongchao.refreshlistview.RefreshListView;
import com.hongchao.utils.CacheUtils;
import com.hongchao.utils.Constants;
import com.hongchao.utils.LogUtil;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * 作用：页签详情页面
 */
public class TopicDetailPager extends MenuDetailBasePager {
    private ViewPager viewPager;
    private TextView tv_title;
    private LinearLayout ll_point_group;
    private ListView listView;
    private List<TabDetailPagerBean.DataEntity.NewsData> news;
    private ImageOptions imageOptions;
    private final NewsCenterPagerBean2.DetailPagerData.ChildrenData childrenData;
    //    private TextView textView;
    private String url;
    /**
     * 顶部轮播图部分的数据
     */
    private List<TabDetailPagerBean.DataEntity.TopnewsData> topnews;
    private String moreUrl;//下一页的联网路径
    private boolean isLoadMore = false;//是否加载更多
    private PullToRefreshListView mPullRefreshListView;

    public TopicDetailPager(Context context, NewsCenterPagerBean2.DetailPagerData.ChildrenData childrenData) {
        super(context);
        this.childrenData = childrenData;
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(100), DensityUtil.dip2px(100))
                .setRadius(DensityUtil.dip2px(5))
                .setCrop(true)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.news_pic_default)
                .setFailureDrawableId(R.drawable.news_pic_default)
                .build();
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.topic_detail_pager, null);
        mPullRefreshListView = view.findViewById(R.id.pull_refresh_list);

        listView =  mPullRefreshListView.getRefreshableView();

        /**
         * Add Sound Event Listener
         */
        SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(context);
        soundListener.addSoundEvent(PullToRefreshBase.State.PULL_TO_REFRESH, R.raw.pull_event);
        soundListener.addSoundEvent(PullToRefreshBase.State.RESET, R.raw.reset_sound);
        soundListener.addSoundEvent(PullToRefreshBase.State.REFRESHING, R.raw.refreshing_sound);
        mPullRefreshListView.setOnPullEventListener(soundListener);

        View topNewsView = View.inflate(context, R.layout.topnews, null);
        viewPager = topNewsView.findViewById(R.id.viewpager);
        tv_title = topNewsView.findViewById(R.id.tv_title);
        ll_point_group = topNewsView.findViewById(R.id.ll_point_group);

        //把顶部轮播图部分视图，以头的方式添加到ListView中
        listView.addHeaderView(topNewsView);
        //listView.addTopNewsView(topNewsView);

        //设置监听
//        listView.setOnRefreshListener(new MyOnRefreshListener());
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getDataFromNet();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (TextUtils.isEmpty(moreUrl)) {
                    //没有更多数据
                    Toast.makeText(context, "没有更多数据..", Toast.LENGTH_SHORT).show();
//                    listView.onRefreshFinish(false);
                    mPullRefreshListView.onRefreshComplete();
                } else {
                    getMoreDataFromNet();
                }
            }
        });
        return view;
    }

    class MyOnRefreshListener implements RefreshListView.OnRefreshListener {

        @Override
        public void onPullDownRefresh() {
            getDataFromNet();
            Toast.makeText(context, "下拉刷新被回调了", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onLoadMore() {
            if (TextUtils.isEmpty(moreUrl)) {
                //没有更多数据
                Toast.makeText(context, "没有更多数据..", Toast.LENGTH_SHORT).show();
                mPullRefreshListView.onRefreshComplete();
            } else {
                getMoreDataFromNet();
            }
        }
    }

    private void getMoreDataFromNet() {
        LogUtil.e("url地址---》"+url);
        RequestParams params = new RequestParams(moreUrl);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("加载更多联网成功---》" + result);
//                listView.onRefreshFinish(false);
                mPullRefreshListView.onRefreshComplete();
                //把这个放在前面
                isLoadMore = true;
                //解析数据
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("加载更多联网失败onError--》" + ex.getMessage());
//                listView.onRefreshFinish(false);
                mPullRefreshListView.onRefreshComplete();//恢复成出事状态
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("加载更多联网失败onCancelled--》" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("加载更多联网 onFinished()");
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        url = Constants.BASE_URL + childrenData.getUrl();
        String savedJson = CacheUtils.getString(context, url);
        if (!TextUtils.isEmpty(savedJson)) {
            //解析数据 和 处理显示数据
            processData(savedJson);
        }
        LogUtil.e(childrenData.getTitle() + "的联网地址---" + url);
        getDataFromNet();
    }

    private int prePosition;

    private void processData(String json) {
        TabDetailPagerBean bean = parsedJson(json);
        LogUtil.e(childrenData.getTitle() + "解析成功=====" + bean.getData().getNews().get(0).getTitle());

        moreUrl = "";
        if (TextUtils.isEmpty(bean.getData().getMore())) {
            moreUrl = "";
        } else {
            moreUrl = Constants.BASE_URL + bean.getData().getMore();        }
        LogUtil.e("加载更多的地址--》"+moreUrl);
        //默认和加载更多
        if (!isLoadMore) {
            //顶部轮播图数据
            topnews = bean.getData().getTopnews();
            //设置viewpager的适配器
            viewPager.setAdapter(new MyTabDetailPagerTopNewsAdapter());
            //添加红点
            addPoint();

            //监听页面的改变，设置红点变化和文本变化
            viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
            //默认选择第0个
            tv_title.setText(topnews.get(prePosition).getTitle());

            //准备ListView对应的集合数据
            news = bean.getData().getNews();
            //设置ListView的适配器
            listView.setAdapter(new MyTabDetailPager());
        } else {
            //加载更多
            isLoadMore = false;
            List<TabDetailPagerBean.DataEntity.NewsData> moreNews = bean.getData().getNews();
            //添加到原来的集合中
            news.addAll(moreNews);
            //刷新适配器
            new MyTabDetailPager().notifyDataSetChanged();
        }


    }

    class MyTabDetailPager extends BaseAdapter {

        @Override
        public int getCount() {
            return news.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_tabdetail_pager, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_time = convertView.findViewById(R.id.tv_time);
                viewHolder.tv_title = convertView.findViewById(R.id.tv_title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //根据位置得到数据
            TabDetailPagerBean.DataEntity.NewsData newsData = news.get(position);
            String imageUrl = Constants.BASE_URL + newsData.getListimage();
            //请求图片
            x.image().bind(viewHolder.iv_icon, imageUrl, imageOptions);
          /*  Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.news_pic_default)
                    .error(R.drawable.news_pic_default)
                    .into(viewHolder.iv_icon);*/
            //设置标题
            viewHolder.tv_title.setText(newsData.getTitle());
            //设置更新时间
            viewHolder.tv_time.setText(newsData.getPubdate());

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_time;
    }

    private void addPoint() {
        ll_point_group.removeAllViews();
        for (int i = 0; i < topnews.size(); i++) {
            ImageView imageView = new ImageView(context);
            //设置北京选择器
            imageView.setBackgroundResource(R.drawable.point_selector);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(5), DensityUtil.dip2px(5));

            if (i == 0) {
                imageView.setEnabled(true);
            } else {
                imageView.setEnabled(false);
                params.leftMargin = DensityUtil.dip2px(8);
            }
            imageView.setLayoutParams(params);
            ll_point_group.addView(imageView);
        }
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //1.设置文本
            tv_title.setText(topnews.get(position).getTitle());
            //2.对应页面的点高亮-红色
            //把之前的变成灰色
            ll_point_group.getChildAt(prePosition).setEnabled(false);
            //把之前的变成红色
            ll_point_group.getChildAt(position).setEnabled(true);

            prePosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    class MyTabDetailPagerTopNewsAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return topnews.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return object == view;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = new ImageView(context);
            //设置图片默认北京
            imageView.setBackgroundResource(R.drawable.home_scroll_default);
            //x轴 和 y轴 拉伸
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            //把图片添加到容器中
            container.addView(imageView);

            TabDetailPagerBean.DataEntity.TopnewsData topNewsData = topnews.get(position);
            //图片请求地址
            String imageUrl = Constants.BASE_URL + topNewsData.getTopimage();
            //联网请求图片
            x.image().bind(imageView, imageUrl/*,imageOptions*/);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            //super.destroyItem(container, position, object);
            container.removeView((View) object);
        }
    }

    private TabDetailPagerBean parsedJson(String json) {
        return new Gson().fromJson(json, TabDetailPagerBean.class);
    }

    private void getDataFromNet() {
        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //缓存数据
                CacheUtils.putString(context, url, result);
                LogUtil.e(childrenData.getTitle() + "--页面请求成功--" + result);
                //解析和处理显示数据
                processData(result);

                //隐藏下拉刷新控件 -- 更新时间
                mPullRefreshListView.onRefreshComplete();
            }


            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(childrenData.getTitle() + "--页面数据请求失败--" + ex.getMessage());
                //隐藏下来刷新控件 -- 不更新时间，只是隐藏
                mPullRefreshListView.onRefreshComplete();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e(childrenData.getTitle() + "--页面数据请求onCancelled--" + cex.getMessage());

            }

            @Override
            public void onFinished() {
                LogUtil.e(childrenData.getTitle() + "onFinished");
            }
        });
    }
}
