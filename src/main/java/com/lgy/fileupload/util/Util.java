package com.lgy.fileupload.util;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Desc:工具类
 * @Author:lgy
 * @Time:2021/6/3013:48
 */
public class Util {
    public static  <T> List<T> stringArray2List(List<String> data, Class<T> clazz) throws Exception {
        List<T> ts = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        int len = data.size()/fields.length;
        int m = data.size()%fields.length;
        if(m>0){
            len = len+1;
        }
        for(int i=0;i<len;i++){
            T t = clazz.newInstance();
            String[] strObj;
            if(i==len-1){
                strObj = Arrays.copyOfRange(data.toArray(new String[data.size()]),i*fields.length,data.size());
            }else{
                strObj = Arrays.copyOfRange(data.toArray(new String[data.size()]),i*fields.length,(i+1)*fields.length);
            }
            for(int j=0;j<fields.length;j++){
                int mod = fields[j].getModifiers();
                if(Modifier.isStatic(mod) || Modifier.isFinal(mod)){
                    continue;
                }
                fields[j].setAccessible(true);
                fields[j].set(t, strObj[j]);
            }
            ts.add(t);
        }
        return ts;
    }




    }


