package com.example.baidumap;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * ���򴫸���ʵ����
 * 
 * @author ��ͿͿi
 * 
 */
public class MyOrientationListener implements SensorEventListener {
	private Context mContext;// ��ǰ������
	
	// ���򴫸������
	private SensorManager mSensorManager;// ������������
	private Sensor mSensor;// ������

	// �������Ĳ���
	private float lastX;// x��

	/**
	 * ���������췽��
	 * 
	 * @param context ��ǰ������
	 */
	public MyOrientationListener(Context context) {
		this.mContext = context;// Ϊ�����ĸ�ֵ
	}

	/**
	 * ��ʼ����
	 */
	public void start() {
		mSensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);

		if (mSensorManager != null) {// �ж��Ƿ�֧��
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);// ��÷��򴫸���
		}

		if (mSensor != null) {// ��Ϊnull��ʾ�ֻ�֧�ַ��򴫸���
			// ע��һ��������
			mSensorManager.registerListener(this, mSensor,
					SensorManager.SENSOR_DELAY_UI);
		}
	}

	/**
	 * ��������
	 */
	public void stop() {
		mSensorManager.unregisterListener(this);// ��ע�������---Ҳ���ǽ�������
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onSensorChanged(SensorEvent event) {
		/*
		 * �����������ı��ʱ��
		 */
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {// ���������Ƿ��򴫸�����ʱ��
			float x = event.values[SensorManager.DATA_X];

			if (Math.abs(x - lastX) > 1.0) {// ����1��
				if (mOnOrientationListener != null) {
					mOnOrientationListener.OrientationChanged(x);// �ص���������ȥ����
				}
			}
			lastX = x;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		/*
		 * �����ȷ����ı��ʱ��
		 */
	}

	private OnOrientationListener mOnOrientationListener;// ������

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
