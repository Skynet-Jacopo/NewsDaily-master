package com.example.liuqun.newsdaily.model.biz;

import android.app.DownloadManager;
import android.content.Context;
import android.net.TrafficStats;
import android.net.Uri;

import com.android.volley.Response;
import com.example.liuqun.newsdaily.common.CommonUtil;
import com.example.liuqun.newsdaily.model.volleyhttp.VolleyHttp;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 90516 on 6/4/2016.
 */
public class UpdateManager {

    /**
     * 下载版本
     *
     * @param context 上下文
     * @param url     下载地址
     */
    public static void downLoad(Context context, String url) {
        DownloadManager manager = (DownloadManager) context.getSystemService(Context
                .DOWNLOAD_SERVICE);//初始化下载管理器
        DownloadManager.Request request = new DownloadManager.Request(Uri
                .parse(url));//创建请求
        //设置允许使用的网络类型,wifi
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //在通知栏显示下载详情 在API 11中被setNotificationVisibility()取代
//        request.setShowRunningNotification(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        //显示下载界面
        request.setVisibleInDownloadsUi(true);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ddhh-mm-ss");
        String           date       = dateFormat.format(new Date());
        //设置下载文件后文件存放的位置 -- 如果目标位置已经存在这个文件名,则不执行下载,所以用date类型随机取名
        request.setDestinationInExternalFilesDir(context, null, date + ".apk");
        //destination  目标 in external 内置式 file dir 文件目录
        manager.enqueue(request);//将下载请求放入队列
    }

    /**
     *
     * @param context 上下文
     * @param listener 成功回调接口
     * @param errorListener 失败回调接口
     * @param args 请求参数 , 顺序如下: arg[0] : IMEI , arg[1] : pkg包名 , arg[2] : ver版本
     */
    public static void judgeUpdate(Context context, Response.Listener<String>
            listener, Response.ErrorListener errorListener, String... args) {//judge  判断
        String url = CommonUtil.APPURL+"/update?imei="+args[0]+"&pkg="+args[1]+"&ver="+args[2];
        new VolleyHttp(context).getJSONObject(url,listener,errorListener);
    }
}
