package com.lgy.fileupload.util;

import java.io.File;
import java.io.IOException;

/**
 * @Desc:地址工具类
 * @Author:lgy
 * @Time:2021/7/19:35
 */
public class PropertiesUntil {
    public static String rootPath = "";
    // 作为客户端的存储路径
    public static    String SERVER_STORY ="\\server\\client\\data\\";
    // 作为客户端的文件分割保存路径
    public static    String SPLIT_PATH ="\\server\\client\\split\\";
    // 作为客户端的文件数据库记录路径
    public static    String STORY_FILE_PATH ="\\server\\client\\fileServer\\remember.txt";
    //作为服务端的存储路径
    //作为服务端的文件分割保存路径
    public static    String SERVER_SPLIT_PATH ="\\server\\server\\split\\";
    //作为服务端的文件记录路径
    public static    String SERVER_STORY_FILE_PATH ="\\server\\server\\fileServer\\storge.txt";

    public static   String STORY_FILE_PATH_1 ="\\server\\client\\fileServer\\";
    public static   String SERVER_STORY_FILE_PATH_1 ="\\server\\server\\fileServer\\";

    public static String getRootPath(){
        return PropertiesUntil.rootPath;
    }

    public static void autoCreateDir() throws IOException {
        File file1= new File(getServerStory());
        if(!file1.exists()){
            file1.mkdirs();
        }
        File file2= new File(getSplitPath());
        if(!file2.exists()){
            file2.mkdirs();
        }
        File file3= new File(getServerSplitPath());
        if(!file3.exists()){
            file3.mkdirs();
        }
        File file4= new File(getStoryFilePath1());
        if(!file4.exists()){
            file4.mkdirs();
        }
        File file5= new File(getServerStoryFilePath1());
        if(!file5.exists()){
            file5.mkdirs();
        }
        File file6= new File(getStoryFilePath());
        if(!file6.exists()){
            file6.createNewFile();
        }
        File file7= new File(getServerStoryFilePath());
        if(!file7.exists()){
            file7.createNewFile();
        }

        PropertiesUntil.setServerStory(getRootPath()+PropertiesUntil.SERVER_STORY);
        PropertiesUntil.setSplitPath(getRootPath()+PropertiesUntil.SPLIT_PATH);
        PropertiesUntil.setStoryFilePath(getRootPath()+PropertiesUntil.STORY_FILE_PATH);
        PropertiesUntil.setServerSplitPath(getRootPath()+PropertiesUntil.SERVER_SPLIT_PATH);
        PropertiesUntil.setServerStoryFilePath(getRootPath()+PropertiesUntil.SERVER_STORY_FILE_PATH);
    }

    public static String getServerStory() {
        return getRootPath()+SERVER_STORY;
    }

    public static String getSplitPath() {
        return getRootPath()+SPLIT_PATH;
    }

    public static String getStoryFilePath() {
        return getRootPath()+STORY_FILE_PATH;
    }

    public static String getServerSplitPath() {
        return getRootPath()+SERVER_SPLIT_PATH;
    }

    public static String getServerStoryFilePath() {
        return getRootPath()+SERVER_STORY_FILE_PATH;
    }

    public static String getStoryFilePath1() {
        return getRootPath()+STORY_FILE_PATH_1;
    }

    public static String getServerStoryFilePath1() {
        return getRootPath()+SERVER_STORY_FILE_PATH_1;
    }


    public static void setRootPath(String rootPath) {
        PropertiesUntil.rootPath = rootPath;
    }

    public static void setServerStory(String serverStory) {
        SERVER_STORY = serverStory;
    }

    public static void setSplitPath(String splitPath) {
        SPLIT_PATH = splitPath;
    }

    public static void setStoryFilePath(String storyFilePath) {
        STORY_FILE_PATH = storyFilePath;
    }

    public static void setServerSplitPath(String serverSplitPath) {
        SERVER_SPLIT_PATH = serverSplitPath;
    }

    public static void setServerStoryFilePath(String serverStoryFilePath) {
        SERVER_STORY_FILE_PATH = serverStoryFilePath;
    }

    public static void setStoryFilePath1(String storyFilePath1) {
        STORY_FILE_PATH_1 = storyFilePath1;
    }

    public static void setServerStoryFilePath1(String serverStoryFilePath1) {
        SERVER_STORY_FILE_PATH_1 = serverStoryFilePath1;
    }

}
