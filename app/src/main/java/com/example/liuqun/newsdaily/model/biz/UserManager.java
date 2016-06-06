package com.example.liuqun.newsdaily.model.biz;

import android.content.Context;

import com.android.volley.Response;
import com.example.liuqun.newsdaily.common.CommonUtil;
import com.example.liuqun.newsdaily.common.LogUtil;
import com.example.liuqun.newsdaily.common.SystemUtils;
import com.example.liuqun.newsdaily.model.volleyhttp.VolleyHttp;

import java.io.File;

/**
 * Created by 90516 on 6/4/2016.
 */
public class UserManager {
    private static UserManager userManager;
    private        Context     context;
    private        String      imei;

    public UserManager(Context context) {
        this.context = context;
        imei = SystemUtils.getIMEI(context);
    }

    public static UserManager getInstance(Context context) {
        if (userManager == null) {
            userManager = new UserManager(context);
        }
        return userManager;
    }

    /**
     * user_register?ver=版本号&uid=用户名&email=邮箱&pwd=登陆密码
     *
     * @param context       上下文
     * @param listener      成功回调接口
     * @param errorListener 失败回调接口
     * @param args          包含参数如下: ver: 版本  uid :用户昵称  pwd:密码  eamil:邮箱
     */
    public void register(Context context, Response.Listener<String> listener,
                         Response.ErrorListener errorListener, String... args) {
        LogUtil.d(LogUtil.TAG, "执行注册...");

        new VolleyHttp(context).getJSONObject(CommonUtil.APPURL
                + "/user_register?ver=" + args[0] + "&uid=" + args[1] + "&pwd="
                + args[2] + "&email=" + args[3], listener, errorListener);
    }

    /**
     * http://118.244.212.82:9094//newsClient/login?ver=1&uid=
     * admin&pwd=admin&device=000000000000000 用户登录处理方法
     *
     * @param context       上下文
     * @param listener      成功回调接口
     * @param errorListener 失败回调接口
     * @param args          包含参数如下: ver : 版本  uid : 用户昵称  pwd : 密码  imei:手机IMEI号
     *                      device: 登录设备: 0 为移动端 , 1 为PC端
     */
    public void login(Context context, Response.Listener<String> listener,
                      Response.ErrorListener errorListener, String... args) {
        LogUtil.d(LogUtil.TAG, "执行登录 ... ");

        new VolleyHttp(context).getJSONObject(CommonUtil.APPURL
                + "/user_login?ver=" + args[0] + "&uid=" + args[1] + "&pwd="
                + args[2] + "&device=" + args[3], listener, errorListener);
    }

    /**
     * user_forgetpass?ver=版本号&email=邮箱
     *
     * @param context       上下文
     * @param listener      成功回调接口
     * @param errorListener 失败回调接口
     * @param args          ver:版本号
     *                      email:邮箱
     */
    public void forgetPass(Context context, Response.Listener<String>
            listener, Response.ErrorListener errorListener, String... args) {
        LogUtil.d(LogUtil.TAG, "执行忘记密码 ... ");

        new VolleyHttp(context).getJSONObject(CommonUtil.APPURL
                        + "/user_forgetpass?ver=" + args[0] + "&email=" + args[1],
                listener, errorListener);
    }

    /**
     * http://118.244.212.82:9094//newsClient/home?ver=sfkl&token=admin1abc&imei
     * =sdf 获取用户中心数据
     *
     * @param context       上下文
     * @param listener      成功回调接口
     * @param errorListener 失败回调接口
     * @param args          包含参数: 顺序如下 ver: 版本  token :令牌 imei: 手机IMEI
     */
    public void getUserInfo(Context context, Response.Listener<String>
            listener, Response.ErrorListener errorListener, String... args) {
        LogUtil.d(LogUtil.TAG, "ִ执行用户中心...");

        new VolleyHttp(context).getJSONObject(CommonUtil.APPURL
                + "/user_home?ver=" + args[0] + "&token=" + args[1] + "&imei="
                + args[2], listener, errorListener);
    }


    /**
     * // TODO: 6/4/2016 这里未能实现
     *
     * @param context 上下文
     * @param token   令牌
     * @param file    文件
     */
    public void changPhoto(Context context, String token, File file, Response
            .Listener<String> listener, Response.ErrorListener errorListener) {

        new VolleyHttp(context).uploadImage(CommonUtil.APPURL
                + "/user_image?token=" + token, file, listener, errorListener);
    }
}
