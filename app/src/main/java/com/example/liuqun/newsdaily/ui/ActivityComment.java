package com.example.liuqun.newsdaily.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.liuqun.newsdaily.R;
import com.example.liuqun.newsdaily.common.CommonUtil;
import com.example.liuqun.newsdaily.common.LogUtil;
import com.example.liuqun.newsdaily.common.SharedPreferencesUtils;
import com.example.liuqun.newsdaily.common.SystemUtils;
import com.example.liuqun.newsdaily.model.biz.CommentsManager;
import com.example.liuqun.newsdaily.model.biz.NewsManager;
import com.example.liuqun.newsdaily.model.biz.parser.ParserComments;
import com.example.liuqun.newsdaily.model.entity.Comment;
import com.example.liuqun.newsdaily.ui.adapter.CommentsAdapter;
import com.example.liuqun.newsdaily.ui.base.MyBaseActivity;
import com.example.liuqun.newsdaily.view.xlistview.XListView;


import java.util.List;

/**
 * 评论界面
 */
public class ActivityComment extends MyBaseActivity {
    //新闻id
    private int             nid;
    //评论列表
    private XListView       listView;
    //评论列表适配器
    private CommentsAdapter adapter;

    private int       mode;
    //发送评论按钮
    private ImageView imageView_send;
    //返回按钮
    private ImageView imageView_back;
    //评论编辑框
    private EditText  editText_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        nid = getIntent().getIntExtra("nid", -1);
        Log.d(LogUtil.TAG, "nid-----------------------> " + nid);
        listView = (XListView) findViewById(R.id.listview);
        imageView_send = (ImageView) findViewById(R.id.imageview);
        imageView_back = (ImageView) findViewById(R.id.imageView_back);
        editText_content = (EditText) findViewById(R.id.edittext_comment);

        adapter = new CommentsAdapter(this, listView);

        listView.setAdapter(adapter);
        listView.setPullRefreshEnable(true);
        listView.setPullLoadEnable(true);
        listView.setXListViewListener(listViewListener);

        loadNextComment();

        imageView_back.setOnClickListener(clickListener);
        imageView_send.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imageView_back:
                    finish();
                    break;
                case R.id.imageview:
                    String content = editText_content.getText().toString();
                    if (content == null || content.equals("")) {
                        Toast.makeText(ActivityComment.this, "请先写评论哦,亲!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    imageView_send.setEnabled(false);
                    String imei = SystemUtils.getInstance(ActivityComment
                            .this).getIMEI();
                    String token = SharedPreferencesUtils.getToken
                            (ActivityComment.this);
                    if (TextUtils.isEmpty(token)) {
                        Toast.makeText(ActivityComment.this, "对不起,您还没登录...",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    showLoadingDialog(ActivityComment.this, "", true);

                    CommentsManager.sendCommnet(ActivityComment.this, nid, new
                            Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    LogUtil.d(LogUtil.TAG,
                                            "发表评论返回信息------>" + response.toString());
                                    int status = ParserComments
                                            .parserSendComment(response.trim());
                                    if (status == 0) {
                                        showToast("评论成功");
                                        editText_content.setText(null);
                                        editText_content.clearFocus();
                                        loadNextComment();
                                    } else {
                                        showToast("评论失败,请检查自己是否登录!");
                                    }
                                    imageView_send.setEnabled(true);
                                    dialog.cancel();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showToast("服务器连接异常!");
                            imageView_send.setEnabled(true);
                            dialog.cancel();
                        }
                    }, CommonUtil.VERSION_CODE + "", token, imei, content);
                    break;
            }
        }
    };


    private XListView.IXListViewListener listViewListener = new XListView.IXListViewListener() {
        @Override
        public void onRefresh() {
            //加载最新数据 ...................
            loadNextComment();
            //加载完毕
            listView.stopLoadMore();
            listView.stopRefresh();
            listView.setRefreshTime(CommonUtil.getSystime());
        }

        @Override
        public void onLoadMore() {
            //加载下面更多的数据
            int count = adapter.getCount();
            if (count > 1) {//如果当前的listView不存在一条item是不允许用户加载更多
                loadPreComment();
            }
            listView.stopLoadMore();
            listView.stopRefresh();
        }
    };

    /**
     * 加载下面的xx条数据
     */
    private void loadPreComment() {
        Comment comment = adapter.getItem(listView.getLastVisiblePosition() - 2);
        mode = NewsManager.MODE_PREVIOUS;
        if (SystemUtils.getInstance(this).isNetConn()) {
            CommentsManager.loadComments(this, CommonUtil.VERSION_CODE + "",
                    listener, errorListener, nid, 2, comment.getCid());

        }
    }

    /**
     * 请求最新的评论
     */
    private void loadNextComment() {
        int curId = adapter.getAdapterData().size() <= 0 ? 0 : adapter.getItem(0)
                .getCid();
        LogUtil.d(LogUtil.TAG, "loadnextcomment ---->currentId=" + curId);
        mode = NewsManager.MODE_NEXT;
        if (SystemUtils.getInstance(this).isNetConn()) {
            CommentsManager.loadComments(this,
                    CommonUtil.VERSION_CODE + "",
                    listener,
                    errorListener,
                    nid,//新闻id
                    1,//方向1  下拉
                    curId);
        }
    }

    Response.Listener<String> listener      = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            List<Comment> comments = ParserComments.parserComment(response);
            if (comments == null || comments.size() < 1) {
                return;
            }
            boolean flag = mode == NewsManager.MODE_NEXT ? true : false;
            adapter.appendData(comments,flag);
            adapter.update();
        }
    };
    Response.ErrorListener    errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(ActivityComment.this, "服务器连接错误", Toast.LENGTH_SHORT).show();
        }
    };
}
