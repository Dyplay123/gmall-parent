package com.atguigu.gmall.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

public class Jsons {
    private static ObjectMapper mapper = new ObjectMapper();
    /**
     * 把对象转为json字符串
     *
     */
    public static String toStr(Object object) {
        //jackson
        try {
            String s = mapper.writeValueAsString(object);
            return s;
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    //把json转成指定的对象
    public static<T>  T toObj(String jsonStr, Class<T> clz) {
        if(StringUtils.isEmpty(jsonStr)){
            return null;
        }

        T t = null;
        try {
            t = mapper.readValue(jsonStr, clz);
            return t;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static<T> T toObj(String jsonStr, TypeReference<T> tr){
        if(StringUtils.isEmpty(jsonStr)){
            return null;
        }
        T t = null;
        try {

            t = mapper.readValue(jsonStr, tr);
            return t;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
