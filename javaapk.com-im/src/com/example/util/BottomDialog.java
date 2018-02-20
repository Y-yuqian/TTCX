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
 * �Զ���dialog
 * 
 * @author Diviner
 * @date 2018-1-23 ����3:12:30
 */
public abstract class BottomDialog extends Dialog implements
		android.view.View.OnClickListener {
	private Activity mActivity;
	private TextView mApp_exit;// �˳�
	private TextView mApp_change;// ע���û�

	/**
	 * ���췽��
	 * 
	 * @param activity
	 *            ��ǰ���õĻ����
	 */
	public BottomDialog(Activity activity) {
		super(activity, R.style.MyDialogTheme);
		this.mActivity = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_exit);// ���ز����ļ�

		init();
	}

	public void init() {
		mApp_change = (TextView) findViewById(R.id.app_change_user);// ע��
		mApp_change.setOnClickListener(this);

		mApp_exit = (TextView) findViewById(R.id.app_exit);// �˳�
		mApp_exit.setOnClickListener(this);

		setViewLocation();
		setCanceledOnTouchOutside(true);// �ⲿ���ȡ��
	}

	/**
	 * ����dialogλ����Ļ�ײ�
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
		// ������ʾλ��
		onWindowAttributesChanged(lp);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.app_change_user:// ע��
			Intent intent = new Intent(mActivity, LoginActivity.class);
			mActivity.startActivity(intent);
			((Activity) mActivity).overridePendingTransition(
					R.anim.activity_up, R.anim.fade_out);
			NetService.getInstance().closeConnection();
			mActivity.finish();
			break;

		case R.id.app_exit:// �˳�
			NetService.getInstance().closeConnection();
			mActivity.finish();
			break;
		}
	}
}
