package com.sdust.im.global;

public enum Result {
	ACCOUNT_EXISTED,//用户已存在
	ACCOUNT_CAN_USE,//用户名可以使用
	REGISTER_SUCCESS,//注册成功
	REGISTER_FAILED,//注册失败
	LOGIN_SUCCESS,//登录成功
	LOGIN_FAILED,//登录失败
	MAKE_FRIEND_REQUEST,//添加好友请求
	FRIEND_REQUEST_RESPONSE_REJECT,//添加好友被拒绝
	FRIEND_REQUEST_RESPONSE_ACCEPT;//添加好友被接收
}
