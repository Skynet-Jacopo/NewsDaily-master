package com.example.liuqun.newsdaily.ui;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.liuqun.newsdaily.R;
import com.example.liuqun.newsdaily.common.CommonUtil;
import com.example.liuqun.newsdaily.common.LoadImage;
import com.example.liuqun.newsdaily.common.LogUtil;
import com.example.liuqun.newsdaily.common.SharedPreferencesUtils;
import com.example.liuqun.newsdaily.common.SystemUtils;
import com.example.liuqun.newsdaily.model.biz.UpdateManager;
import com.example.liuqun.newsdaily.model.biz.parser.ParserVersion;
import com.example.liuqun.newsdaily.model.entity.Version;
import com.example.liuqun.newsdaily.receiver.DownloadCompleteReceiver;

import org.w3c.dom.Text;

//import cn.sharesdk.framework.ShareSDK;


public class MenuRightFragment extends Fragment implements LoadImage.ImageLoadListener {
    public static final int WEBCHAT = 1, QQ = 2, WEBCHATMOMENTS = 3, SINA = 4;
    private View                     view;
    private SharedPreferences        sharedPreferences;
    private RelativeLayout           relativeLayout_unlogin;
    private RelativeLayout           relativeLayout_logined;
    private ImageView                iv_unlogin;
    private TextView                 tv_unlogin;
    private TextView                 updateTv;
    private DownloadCompleteReceiver receiver;
    private boolean                  islogin;
    private String[]                 str;
    private ImageView                iv_pic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_menu_right, container,
                false);

        sharedPreferences = getActivity().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        islogin = sharedPreferences.getBoolean("islogin", false);
        relativeLayout_unlogin = (RelativeLayout) view.findViewById(R.id
                .rl_unlogin);
        relativeLayout_logined = (RelativeLayout) view.findViewById(R.id
                .rl_logined);
        iv_unlogin = (ImageView) view.findViewById(R.id.iv_unlogin);
        tv_unlogin = (TextView) view.findViewById(R.id.tv_unlogin);
        updateTv = (TextView) view.findViewById(R.id.update_version);

        //初始化分享功能控件
        ImageView iv_friend  = (ImageView) view.findViewById(R.id.fun_friend);
        ImageView iv_qq      = (ImageView) view.findViewById(R.id.fun_qq);
        ImageView iv_friends = (ImageView) view.findViewById(R.id.fun_friends);
        ImageView iv_weibo   = (ImageView) view.findViewById(R.id.fun_weibo);

        iv_friend.setOnClickListener(l);
        iv_qq.setOnClickListener(l);
        iv_friends.setOnClickListener(l);
        iv_weibo.setOnClickListener(l);

        iv_unlogin.setOnClickListener(l);
        tv_unlogin.setOnClickListener(l);

        relativeLayout_logined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityUser.class);
                startActivity(intent);
            }
        });

        receiver = new DownloadCompleteReceiver();  //创建下载完毕接收器
        //版本更新
        updateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), "等会儿在做", Toast.LENGTH_SHORT)
//                        .show();
                UpdateManager.judgeUpdate(getActivity(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //解析返回json数据
                        Version version = ParserVersion.parserJson(response);
                        //判断本地版本与服务器版本
                        if (CommonUtil.getVersionCode(MenuRightFragment.this
                                .getActivity()) < Integer.parseInt(version
                                .getVersion())){
                            //执行下载请求
                            Toast.makeText(getActivity(), "正在下载最新版本", Toast
                                    .LENGTH_SHORT).show();
                            UpdateManager.downLoad(getActivity(),version.getLink());
                        }else {
                            Toast.makeText(getActivity(), "当前已是最新版本", Toast
                                    .LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "更新失败", Toast
                                .LENGTH_SHORT).show();
                    }
                }, SystemUtils.getIMEI(getActivity()),"package-name",
                        CommonUtil.VERSION_CODE +"");

            }
        });
        return view;
    }

    private View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //判断登录
            if (v.getId() == R.id.iv_unlogin || v.getId() == R.id.tv_unlogin) {
                ((MainActivity) getActivity()).showFragmentLogin();
            }
            // 判断分享
            switch (v.getId()) {
                case R.id.fun_friend://分享到微信
                    showShare(WEBCHAT);
                    break;
                case R.id.fun_qq:
//                    showShare(QQ);
                    //在网上找到的简单的实现方法
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "share");
                    intent.putExtra(Intent.EXTRA_TEXT, "I have successfully share my message through my app");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Intent.createChooser(intent, "分享到:"));
                    break;
                case R.id.fun_friends:
                    showShare(WEBCHATMOMENTS);
                    break;
                case R.id.fun_weibo:
                    showShare(SINA);
                    break;
            }
        }
    };

    /**
     * 全部分享界面显示
     *
     * @param platforms 分享的位置
     */
    private void showShare(int platforms) {
        // TODO: 6/4/2016 在网上找了一个简便的方法实现分享
//        ShareSDK.initSDK(getActivity());
        Toast.makeText(getActivity(), "去点QQ", Toast.LENGTH_SHORT).show();
    }

    /**
     * 初始化用户信息
     */
    private void initUserInfo() {
        TextView tv_name = (TextView) view.findViewById(R.id.textView_name);
        iv_pic = (ImageView) view.findViewById(R.id.imageView_photo);
        tv_name.setText(str[0]);
        String iconPath = SharedPreferencesUtils.getUserLocalIcon(getActivity
                ());
        if (!TextUtils.isEmpty(iconPath)) {
            LogUtil.d(LogUtil.TAG, "menu right 本地存在用户主动上传的头像");
            Bitmap bitmap = BitmapFactory.decodeFile(iconPath);
            iv_pic.setImageBitmap(bitmap);
            return;
        }
        if (!TextUtils.isEmpty(str[1])) {
            LogUtil.d(LogUtil.TAG, "menu right 本地存在用户主动上传的头像");
            LoadImage loadImage = new LoadImage(getActivity(), this);
            loadImage.geBitmap(str[1], iv_pic);
        }
    }


    /**
     * 根据用户信息是否存在本地来设置当前视图
     */
    public void changView() {
        islogin = sharedPreferences.getBoolean("islogin", false);
        if (islogin) {
            relativeLayout_logined.setVisibility(View.VISIBLE);
            relativeLayout_unlogin.setVisibility(View.GONE);
            initUserInfo();
        } else {
            relativeLayout_unlogin.setVisibility(View.VISIBLE);
            relativeLayout_logined.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d(LogUtil.TAG,"menu right onResume...");
        str =SharedPreferencesUtils.getUserNameAndPhoto(getActivity());
        if (!TextUtils.isEmpty(str[0])){
            relativeLayout_logined.setVisibility(View.VISIBLE);
            relativeLayout_unlogin.setVisibility(View.GONE);
            initUserInfo();
        }else {
            relativeLayout_unlogin.setVisibility(View.VISIBLE);
            relativeLayout_logined.setVisibility(View.GONE);
        }
        getActivity().registerReceiver(receiver,new IntentFilter
                (DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void imageLoadOk(Bitmap bitmap, String url) {
        if (bitmap != null) {
            iv_pic.setImageBitmap(bitmap);
        }
    }
}
