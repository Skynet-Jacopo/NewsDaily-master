package com.example.liuqun.newsdaily.ui;

import android.content.Intent;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.liuqun.newsdaily.R;
import com.example.liuqun.newsdaily.ui.base.MyBaseActivity;

public class SplashActivity extends MyBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        ImageView ivSplash = (ImageView) findViewById(R.id.iv_splash);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.logo);

        animation.setFillAfter(true);

        animation.setAnimationListener(new Animation.AnimationListener() {
            //动画启动时调用
            @Override
            public void onAnimationStart(Animation animation) {

            }
            //动画结束时调用
            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent =new Intent(SplashActivity.this,MainActivity
                        .class);
                startActivity(intent);
                finish();
            }
            //动画重复时调用
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        assert ivSplash != null;
        ivSplash.setAnimation(animation);
    }
}
