package com.example.baidumap;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 方向传感器实现类
 * 
 * @author 胡涂涂i
 * 
 */
public class MyOrientationListener implements SensorEventListener {
	private Context mContext;// 当前上下文
	
	// 方向传感器相关
	private SensorManager mSensorManager;// 传感器管理器
	private Sensor mSensor;// 传感器

	// 传感器的参数
	private float lastX;// x轴

	/**
	 * 带参数构造方法
	 * 
	 * @param context 当前上下文
	 */
	public MyOrientationListener(Context context) {
		this.mContext = context;// 为上下文赋值
	}

	/**
	 * 开始监听
	 */
	public void start() {
		mSensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);

		if (mSensorManager != null) {// 判断是否支持
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);// 获得方向传感器
		}

		if (mSensor != null) {// 不为null表示手机支持方向传感器
			// 注册一个监听器
			mSensorManager.registerListener(this, mSensor,
					SensorManager.SENSOR_DELAY_UI);
		}
	}

	/**
	 * 结束监听
	 */
	public void stop() {
		mSensorManager.unregisterListener(this);// 反注册监听器---也就是结束监听
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onSensorChanged(SensorEvent event) {
		/*
		 * 传感器发生改变的时候
		 */
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {// 返回类型是方向传感器的时候
			float x = event.values[SensorManager.DATA_X];

			if (Math.abs(x - lastX) > 1.0) {// 大于1度
				if (mOnOrientationListener != null) {
					mOnOrientationListener.OrientationChanged(x);// 回调到主界面去更新
				}
			}
			lastX = x;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		/*
		 * 当精度发生改变的时候
		 */
	}

	private OnOrientationListener mOnOrientationListener;// 监听器

	/**
	 * @param mOnOrientationListener
	 *            the mOnOrientationListener to set
	 */
	public void setOnOrientationListener(
			OnOrientationListener mOnOrientationListener) {
		this.mOnOrientationListener = mOnOrientationListener;
	}

	public interface OnOrientationListener {
		public void OrientationChanged(float x);
	}
}
