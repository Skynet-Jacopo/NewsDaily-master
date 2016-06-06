package com.example.liuqun.newsdaily.model.biz;

import android.content.Context;
import android.net.rtp.AudioGroup;

import com.android.volley.Response;
import com.example.liuqun.newsdaily.common.CommonUtil;
import com.example.liuqun.newsdaily.common.LogUtil;
import com.example.liuqun.newsdaily.model.volleyhttp.VolleyHttp;

/**
 * Created by 90516 on 6/3/2016.
 * 评论管理
 */
public class CommentsManager {

    // cmt_list ?ver=版本号&nid=新闻id&type=1&stamp=yyyyMMdd&cid=评论id&dir=0&cnt=20

    /**
     * 请求评论(显示评论)
     *
     * @param ver             版本
     * @param listener 成功回调接口
     * @param args            顺序要求如下 。包含 nid (新闻id) dir ( 刷新方向 ，1 表示：下拉刷新，请求最新的数据 2
     *                        表示上拉刷新，，即加载更多的xx条 ) cid 评论id
     */

    public static void loadComments(Context context, String ver, Response
            .Listener<String> listener, Response.ErrorListener errorListener,
                                    int... args) {
        String url = CommonUtil
                .APPURL + "/cmt_list?ver=" + ver + "&nid=" + args[0] + "&dir=" + args[1
                ] + "&cid=" + args[2] + "&type=" + 1 + "&stamp=" + "20140707";

        new VolleyHttp(context).getJSONObject(url, listener, errorListener);

        LogUtil.d("url", "url == " + CommonUtil
                .APPURL + "/cmt_list?ver=" + ver + "&nid=" + args[0] + "&dir=" + args[1
                ] + "&cid=" + args[2] + "&type=" + 1 + "&stamp=" + "20140707");
    }

    /**
     * cmt?ver=版本号&nid=新闻编号&token=用户令牌&imei=手机标识符&ctx=评论内容 发送评论
     *
     * @param context         上下文
     * @param nid             新闻id
     * @param listener  成功回调接口
     * @param errorListener 失败回调接口
     * @param args            顺序如下：包含 ver:版本 ， token :用户令牌 imei:手机IMEI号 　 ctx : 评论内容
     */
    public static void sendCommnet(Context context, int nid,
                                   Response.Listener<String> listener, Response.ErrorListener errorListener,
                                   String... args) {
        String url = CommonUtil.APPURL + "/cmt_commit?nid=" + nid + "&ver="
                + args[0] + "&token=" + args[1] + "&imei=" + args[2] + "&ctx="
                + args[3];
        new VolleyHttp(context).getJSONObject(url, listener, errorListener);

        LogUtil.d("url", "url == "+CommonUtil.APPURL + "/cmt_commit?nid=" + nid + "&ver="
                + args[0] + "&token=" + args[1] + "&imei=" + args[2] + "&ctx="
                + args[3]);
    }

    /**
     * 评论数量
     * cmt_num?ver=版本号& nid=新闻编号
     * @param ver
     *            版本号
     * @param nid
     *            新闻id
     * @param listener 成功回调接口
     *            回调接口
     */
    public static void commentNum(Context context, int ver, int nid,
                                  Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = CommonUtil.APPURL + "/cmt_num?nid=" + nid + "&ver="
                + ver;
        new VolleyHttp(context).getJSONObject(url, listener, errorListener);
//		new AsyncHttpClient().get(url, responseHandlerInterface);
    }
}
