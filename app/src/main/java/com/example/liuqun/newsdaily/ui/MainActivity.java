package com.example.liuqun.newsdaily.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.liuqun.newsdaily.R;
import com.example.liuqun.newsdaily.ui.base.MyBaseActivity;
import com.example.liuqun.newsdaily.view.slidingmenu.SlidingMenu;

/**
 * 主界面
 */
public class MainActivity extends MyBaseActivity {

    private Fragment leftFragment,rightFragment;
    public static SlidingMenu      slidingMenu;
    private ImageView iv_set;
    private ImageView iv_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_set = (ImageView) findViewById(R.id.iv_set);
        iv_user = (ImageView) findViewById(R.id.iv_user);
        iv_set.setOnClickListener(onClickListener);
        iv_user.setOnClickListener(onClickListener);
        initSlidingMenu();
    }

    private View.OnClickListener onClickListener =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_set:
                    if (slidingMenu != null &&slidingMenu.isMenuShowing()) {
                        slidingMenu.showContent();
                    }else if (slidingMenu != null) {
                        slidingMenu.showMenu();
                    }
                    break;
                case R.id.iv_user:
                    if (slidingMenu != null && slidingMenu.isMenuShowing()) {
                        slidingMenu.showContent();
                    }else if (slidingMenu != null) {
                        slidingMenu.showSecondaryMenu();
                    }
                    break;
            }
        }
    };

    /**
     * 初始化侧滑菜单
     */
    private void initSlidingMenu() {
        leftFragment = new MenuLeftFragment();
        rightFragment =new MenuRightFragment();

        slidingMenu =new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.attachToActivity(this,SlidingMenu.SLIDING_CONTENT);

//        slidingMenu.setBehindOffset(100);

        slidingMenu.setMenu(R.layout.layout_menu);
        slidingMenu.setSecondaryMenu(R.layout.layout_menu_right);

        getSupportFragmentManager().beginTransaction().replace(R.id
                .layout_menu, leftFragment).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id
                .layout_menu_right,rightFragment).commit();

    }

    @Override
    public void onBackPressed() {
        if (slidingMenu.isMenuShowing()){
            slidingMenu.showContent();
        }else {
            exitTwice();
        }
    }

    //两次退出
    private boolean isFirstExit =true;
    private void exitTwice() {
        if (isFirstExit){
            Toast.makeText(MainActivity.this, "再按一次退出!", Toast.LENGTH_SHORT).show();
            isFirstExit =false;
            new Thread(){
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        isFirstExit =true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }else {
            finish();
        }
    }
}
