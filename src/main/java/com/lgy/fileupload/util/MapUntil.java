package com.lgy.fileupload.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MapUntil {
    static Map<String,Object> dataMap=new HashMap<String ,Object>();
    static ReadWriteLock lock=new ReentrantReadWriteLock();//创建读写锁的实例
    public static boolean getData(String key,String valSend){
        lock.readLock().lock();//读取前先上锁
        Object val=null;
        try{
            val=dataMap.get(key);
            if(val == null){
                // Must release read lock before acquiring write lock
                lock.readLock().unlock();
                lock.writeLock().lock();
                try{
                    //可能已经由其他线程写入数据
                    if(val==null){
                        //dataMap.put(key, "");//query from db
                        queryDataFromDB(key,valSend);
                    }
                }finally{
                    //Downgrade by acquiring read lock before releasing write lock
                    lock.readLock().lock();
                    // Unlock write, still hold read
                    lock.writeLock().unlock();
                }
            }
        }finally{
            lock.readLock().unlock();//最后一定不要忘记释放锁
        }
        return val == null ? false:true;
    }

    static void queryDataFromDB(String key, String val){
        dataMap.put(key, val);
        System.out.println("写入key="+key+">val="+val);
    }

    public static void main(String[] args) {

    }
}