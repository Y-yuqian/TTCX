package com.example.util;


import com.sdust.im.R;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * �Զ���dialog
 * 
 * @author Diviner
 * @date 2018-1-23 ����3:12:30
 */
public abstract class BottomDialogUtil extends Dialog implements
		android.view.View.OnClickListener {
	private Activity mActivity;
	private Button mGeneralMap;
	private Button msatelliteMap;
	private Button mRealTimeTraffic;

	/**
	 * ���췽��
	 * 
	 * @param activity
	 *            ��ǰ���õĻ����
	 */
	public BottomDialogUtil(Activity activity) {
		super(activity, R.style.MyDialogTheme);
		this.mActivity = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bottom_dialog_layout);// ���ز����ļ�

		init();
	}

	public void init() {
		mGeneralMap = (Button) findViewById(R.id.general_map_id);
		mGeneralMap.setOnClickListener(this);

		msatelliteMap = (Button) findViewById(R.id.satellite_map_id);
		msatelliteMap.setOnClickListener(this);

		mRealTimeTraffic = (Button) findViewById(R.id.real_time_traffic_id);
		mRealTimeTraffic.setOnClickListener(this);
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
		case R.id.general_map_id:
			GeneralMap();
			this.cancel();
			break;

		case R.id.satellite_map_id:
			SatelliteMap();
			this.cancel();
			break;
		case R.id.real_time_traffic_id:
			RealTimeTraffic();
			this.cancel();
			break;
		}
	}

	/**
	 * ��ͨ��ͼ
	 */
	public abstract void GeneralMap();

	/**
	 * ���ǵ�ͼ
	 */
	public abstract void SatelliteMap();

	/**
	 * ʵʱ��ͨ
	 */
	public abstract void RealTimeTraffic();
}
