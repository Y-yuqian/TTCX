package com.example.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sdust.im.R;
import com.sdust.im.activity.LoginActivity;
import com.sdust.im.network.NetService;

/**
 * 自定义dialog
 * 
 * @author Diviner
 * @date 2018-1-23 下午3:12:30
 */
public abstract class BottomDialog extends Dialog implements
		android.view.View.OnClickListener {
	private Activity mActivity;
	private TextView mApp_exit;// 退出
	private TextView mApp_change;// 注销用户

	/**
	 * 构造方法
	 * 
	 * @param activity
	 *            当前调用的活动名称
	 */
	public BottomDialog(Activity activity) {
		super(activity, R.style.MyDialogTheme);
		this.mActivity = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_exit);// 加载布局文件

		init();
	}

	public void init() {
		mApp_change = (TextView) findViewById(R.id.app_change_user);// 注销
		mApp_change.setOnClickListener(this);

		mApp_exit = (TextView) findViewById(R.id.app_exit);// 退出
		mApp_exit.setOnClickListener(this);

		setViewLocation();
		setCanceledOnTouchOutside(true);// 外部点击取消
	}

	/**
	 * 设置dialog位于屏幕底部
	 */
	private void setViewLocation() {
		DisplayMetrics dm = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int height = dm.heightPixels;

		Window window = this.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.x = 0;
		lp.y = height;
		lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
		lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		// 设置显示位置
		onWindowAttributesChanged(lp);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.app_change_user:// 注销
			Intent intent = new Intent(mActivity, LoginActivity.class);
			mActivity.startActivity(intent);
			((Activity) mActivity).overridePendingTransition(
					R.anim.activity_up, R.anim.fade_out);
			NetService.getInstance().closeConnection();
			mActivity.finish();
			break;

		case R.id.app_exit:// 退出
			NetService.getInstance().closeConnection();
			mActivity.finish();
			break;
		}
	}
}
