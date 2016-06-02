package com.example.liuqun.newsdaily.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.liuqun.newsdaily.R;
import com.example.liuqun.newsdaily.ui.base.MyBaseActivity;

import java.util.ArrayList;

public class LeadActivity extends MyBaseActivity implements ViewPager
        .OnPageChangeListener {

    private ViewPager       pager;
    private ArrayList<View> list;

    int[] pics = {R.drawable.welcome, R.drawable.small, R.drawable.bd, R.drawable
            .wy};
    //四个点
    private ImageView[] points = new ImageView[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences preferences =getSharedPreferences("runconfig",
                MODE_PRIVATE);
        boolean isFirst =preferences.getBoolean("isFirstRun",true);

        if (!isFirst){
            Intent intent =new Intent(LeadActivity.this,SplashActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        //初始化4个点
        initPoints();
        //初始化ViewPager
        initView();
    }

    private void initPoints() {
        points[0] = (ImageView) findViewById(R.id.iv_p1);
        points[1] = (ImageView) findViewById(R.id.iv_p2);
        points[2] = (ImageView) findViewById(R.id.iv_p3);
        points[3] = (ImageView) findViewById(R.id.iv_p4);
        setPoint(0);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setPoint(int index) {
        for (int i = 0; i < points.length; i++) {
            if (i == index) {
                points[i].setImageAlpha(255);
            } else {
                points[i].setImageAlpha(100);
            }
        }
    }

    private void initView() {
        list = new ArrayList<View>();

        pager = (ViewPager) findViewById(R.id.vp_guide);

        for (int i = 0; i < pics.length; i++) {
            ImageView iv = new ImageView(this);
            //填充整个屏幕
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setImageResource(pics[i]);
            list.add(iv);
        }
        pager.setAdapter(new MyPagerAdapter(list));
        pager.setOnPageChangeListener(this);

        //为ViewPager跳转设置动画
        pager.setPageTransformer(true,new ZoomOutPageTransformer());
//        pager.setPageTransformer(true,new DepthPageTransformer());

    }
    //缩放PageTransformer(官方给的动画1)
    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();
            if (position < -1) { // [-∞,-1)
// 这一页已经是最左边的屏幕页
                view.setAlpha(0);
            } else if (position <= 1) { // [-1,1]
// 修改默认的滑动过渡效果为缩放效果
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }
// 开始根据缩放系数进行变化 (在 MIN_SCALE 和 1 之间变化)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
// 根据大小（缩放系数）变化化透明度实现淡化页面效果
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            } else { // (1,+∞ ]
// 这一页已经是最右边的屏幕页
                view.setAlpha(0);
            }
        }
    }
    //潜藏型PageTransformer（页面转换动画）(官方文档动画2)
    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            if (position < -1) { // [-∞ ,-1)
// 这一页已经是最左边的屏幕页
                view.setAlpha(0);
            } else if (position <= 0) { // [-1,0]
// 向左面滑屏使用默认的过渡动画
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);
            } else if (position <= 1) { // (0,1]
// 淡出页面
                view.setAlpha(1 - position);
// 抵消默认的整页过渡
                view.setTranslationX(pageWidth * -position);
// 根据缩放系数变化 (在 MIN_SCALE 和 1 之间变化)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            } else { // (1,+∞]
// 这一页已经是最右边的屏幕页
                view.setAlpha(0);
            }
        }
    }

    //界面切换时调用
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    //界面切换后调用
    @Override
    public void onPageSelected(int position) {
        //实现4个点和图片的联动
        setPoint(position);

        if (position >= 3) {
            Intent intent = new Intent(LeadActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();

            SharedPreferences preferences =getSharedPreferences("runconfig",
                    MODE_PRIVATE);
            SharedPreferences.Editor editor =preferences.edit();
            editor.putBoolean("isFirstRun",false);
            editor.apply();
        }

    }

    //滑动状态变化时调用
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //自定义MyPagerAdapter继承PagerAdapter
    private class MyPagerAdapter extends PagerAdapter {

        private ArrayList<View> list;

        public MyPagerAdapter(ArrayList<View> list) {
            this.list = list;
        }

        //初始化position 展现到界面上来
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position), 0);
            return list.get(position);
        }

        //当不可见时,销毁position
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }

        @Override
        public int getCount() {
            if (list != null) {
                return list.size();
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
