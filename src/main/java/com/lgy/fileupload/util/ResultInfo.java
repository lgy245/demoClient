package com.lgy.fileupload.util;


import java.io.Serializable;

/**
 * @Desc:返回结果封装类
 * @Author:lgy
 * @Time:2021/6/1715:15
 */

public class ResultInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;
    private String msg;
    private Object data;
    private Long total; // 分页信息：总条数

    public ResultInfo() {
    }

    private ResultInfo(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ResultInfo ok() {
        return new ResultInfo(Status.SUCCESS.code, Status.SUCCESS.message, null);
    }

    public static ResultInfo ok(Object data) {
        return new ResultInfo(Status.SUCCESS.code, Status.SUCCESS.message, data);
    }

    public static ResultInfo ok(String msg, Object data) {
        return new ResultInfo(Status.SUCCESS.code, msg, data);
    }
    public static ResultInfo fail() {
        return new ResultInfo(Status.WARN.code, Status.WARN.message, null);
    }
    public static ResultInfo fail(String msg) {
        return new ResultInfo(Status.WARN.code, msg, null);
    }

    public static ResultInfo fail(int errorCode, String msg) {
        return new ResultInfo(errorCode, msg, null);
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public Long getTotal() {
        return total;
    }

    public ResultInfo setTotal(Long total) {
        this.total = total;
        return this;
    }

}
