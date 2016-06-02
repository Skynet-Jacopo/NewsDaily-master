package com.example.liuqun.newsdaily.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.liuqun.newsdaily.R;
import com.example.liuqun.newsdaily.common.CommonUtil;
import com.example.liuqun.newsdaily.common.LogUtil;
import com.example.liuqun.newsdaily.common.SystemUtils;
import com.example.liuqun.newsdaily.model.biz.NewsManager;
import com.example.liuqun.newsdaily.model.biz.parser.ParserNews;
import com.example.liuqun.newsdaily.model.db.NewsDBManager;
import com.example.liuqun.newsdaily.model.entity.News;
import com.example.liuqun.newsdaily.model.entity.SubType;
import com.example.liuqun.newsdaily.ui.adapter.NewsAdapter;
import com.example.liuqun.newsdaily.ui.adapter.NewsTypeAdapter;
import com.example.liuqun.newsdaily.view.HorizontalListView;
import com.example.liuqun.newsdaily.view.xlistview.XListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻列表界面
 * Created by 90516 on 6/2/2016.
 */
public class FragmentMain extends Fragment {
    //填充view
    private View               view;
    //新闻列表
    private XListView          listView;
    //分类列表
    private HorizontalListView hl_type;
    //更多分类
    private View               btn_moretype;
    //分类适配器
    private NewsTypeAdapter    typeAdapter;
    //数据库
    private NewsDBManager      dbManager;
    //当前Activity
    private MainActivity       mainActivity;
    //新闻适配器
    private NewsAdapter        newsAdapter;
    //模式  1上拉,2下拉
    private int                mode;
    //新闻分类编号,默认为1
    private int subId = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_newslist, container, false);
        dbManager = new NewsDBManager(getActivity());
        mainActivity = (MainActivity) getActivity();
        listView = (XListView) view.findViewById(R.id.news_list);
        hl_type = (HorizontalListView) view.findViewById(R.id.hl_type);
        btn_moretype = view.findViewById(R.id.iv_moretype);
        btn_moretype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.showFragmentType();
            }
        });

        if (hl_type != null) {
            typeAdapter = new NewsTypeAdapter(getActivity());
            hl_type.setAdapter(typeAdapter);
            hl_type.setOnItemClickListener(typeItemListener);
        }
        //加载新闻分类
        loadNewsType();
        if (listView != null) {
            newsAdapter = new NewsAdapter(getActivity(), listView);
            listView.setAdapter(newsAdapter);
            listView.setPullRefreshEnable(true);
            listView.setPullLoadEnable(true);
            listView.setXListViewListener(listViewListener);
            listView.setOnItemClickListener(newsItemListener);
        }
        //加载新闻列表
        loadNextNews(true);
        mainActivity.showLoadingDialog(mainActivity, "加载中", false);
        return view;
    }

    private XListView.IXListViewListener    listViewListener = new XListView.IXListViewListener() {
        @Override
        public void onRefresh() {
            //加载数据。。。。。。。。。。。。。。。。。。。
            loadNextNews(false);
            // 加载完毕
            listView.stopLoadMore();
            listView.stopRefresh();
            listView.setRefreshTime(CommonUtil.getSystime());
        }

        @Override
        public void onLoadMore() {
            //加载数据。。。。。。。。。。。。。。。。。。。
            loadPreNews();
            listView.stopLoadMore();
            listView.stopRefresh();
        }
    };
    /**
     * 分类单项点击事件
     */
    private AdapterView.OnItemClickListener typeItemListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            SubType subType = (SubType) parent.getItemAtPosition(position);
            subId = subType.getSubid();
            typeAdapter.setSelectedPosition(position);
            typeAdapter.update();
            loadNextNews(true);
            mainActivity.showLoadingDialog(mainActivity, "加载中", false);
        }
    };


    /**
     * 新闻单项点击事件
     */
    private AdapterView.OnItemClickListener newsItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // 打开显示当前选中的新闻
            News   news   = (News) parent.getItemAtPosition(position);
            Intent intent = new Intent(getActivity(), ActivityShow.class);
            intent.putExtra("newsitem", news);
            getActivity().startActivity(intent);
        }
    };

    private void loadNewsType() {
//        RequestQueue mQueue = Volley.newRequestQueue(getActivity());
//        StringRequest stringRequest = new StringRequest(
//                "http://118.244.212.82:9092/newsClient/news_sort?ver=1&imei=1",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        LogUtil.d("onResponse", "response = " + response);
//                        List<SubType> types = ParserNews.parserTypeList(response);
////                        dbManager.saveNewsType(types);
//                        typeAdapter.appendData(types, true);
//                        typeAdapter.update();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getActivity(), "Hello", Toast.LENGTH_SHORT).show();
//                    }
//                });
//        mQueue.add(stringRequest);
        if (dbManager.queryNewsType().size() == 0) {
            if (SystemUtils.getInstance(getActivity()).isNetConn()) {
                System.out.println("loadNewsType");
                NewsManager.loadNewsType(getActivity(),
                        new VolleyTypeResponseHandler(), new VolleyErrorHandler());
            }
        } else {
            List<SubType> types = dbManager.queryNewsType();
            typeAdapter.appendData(types, true);
            typeAdapter.update();
        }

    }


    /**
     * 加载先前的新闻数据
     */
    protected void loadPreNews() {
        if (listView.getCount() - 2 <= 0)
            return;
        int nId = newsAdapter.getItem(listView.getLastVisiblePosition() - 2)
                .getNid();
        mode = NewsManager.MODE_PREVIOUS;
        if (SystemUtils.getInstance(getActivity()).isNetConn()) {
            NewsManager.loadNewsFromServer(getActivity(), mode, subId, nId,
                    new VolleyResponseHandler(), new VolleyErrorHandler());
        } else {
            NewsManager.loadNewsFromsLocal(mode, nId,
                    new MyLocalResponseHandler());
        }
    }

    /**
     * 加载新的数据
     */
    protected void loadNextNews(boolean isNewType) {
        int nId = 1;
        if (!isNewType) {
            if (newsAdapter.getAdapterData().size() > 0) {
                nId = newsAdapter.getItem(0).getNid();
            }
        }
        mode = NewsManager.MODE_NEXT;
        if (SystemUtils.getInstance(getActivity()).isNetConn()) {
            NewsManager.loadNewsFromServer(getActivity(), mode, subId, nId,
                    new VolleyResponseHandler(), new VolleyErrorHandler());
        } else {
            NewsManager.loadNewsFromsLocal(mode, nId,
                    new MyLocalResponseHandler());
        }
    }

    /*
     05-17 23:16:40.084: D/TYPE(10972): TYPE Response = {
     "message":"OK","status":0,"data":[{"subgrp":[{"subgroup":"社会","subid":2},{"subgroup":"军事","subid":1}],"gid":1,"group":"新闻"},{"subgrp":[{"subgroup":"股票","subid":3},{"subgroup":"基金","subid":4}],"gid":2,"group":"财经"},{"subgrp":[{"subgroup":"手机","subid":5},{"subgroup":"探索","subid":6}],"gid":3,"group":"科技"},{"subgrp":[{"subgroup":"英超","subid":7},{"subgroup":"NBA","subid":8}],"gid":4,"group":"体育"}]}

     */
    class VolleyTypeResponseHandler implements Response.Listener<String> {

        @Override
        public void onResponse(String response) {
            // TODO Auto-generated method stub
            LogUtil.d("TYPE", "TYPE Response = " + response);
            List<SubType> types = ParserNews.parserTypeList(response);
            dbManager.saveNewsType(types);
            typeAdapter.appendData(types, true);
            typeAdapter.update();
        }
    }


    /**
     * Volley成功，新闻列表回调接口实现类
     */

    class VolleyResponseHandler implements Response.Listener<String> {

        @Override
        public void onResponse(String response) {
            // TODO Auto-generated method stub
            List<News> data    = ParserNews.parserNewsList(response);
            boolean    isClear = mode == NewsManager.MODE_NEXT ? true : false;
            newsAdapter.appendData((ArrayList<News>) data, isClear);
            mainActivity.cancelDialog();
            newsAdapter.update();
        }
    }

    class VolleyErrorHandler implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            // TODO Auto-generated method stub
            mainActivity.cancelDialog();
            mainActivity.showToast("服务器连接异常");
        }

    }

    public class MyLocalResponseHandler implements
            NewsManager.LocalResponseHandler {
        public void update(ArrayList<News> data, boolean isClearOld) {
            newsAdapter.appendData(data, isClearOld);
            newsAdapter.update();
            if (data.size() <= 0) {
                Toast.makeText(getActivity(), "请先设置网络连接", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
