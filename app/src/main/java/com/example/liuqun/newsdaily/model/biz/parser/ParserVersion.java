package com.example.liuqun.newsdaily.model.biz.parser;

import com.example.liuqun.newsdaily.model.entity.Version;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by 90516 on 6/4/2016.
 */
public class ParserVersion {

    public static Version parserJson(String json){
        Gson gson =new Gson();
        Type type =new TypeToken<Version>(){}.getType();
        return gson.fromJson(json,type);
    }
}
