package com.hongchao.fragment;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hongchao.R;
import com.hongchao.activity.MainActivity;
import com.hongchao.base.BaseFragment;
import com.hongchao.domain.NewsCenterPagerBean2;
import com.hongchao.pager.NewsCenterPager;
import com.hongchao.utils.DensityUtil;
import com.hongchao.utils.LogUtil;

import java.util.List;


public class LeftMenuFragment extends BaseFragment {
    private List<NewsCenterPagerBean2.DetailPagerData> data;
    private ListView listView;
    private LeftMenuFragmentAdapter adapter;
    private int prePosition;//点击的位置

    @Override
    public View initView() {
        LogUtil.e("左侧菜单视图被初始化了");
        listView = new ListView(context);
        listView.setPadding(0, DensityUtil.dip2px(context, 40), 0, 0);
        listView.setDividerHeight(0);//设置分割线高度为0
        listView.setCacheColorHint(Color.TRANSPARENT);
        //设置按下listView的item不变色
        listView.setSelector(android.R.color.transparent);

        //设置item的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //1、记录点击的位置，变成红色
                prePosition = position;
                adapter.notifyDataSetChanged();//getcount() --> getview()
                //2、把左侧菜单关闭
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.getSlidingMenu().toggle();//关<-->开
                //3、切换到对应的详情页面：新闻详情页面，专题详情页面，图组详情页面，互动详情页面
                switchPager(prePosition);

            }
        });
        return listView;
    }

    //根据位置不同切换详情页面
    private void switchPager(int position) {
        MainActivity mainActivity = (MainActivity) context;
        ContentFragment contentFragment = mainActivity.getContentFragment();
        NewsCenterPager newsCenterPager = contentFragment.getNewsCenterPager();
        newsCenterPager.switchPager(position);
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("左侧菜单数据被初始化了");
    }

    /**
     * 接收数据
     *
     * @param data
     */
    public void setData(List<NewsCenterPagerBean2.DetailPagerData> data) {
        this.data = data;
        for (int i = 0; i < data.size(); i++) {
            LogUtil.e("title -->" + data.get(i).getTitle());
        }

        //设置适配器
        adapter = new LeftMenuFragmentAdapter();
        listView.setAdapter(adapter);
        //设置默认页面
        switchPager(prePosition);

    }

    class LeftMenuFragmentAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data.size();
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
            TextView textView = (TextView) View.inflate(context, R.layout.item_left_menu, null);
            textView.setText(data.get(position).getTitle());
            if (position == prePosition) {
                //设置为红色
                textView.setEnabled(true);
            } else {
                textView.setEnabled(false);
            }
//            textView.setEnabled(position == prePosition);
            return textView;
        }
    }
}
