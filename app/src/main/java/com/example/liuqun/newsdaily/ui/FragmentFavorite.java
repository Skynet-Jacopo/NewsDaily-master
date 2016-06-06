package com.example.liuqun.newsdaily.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.liuqun.newsdaily.R;
import com.example.liuqun.newsdaily.model.db.NewsDBManager;
import com.example.liuqun.newsdaily.model.entity.News;
import com.example.liuqun.newsdaily.ui.adapter.NewsAdapter;

import java.util.ArrayList;

/**
 * 收藏界面
 */
public class FragmentFavorite extends Fragment {
    private View        view;
    private ListView    listView;
    private NewsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_favorite, container, false);
        listView = (ListView) view.findViewById(R.id.listview);
        adapter = new NewsAdapter(getActivity(), listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(itemListener);

        //加载数据库
        loadLoveNews();
        return view;
    }

    /**
     * 从数据库中加载保存的新闻
     */
    private void loadLoveNews() {
        ArrayList<News> data =new NewsDBManager(getActivity()).queryLoveNews();
        adapter.appendData(data,true);
    }

    private AdapterView.OnItemClickListener itemListener =new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //打开显示当前选中的新闻
            News news = (News) parent.getItemAtPosition(position);
            Intent intent =new Intent(getActivity(),ActivityShow.class);
            intent.putExtra("newsitem",news);
            (getActivity()).startActivity(intent);
        }
    };
}
