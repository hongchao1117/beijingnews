package com.hongchao.pager;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hongchao.activity.MainActivity;
import com.hongchao.base.BasePager;
import com.hongchao.base.MenuDetailBasePager;
import com.hongchao.domain.NewsCenterPagerBean2;
import com.hongchao.fragment.LeftMenuFragment;
import com.hongchao.menudetailpager.InteractionMenuDetailPager;
import com.hongchao.menudetailpager.NewsMenuDetailPager;
import com.hongchao.menudetailpager.PhotoMenuDetailPager;
import com.hongchao.menudetailpager.TopicMenuDetailPager;
import com.hongchao.utils.CacheUtils;
import com.hongchao.utils.Constants;
import com.hongchao.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 作用：新闻中心
 */
public class NewsCenterPager extends BasePager {
    public NewsCenterPager(Context context) {
        super(context);
    }

    private List<NewsCenterPagerBean2.DetailPagerData> data;
    //详情页面的集合
    private ArrayList<MenuDetailBasePager> detailBasePagers;

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("新闻中心页面数据被初始化了...");
        ib_menu.setVisibility(View.VISIBLE);
        //1、设置标题
        tv_title.setText("新闻中心");
        //2、联网请求，得到数据，创建视图
        TextView textView = new TextView(context);
        textView.setText("新闻中心内容");
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        textView.setTextSize(25);
        //3、把子视图添加到BasePager的frameLayout
        fl_content.addView(textView);
        //4、绑定数据
        textView.setText("新闻中心内容");

        //缓存数据
        String saveJson = CacheUtils.getString(context,Constants.NEWS_CENTER_PAGER_URL);//""
        if (!TextUtils.isEmpty(saveJson)){
            processData(saveJson);
        }

        //联网请求数据
        getDataFormNet();


    }

    /**
     * 使用 xUtils3 联网请求数据
     */
    private void getDataFormNet() {
        RequestParams params = new RequestParams(Constants.NEWS_CENTER_PAGER_URL);
        params.setConnectTimeout(5000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                LogUtil.e("使用 xUtil3 联网成功-->" + result);
                //缓存数据
                CacheUtils.putString(context,Constants.NEWS_CENTER_PAGER_URL,result);

                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("使用 xUtil3 联网失败-->" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("使用 xUtil3 onCancelled -->" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("使用 xUtil3 --> onFinished");
            }
        });
    }

    /**
     * 解析json数据和显示数据
     *
     * @param json
     */
    private void processData(String json) {
//        NewsCenterPagerBean bean = parsedJson(json);
        NewsCenterPagerBean2 bean2 = parsedJson2(json);
//        String title = bean.getData().get(0).getChildren().get(1).getTitle();
//        LogUtil.e("使用Gson解析json数据成功-title====" + title);

        String title2 = bean2.getData().get(0).getChildren().get(1).getTitle();
        LogUtil.e("使用Gson解析json数据成功-title2====" + title2);

        //把左侧菜单传递数据
        data = bean2.getData();

        MainActivity mainActivity = (MainActivity) context;
        //得到左侧菜单
        LeftMenuFragment leftMenuFragment = mainActivity.getLeftMenuFragment();

        //添加详情页面
        detailBasePagers = new ArrayList<>();
        detailBasePagers.add(new NewsMenuDetailPager(context,data.get(0)));//新闻详情页面
        detailBasePagers.add(new TopicMenuDetailPager(context,data.get(0)));//专题详情页面
        detailBasePagers.add(new PhotoMenuDetailPager(context));//图组详情页面
        detailBasePagers.add(new InteractionMenuDetailPager(context));//互动详情页面

        //把数据传递给左侧菜单
        leftMenuFragment.setData(data);
    }

    /**
     * 使用Android系统自带的API解析json数据
     *
     * @param json
     * @return
     */
    private NewsCenterPagerBean2 parsedJson2(String json) {
        NewsCenterPagerBean2 bean2 = new NewsCenterPagerBean2();
        try {
            JSONObject object = new JSONObject(json);


            int retcode = object.optInt("retcode");
            bean2.setRetcode(retcode);//retcode字段解析成功

            JSONArray data = object.optJSONArray("data");
            if (data != null && data.length() > 0) {

                List<NewsCenterPagerBean2.DetailPagerData> detailPagerDatas = new ArrayList<>();
                //设置列表数据
                bean2.setData(detailPagerDatas);
                //for循环，解析每条数据
                for (int i = 0; i < data.length(); i++) {

                    JSONObject jsonObject = (JSONObject) data.get(i);

                    NewsCenterPagerBean2.DetailPagerData detailPagerData = new NewsCenterPagerBean2.DetailPagerData();
                    //添加到集合中
                    detailPagerDatas.add(detailPagerData);

                    int id = jsonObject.optInt("id");
                    detailPagerData.setId(id);
                    int type = jsonObject.optInt("type");
                    detailPagerData.setType(type);
                    String title = jsonObject.optString("title");
                    detailPagerData.setTitle(title);
                    String url = jsonObject.optString("url");
                    detailPagerData.setUrl(url);
                    String url1 = jsonObject.optString("url1");
                    detailPagerData.setUrl1(url1);
                    String dayurl = jsonObject.optString("dayurl");
                    detailPagerData.setDayurl(dayurl);
                    String excurl = jsonObject.optString("excurl");
                    detailPagerData.setExcurl(excurl);
                    String weekurl = jsonObject.optString("weekurl");
                    detailPagerData.setWeekurl(weekurl);


                    JSONArray children = jsonObject.optJSONArray("children");
                    if (children != null && children.length() > 0) {

                        List<NewsCenterPagerBean2.DetailPagerData.ChildrenData> childrenDatas  = new ArrayList<>();

                        //设置集合-ChildrenData
                        detailPagerData.setChildren(childrenDatas);

                        for (int j = 0; j < children.length(); j++) {
                            JSONObject childrenitem = (JSONObject) children.get(j);

                            NewsCenterPagerBean2.DetailPagerData.ChildrenData childrenData = new NewsCenterPagerBean2.DetailPagerData.ChildrenData();
                            //添加到集合中
                            childrenDatas.add(childrenData);


                            int childId = childrenitem.optInt("id");
                            childrenData.setId(childId);
                            String childTitle = childrenitem.optString("title");
                            childrenData.setTitle(childTitle);
                            String childUrl = childrenitem.optString("url");
                            childrenData.setUrl(childUrl);
                            int childType = childrenitem.optInt("type");
                            childrenData.setType(childType);

                        }

                    }


                }


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return bean2;
    }

    /**
     * 解析json数据：1、使用系统的API解析json；2、使用第三方框架解析json数据
     * 例如Gson,fastJson
     *
     * @param json
     * @return
     */
    private NewsCenterPagerBean2 parsedJson(String json) {
        Gson gson = new Gson();
        NewsCenterPagerBean2 bean = gson.fromJson(json, NewsCenterPagerBean2.class);
        return bean;
    }

    //根据位置切换详情页面
    public void switchPager(int position) {
        //1、设置标题
        tv_title.setText(data.get(position).getTitle());
        //2、移除之前内容(视图）
        fl_content.removeAllViews();
        //3、添加新内容
        MenuDetailBasePager detailBasePager = detailBasePagers.get(position);
        View rootView = detailBasePager.rootView;
        detailBasePager.initData();//初始化数据
        fl_content.addView(rootView);
    }
}
