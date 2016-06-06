package com.example.liuqun.newsdaily.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liuqun.newsdaily.R;
import com.example.liuqun.newsdaily.ui.base.MyBaseActivity;
import com.example.liuqun.newsdaily.view.slidingmenu.SlidingMenu;

/**
 * 主界面
 */
public class MainActivity extends MyBaseActivity {

    private Fragment leftFragment, rightFragment;
    private Fragment fragmentMain, fragmentType, fragmentLogin,
            fragmentRegister, fragmentForgetPass,fragmentFavorite;
    public static SlidingMenu slidingMenu;
    private       ImageView   iv_set;
    private       ImageView   iv_user;
    private       TextView    textView_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView_title = (TextView) findViewById(R.id.tv_title);
        iv_set = (ImageView) findViewById(R.id.iv_set);
        iv_user = (ImageView) findViewById(R.id.iv_user);
        iv_set.setOnClickListener(onClickListener);
        iv_user.setOnClickListener(onClickListener);
        initSlidingMenu();
        showFragmentMain();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_set:
                    if (slidingMenu != null && slidingMenu.isMenuShowing()) {
                        slidingMenu.showContent();
                    } else if (slidingMenu != null) {
                        slidingMenu.showMenu();
                    }
                    break;
                case R.id.iv_user:
                    if (slidingMenu != null && slidingMenu.isMenuShowing()) {
                        slidingMenu.showContent();
                    } else if (slidingMenu != null) {
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
        rightFragment = new MenuRightFragment();

        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);

//        slidingMenu.setBehindOffset(100);

        slidingMenu.setMenu(R.layout.layout_menu);
        slidingMenu.setSecondaryMenu(R.layout.layout_menu_right);

        getSupportFragmentManager().beginTransaction().replace(R.id
                .layout_menu, leftFragment).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id
                .layout_menu_right, rightFragment).commit();

    }

    @Override
    public void onBackPressed() {
        if (slidingMenu.isMenuShowing()) {
            slidingMenu.showContent();
        } else {
            exitTwice();
        }
    }

    //两次退出
    private boolean isFirstExit = true;

    private void exitTwice() {
        if (isFirstExit) {
            Toast.makeText(MainActivity.this, "再按一次退出!", Toast.LENGTH_SHORT).show();
            isFirstExit = false;
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        isFirstExit = true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            finish();
        }
    }

    /**
     * 显示：“显示新闻更多分类Fragment”
     */
    public void showFragmentType() {
        setTitle("分类");
        slidingMenu.showContent();
        if (fragmentType == null)
            fragmentType = new FragmentType();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_content, fragmentType).commit();
    }

    /**
     * 显示:"显示新闻列表的Fragment"
     */
    public void showFragmentMain() {
        setTitle("资讯");
        slidingMenu.showContent();
        if (fragmentMain == null) {
            fragmentMain = new FragmentMain();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id
                .layout_content, fragmentMain).commit();
    }

    /**
     * 显示:"登录的Fragment"
     */
    public void showFragmentLogin() {
        setTitle("用户登录");
        slidingMenu.showContent();
        if (fragmentLogin == null) {
            fragmentLogin = new FragmentLogin();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id
                .layout_content, fragmentLogin).commit();
    }

    /**
     * 显示 : "注册的Fragment"
     */
    public void showFragmentRegister() {
        setTitle("用户注册");
        if (fragmentRegister == null) {
            fragmentRegister = new FragmentRegister();
            getSupportFragmentManager().beginTransaction().replace(R.id
                    .layout_content, fragmentRegister).commit();
        }
    }

    /**
     * 显示:"忘记密码的Fragment"
     */
    public void showFragmentForgetPass() {
        setTitle("忘记密码");
        if (fragmentForgetPass == null) {
            fragmentForgetPass = new FragmentForgetPass();
            getSupportFragmentManager().beginTransaction().replace(R.id
                    .layout_content, fragmentForgetPass).commit();
        }
    }

    public void showFragmentFavorite(){
        setTitle("收藏新闻");
        slidingMenu.showContent();
        if (fragmentFavorite == null) {
            fragmentFavorite =new FragmentFavorite();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id
                .layout_content,fragmentFavorite).commit();

    }
    /**
     * 右侧是否登录的切换
     */
    public void changeFragmentUser() {
        ((MenuRightFragment) rightFragment).changView();
    }

    /**
     * 更换当前界面的Title
     *
     * @param title
     */
    private void setTitle(String title) {
        textView_title.setText(title);
    }
}
