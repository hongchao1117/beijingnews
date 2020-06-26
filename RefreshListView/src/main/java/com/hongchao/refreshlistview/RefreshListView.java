package com.hongchao.refreshlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作用：自定义下拉刷新的listview
 */
public class RefreshListView extends ListView {
    //下拉刷新和顶部轮播图（先不加入）
    private LinearLayout headerView;

    //下拉刷新控件
    private View ll_pull_down_refresh;
    private ImageView iv_arrow;
    private ProgressBar pb_status;
    private TextView tv_status;
    private TextView tv_time;
    private int pullDownRefreshHeight;//下拉刷新控件的高
    //下拉刷新
    public static final int PULL_DOWN_REFRESH = 0;
    //手松刷新
    public static final int RELEASE_REFRESH = 1;
    //正在刷新
    public static final int REFRESHING = 2;
    //当前的状态
    private int currentstatus = PULL_DOWN_REFRESH;

    private Animation upAnimation;
    private Animation downAnimation;
    private View footView;//加载更多的控件
    private int footerViewHeight;//加载更多控件高
    private boolean isLoadMore;//是否已经加载更多
    private View topNewsView;
    private int ListViewOnScreenY = -1;


    public RefreshListView(Context context) {
        this(context, null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderView(context);
        initAnimation();
        initFooterView(context);
    }

    private void initFooterView(Context context) {
        footView = View.inflate(context, R.layout.refresh_footer, null);
        footView.measure(0, 0);
        footerViewHeight = footView.getMeasuredHeight();
        footView.setPadding(0, -footerViewHeight, 0, 0);
        //listview添加footer
        addFooterView(footView);

        //监听listview的滚动
        setOnScrollListener(new MyOnScrollListener());

    }

    /**
     * 添加顶部轮播图
     *
     * @param topNewsView
     */
    public void addTopNewsView(View topNewsView) {
        if (topNewsView != null) {
            this.topNewsView = topNewsView;
            headerView.addView(topNewsView);
        }
    }

    class MyOnScrollListener implements OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            //当静止或者惯性滚动的时候
            if (scrollState == OnScrollListener.SCROLL_STATE_IDLE ||
                    scrollState == OnScrollListener.SCROLL_STATE_FLING) {
                //并且是 最后一条 可见
                if (getLastVisiblePosition() >= getCount() - 1) {
                    //显示加载更多布局
                    footView.setPadding(8, 8, 8, 8);
                    //状态改变
                    isLoadMore = true;
                    //回调接口
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.onLoadMore();
                    }
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    }


    private void initAnimation() {
        upAnimation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        upAnimation.setDuration(500);
        upAnimation.setFillAfter(true);

        downAnimation = new RotateAnimation(-180, -360,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        downAnimation.setDuration(500);
        downAnimation.setFillAfter(true);

    }

    private void initHeaderView(Context context) {
        headerView = (LinearLayout) View.inflate(context, R.layout.refresh_header, null);
        //下拉刷新控件
        ll_pull_down_refresh = headerView.findViewById(R.id.ll_pull_down_refresh);
        iv_arrow = headerView.findViewById(R.id.iv_arrow);
        pb_status = headerView.findViewById(R.id.pb_status);
        tv_status = headerView.findViewById(R.id.tv_status);
        tv_time = headerView.findViewById(R.id.tv_time);
        //测量
        ll_pull_down_refresh.measure(0, 0);
        pullDownRefreshHeight = ll_pull_down_refresh.getMeasuredHeight();
        //默认隐藏下拉刷新控件
        //view.setPadding(0，-控件高-，0,0)；完全隐藏
        //view.setPadding(0，0，0,0)；完全显示
        ll_pull_down_refresh.setPadding(0, -pullDownRefreshHeight, 0, 0);

        //添加头
        addHeaderView(headerView);

    }

    private float startY = -1;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //1.记录起始坐标
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (startY == -1) {
                    startY = ev.getY();
                }
                //判断顶部轮播图是否完全显示，只有完全显示才会有下拉刷新
                boolean isDisplayTopNews = isDisplayTopNews();
                if (!isDisplayTopNews) {
                    //加载更多
                    break;
                }


                if (currentstatus == REFRESHING) {
                    break;
                }
                //2.来到新的坐标
                float endY = ev.getY();
                //3.记录滑动的距离
                float distanceY = endY - startY;
                if (distanceY > 0) {
                    //下拉
                    int paddingTop = (int) (-pullDownRefreshHeight + distanceY);
                    if (paddingTop < 0 && currentstatus != PULL_DOWN_REFRESH) {
                        //下拉刷新状态
                        currentstatus = PULL_DOWN_REFRESH;
                        //更新状态

                    } else if (paddingTop > 0 && currentstatus != RELEASE_REFRESH) {
                        //手动刷新状态
                        currentstatus = RELEASE_REFRESH;
                        //更新状态
                        refreshViewState();
                    }


                    ll_pull_down_refresh.setPadding(0, paddingTop, 0, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
                startY = -1;
                if (currentstatus == PULL_DOWN_REFRESH) {
                    ll_pull_down_refresh.setPadding(0, -pullDownRefreshHeight, 0, 0);
                } else if (currentstatus == RELEASE_REFRESH) {
                    //设置正在刷新
                    currentstatus = REFRESHING;
                    refreshViewState();
                    ll_pull_down_refresh.setPadding(0, 0, 0, 0);
                    //回调接口
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.onPullDownRefresh();
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 判断是否完全显示顶部轮播图
     * 当listview在屏幕上的Y轴坐标 <= 顶部轮播图在Y轴的坐标的时候，顶部轮播图完全显示
     *
     * @return
     */
    private boolean isDisplayTopNews() {
        if (topNewsView != null) {
            //1.得到listview在屏幕上的坐标
            int[] location = new int[2];
            if (ListViewOnScreenY == -1) {
                getLocationOnScreen(location);
                ListViewOnScreenY = location[1];
            }
            //2.得到顶部轮播图在屏幕上的坐标
            topNewsView.getLocationOnScreen(location);
            int topNewsViewOnScreenY = location[1];
//        if (ListViewOnScreenY<=topNewsViewOnScreenY){
//            return true;
//        }else

            return ListViewOnScreenY <= topNewsViewOnScreenY;
        }else{
            return true;
        }
    }

    private void refreshViewState() {
        switch (currentstatus) {
            case PULL_DOWN_REFRESH://下拉刷新状态
                iv_arrow.startAnimation(downAnimation);
                tv_status.setText("下拉刷新...");
                break;
            case RELEASE_REFRESH://手松刷新状态
                iv_arrow.startAnimation(upAnimation);
                tv_status.setText("手松刷新...");
                break;
            case REFRESHING://正在刷新状态
                pb_status.setVisibility(VISIBLE);
                tv_status.setText("正在刷新...");
                iv_arrow.clearAnimation();
                iv_arrow.setVisibility(GONE);
                break;

        }
    }

    /**
     * 当联网成功和失败的时候回调该方法
     * 用户刷新状态的还原
     *
     * @param b
     */
    public void onRefreshFinish(boolean success) {
        if (isLoadMore) {
            //加载更多
            isLoadMore = false;
            //隐藏加载更多布局
            footView.setPadding(0, -footerViewHeight, 0, 0);
        } else {
            //下拉刷新
            tv_status.setText("下拉刷新...");
            currentstatus = PULL_DOWN_REFRESH;
            iv_arrow.clearAnimation();
            pb_status.setVisibility(GONE);
            iv_arrow.setVisibility(VISIBLE);
            //隐藏下拉刷新控件
            ll_pull_down_refresh.setPadding(0, -pullDownRefreshHeight, 0, 0);
            if (success) {
                //设置最新更新时间
                tv_time.setText("上次更新时间：" + getSystemTime());
            }

        }
    }

    /**
     * 得到当前Android系统的时间
     *
     * @return
     */
    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    /**
     * 监听控件的刷新
     */
    public interface OnRefreshListener {
        //当下拉刷新的时候回调这个方法
        void onPullDownRefresh();

        //当加载更多的时候回调这个方法
        void onLoadMore();

    }

    public OnRefreshListener mOnRefreshListener;

    /**
     * 设置监听刷新，由外界设置
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        this.mOnRefreshListener = listener;
    }


}
