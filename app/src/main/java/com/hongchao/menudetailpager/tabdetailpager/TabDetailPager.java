package com.hongchao.menudetailpager.tabdetailpager;

import android.content.Context;
import android.text.PrecomputedText;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.hongchao.R;
import com.hongchao.base.MenuDetailBasePager;
import com.hongchao.domain.NewsCenterPagerBean2;
import com.hongchao.domain.TabDetailPagerBean;
import com.hongchao.utils.CacheUtils;
import com.hongchao.utils.Constants;
import com.hongchao.utils.LogUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * 作用：页签详情页面
 */
public class TabDetailPager extends MenuDetailBasePager {
    private final NewsCenterPagerBean2.DetailPagerData.ChildrenData childrenData;
    //    private TextView textView;
    private String url;

    public TabDetailPager(Context context, NewsCenterPagerBean2.DetailPagerData.ChildrenData childrenData) {
        super(context);
        this.childrenData = childrenData;
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.tab_detail_pager, null);
        return view;
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

    private void processData(String json) {
        TabDetailPagerBean bean = parsedJson(json);
        LogUtil.e(childrenData.getTitle()+"解析成功====="+bean.getData().getNews().get(0).getTitle());
    }

    private TabDetailPagerBean parsedJson(String json) {
        return new Gson().fromJson(json, TabDetailPagerBean.class);

    }

    private void getDataFromNet() {
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //缓存数据
                CacheUtils.putString(context, url, result);
                LogUtil.e(childrenData.getTitle() + "--页面请求成功--" + result);
                //解析和处理显示数据
                processData(result);
            }


            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(childrenData.getTitle() + "--页面数据请求失败--" + ex.getMessage());
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
