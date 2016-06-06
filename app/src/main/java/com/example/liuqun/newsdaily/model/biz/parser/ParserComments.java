package com.example.liuqun.newsdaily.model.biz.parser;

import com.example.liuqun.newsdaily.common.LogUtil;
import com.example.liuqun.newsdaily.model.entity.BaseEntity;
import com.example.liuqun.newsdaily.model.entity.Comment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import junit.framework.TestCase;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by 90516 on 6/3/2016.
 */
public class ParserComments extends TestCase {

    /**
     * 解析评论列表
     * @param json
     * @return
     */
    public static List<Comment> parserComment(String json){
        Type type = new TypeToken<BaseEntity<List<Comment>>>() {
        }.getType();

        BaseEntity<List<Comment>> entity = new Gson().fromJson(json, type);

        LogUtil.d(LogUtil.TAG,"评论数据 ----->"+entity.getData());
        return entity.getData();
    }

    /**
     * 解析评论数量
     * @param json
     * @return
     */
    public static int parserCommentNum(String json){
        Type type = new TypeToken<BaseEntity<Integer>>() {
        }.getType();

        BaseEntity<Integer> entity =new Gson().fromJson(json,type);

        return entity.getData().intValue();
    }

    /**
     * 发表评论后解析
     * @param json
     * @return
     */
    public static int parserSendComment(String json){
        Type type = new TypeToken<BaseEntity>() {
        }.getType();

        BaseEntity entity =new Gson().fromJson(json,type);
        return Integer.parseInt(entity.getStatus());
    }
}
