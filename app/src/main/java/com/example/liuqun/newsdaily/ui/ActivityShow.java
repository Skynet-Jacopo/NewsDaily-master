package com.example.liuqun.newsdaily.ui;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;

import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.liuqun.newsdaily.R;
import com.example.liuqun.newsdaily.common.CommonUtil;
import com.example.liuqun.newsdaily.common.LogUtil;
import com.example.liuqun.newsdaily.common.SystemUtils;
import com.example.liuqun.newsdaily.model.biz.CommentsManager;
import com.example.liuqun.newsdaily.model.biz.parser.ParserComments;
import com.example.liuqun.newsdaily.model.db.NewsDBManager;
import com.example.liuqun.newsdaily.model.entity.News;
import com.example.liuqun.newsdaily.ui.base.MyBaseActivity;

/**
 * 新闻的具体界面
 */
public class ActivityShow extends MyBaseActivity {

    private WebView     webView;
    private ProgressBar progressBar;
    private TextView    tv_commentCount;
    private ImageView   imageViewBack;
    private ImageView   imageViewMenu;
    private News        newsitem;

    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!SystemUtils.getInstance(this).isNetConn()) {
            setContentView(R.layout.oh_no);
        } else {
            setContentView(R.layout.activity_show);
            tv_commentCount = (TextView) findViewById(R.id.textView2);
            progressBar = (ProgressBar) findViewById(R.id.progressBar1);
            webView = (WebView) findViewById(R.id.webView1);
            imageViewBack = (ImageView) findViewById(R.id.imageView_back);
            imageViewMenu = (ImageView) findViewById(R.id.imageView_menu);
            newsitem = (News) getIntent().getSerializableExtra("newsitem");
            //Serializable 可串行化的

            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            WebChromeClient client = new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress >= 100) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            };
            webView.setWebChromeClient(client);
            webView.loadUrl(newsitem.getLink());

            tv_commentCount.setOnClickListener(clickListener);
            imageViewBack.setOnClickListener(clickListener);
            imageViewMenu.setOnClickListener(clickListener);

            //弹出加入收藏窗口
            initPopupWindow();
        }
    }

    private void initPopupWindow() {
        View popView = getLayoutInflater().inflate(R.layout.item_pop_save, null);
        popupWindow = new PopupWindow(popView, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        TextView tv_saveLocal = (TextView) popView.findViewById(R.id.saveLocal);
        tv_saveLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                NewsDBManager manager = new NewsDBManager(ActivityShow.this);
                if (manager.saveLoveNews(newsitem)) {
                    showToast("收藏成功!\n在主界面侧滑菜单中查看");
                } else {
                    showToast("已经收藏过这条新闻了!\n在主界面侧滑菜单中查看");
                }
            }
        });
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imageView_back:
                    finish();
                    break;
                case R.id.textView2:
                    Bundle bundle = new Bundle();
                    bundle.putInt("nid", newsitem.getNid());
                    openActivity(ActivityComment.class, bundle);
                    break;
                case R.id.imageView_menu:
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    } else if (popupWindow != null) {
                        popupWindow.showAsDropDown(imageViewMenu, 0, 12);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //请求评论数量
        CommentsManager.commentNum(this, CommonUtil.VERSION_CODE, newsitem
                .getNid(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int num = ParserComments.parserCommentNum(response.trim());
                LogUtil.d(LogUtil.TAG, "评论数量------" + num + "");

                tv_commentCount.setText(num + " 跟帖");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ActivityShow.this, "网络异常", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
