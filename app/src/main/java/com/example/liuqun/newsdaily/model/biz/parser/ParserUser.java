package com.example.liuqun.newsdaily.model.biz.parser;

import com.example.liuqun.newsdaily.model.entity.BaseEntity;
import com.example.liuqun.newsdaily.model.entity.Register;
import com.example.liuqun.newsdaily.model.entity.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by 90516 on 6/4/2016.
 * 解析用户模块的返回数据
 */
public class ParserUser {

    /**
     * 解析用户注册返回信息
     * @param json json数据
     * @return BaseEntity<Register>
     */
    public static BaseEntity<Register> parserRigister(String json){
        Gson gson =new Gson();
        return gson.fromJson(json,new TypeToken<BaseEntity<Register>>(){}
                .getType());
    }

    /**
     * 解析用户中心数据
     * @param json json数据
     * @return BaseEntity<User>
     */
    public static BaseEntity<User> parserUser(String json){
        return new Gson().fromJson(json,new TypeToken<BaseEntity<User>>(){}
                .getType());
    }

    /**
     *解析上传用户头像(其实和第一个是相同的)
     * @param json json数据
     * @return BaseEntity<Register>
     */
    public static BaseEntity<Register> parserUploadImage(String json){
        return new Gson().fromJson(json,new TypeToken<BaseEntity<Register>>()
        {}.getType());
    }
}
