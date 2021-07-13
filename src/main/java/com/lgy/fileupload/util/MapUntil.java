package com.lgy.fileupload.util;

import org.apache.commons.collections.CollectionUtils;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MapUntil {
    public static boolean isHeart = true;

    public static Object sendFileIndexLockLoss = new Object();
    //    public static ConcurrentHashMap<String, char[]> sendFileIndexLoss = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, List<Integer>> sendFileIndexLoss = new ConcurrentHashMap<>();
/*

    public static Integer getSendFileNextIndexLoss(String id,char[] lossPackages){
        synchronized (sendFileIndexLockLoss){
            char[] data = sendFileIndexLoss.get(id);
            if(data == null){
                data = lossPackages;
            }
            if(data.length == 0){
                sendFileIndexLoss.remove(id);
                return null;
            }
            char[] temp = new char[data.length-1];
            System.arraycopy(data, 1, temp, 0, temp.length);
            sendFileIndexLoss.put(id, temp);

            return Integer.parseInt(String.valueOf(data[0]));
        }
    }
*/

    /**
     * 获取当前未穿分片list中第一个 下标
     * @param id
     * @param lossPackages
     * @return
     */
    public static Integer getSendFileNextIndexLoss(String id,List<Integer> lossPackages){
        synchronized (sendFileIndexLockLoss){
            List<Integer> data = sendFileIndexLoss.get(id);
            if(data == null){
                data = lossPackages;
            }
            if(data.size() == 0){
                sendFileIndexLoss.remove(id);
                return null;
            }
            List<Integer> temp = new ArrayList<>();
            CollectionUtils.addAll(temp, new Object[data.size()]);
            Collections.copy(temp, data);
            temp.remove(temp.get(0));
            sendFileIndexLoss.put(id, temp);
            return data.get(0);
        }
    }

    /**
     * 获取未传分片的所有下标放入list
     * @param id
     * @param totalIndex
     * @return
     */
    public static List<Integer> getLossPackage(String id,int totalIndex){
        char[] array = getStatusArray(id, totalIndex);
        List<Integer> list  = new ArrayList<>();
        for(int i = 0; i<array.length;i++){
            if(array[i] == '0'){
                if(list.size()>200){
                    break;
                }
                list.add(i+1);
            }
        }
        return list;
    }
    public static Object sendFileIndexLock = new Object();
    public static ConcurrentHashMap<String, Integer> sendFileIndex = new ConcurrentHashMap<>();

    public static Integer getSendFileNextIndex(String id){
        synchronized (sendFileIndexLock){
            Integer index = sendFileIndex.get(id);
            if(index == null){
                index = 0;
            }
            index = index +1;
            sendFileIndex.put(id, index);
            return index;
        }
    }
    public static Object statusArrayLock = new Object();
    public static ConcurrentHashMap<String, char[]> statusArray = new ConcurrentHashMap<>();

    public static char[] getStatusArray(String id,int totalIndex){
        synchronized (statusArrayLock){
            char[] array = statusArray.get(id);
            if(array == null){
                array = new char[totalIndex];
                for(int i = 0;i<array.length;i++){
                    array[i] = '0';
                }
                statusArray.put(id, array);
            }

            return array;
        }
    }
    public static void setSubFileStatus(String id,int index,int totalIndex){
        synchronized (statusArrayLock){
            char[] array = statusArray.get(id);
            if(array == null){
                array = new char[totalIndex];
                for(int i = 0;i<array.length;i++){
                    array[i] = '0';
                }
            }
            array[index] = '1';
            statusArray.put(id, array);
        }
    }

    static Map<String,Object> dataMap=new HashMap<String ,Object>();
    static ReadWriteLock lock=new ReentrantReadWriteLock();//创建读写锁的实例

    /**
     * 缓存判断当前文件是否接受过
     * @param key
     * @param valSend
     * @return
     */
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