package com.sdust.im.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sdust.im.bean.ApplicationData;
import com.sdust.im.bean.ChatEntity;
import com.sdust.im.bean.TranObject;
import com.sdust.im.bean.TranObjectType;
import com.sdust.im.bean.User;
import com.sdust.im.databse.ImDB;
import com.sdust.im.global.Result;
import com.sdust.im.network.NetService;

public class UserAction {
	private static NetService mNetService = NetService.getInstance();
	/**
	 * 核实账户
	 */
	public static void accountVerify(String account) throws IOException {

		TranObject t = new TranObject(account, TranObjectType.REGISTER_ACCOUNT);
		mNetService.send(t);

	}
	/**
	 * 注册用户
	 * @param user用户名
	 * @throws IOException
	 */
	public static void register(User user) throws IOException {

		TranObject t = new TranObject(user, TranObjectType.REGISTER);
		mNetService.send(t);

	}
	/**
	 * 登录验证
	 */
	public static void loginVerify(User user) throws IOException {
		TranObject t = new TranObject(user, TranObjectType.LOGIN);
		mNetService.send(t);
	}
	/**
	 * 查找好友
	 */
	public static void searchFriend(String message) throws IOException {
		TranObject t = new TranObject(message, TranObjectType.SEARCH_FRIEND);
		mNetService.send(t);

	}
	/**
	 * 发送好友请求
	 * @param result请求
	 * @param id 本人的id
	 */
	public static void sendFriendRequest(Result result, Integer id) {
		TranObject t = new TranObject();
		t.setReceiveId(id);
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd hh:mm:ss");
		String sendTime = sdf.format(date);
		t.setSendTime(sendTime);
		User user = ApplicationData.getInstance().getUserInfo();
		t.setResult(result);
		t.setSendId(user.getId());
		t.setTranType(TranObjectType.FRIEND_REQUEST);
		t.setSendName(user.getUserName());
		try {
			mNetService.send(t);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 发送消息
	 * @param message
	 */
	public static void sendMessage(ChatEntity message) {

		TranObject t = new TranObject();
		t.setTranType(TranObjectType.MESSAGE);
		t.setReceiveId(message.getReceiverId());
		t.setSendName(ApplicationData.getInstance().getUserInfo().getUserName());
		t.setObject(message);
		try {
			mNetService.send(t);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
