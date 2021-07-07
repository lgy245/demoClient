package com.lgy.fileupload.exection;

/**
 * @Desc:异常
 * @Author:lgy
 * @Time:2021/6/2911:03
 */
public class FileException extends RuntimeException{
    public FileException(String message) {
        super(message);
    }

    public FileException(String message, Throwable cause) {
        super(message, cause);
    }
}