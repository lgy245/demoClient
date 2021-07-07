package com.lgy.fileupload.clientServer.domain;

public class TransferType {

	/**
	 * 请求传输文件
	 */
	public static final int REQUEST = 0;

	/**
	 * 同意传输文件
	 */
	public static final int AGREE = 1;

	/**
	 * 拒绝传输文件
	 */
	public static final int REFUSE = 2;

	/**
	 * 文件传输数据
	 */
	public static final int TRANSFER = 3;

	/**
	 * 文件传输完成
	 */
	public static final int FINISH = 4;


	/**
	 * 正常心跳包
	 */
	public static final int HEART = 5;

	/**
	 * 存在丢包
	 */
	public static final int PACKET_LOSS = 6;
	/**
	 * 客户端传输
	 */
	public static final int CLIENT_SEND = 7;
	/**
	 * 客户端接受
	 */
	public static final int CLIENT_DOWN = 8;

}