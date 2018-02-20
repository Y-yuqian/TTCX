package com.example.util;

import com.baidu.mapapi.model.LatLng;



public class MapUtil {
	/**
	 * ��γ���Ƿ�Ϊ(0,0)��
	 * 
	 * @return
	 */
	public static boolean isZeroPoint(double latitude, double longitude) {
		return isEqualToZero(latitude) && isEqualToZero(longitude);
	}

	/**
	 * У��double��ֵ�Ƿ�Ϊ0
	 * 
	 * @param value
	 * 
	 * @return
	 */
	public static boolean isEqualToZero(double value) {
		return Math.abs(value - 0.0) < 0.01 ? true : false;
	}

	/**
	 * ����������ȡͼ��ת�ĽǶ�
	 */
	public static double getAngle(LatLng fromPoint, LatLng toPoint) {
		double slope = getSlope(fromPoint, toPoint);
		if (slope == Double.MAX_VALUE) {
			if (toPoint.latitude > fromPoint.latitude) {
				return 0;
			} else {
				return 180;
			}
		}
		float deltAngle = 0;
		if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
			deltAngle = 180;
		}
		double radio = Math.atan(slope);
		return 180 * (radio / Math.PI) + deltAngle - 90;
	}

	/**
	 * ��б��
	 */
	public static double getSlope(LatLng fromPoint, LatLng toPoint) {
		if (toPoint.longitude == fromPoint.longitude) {
			return Double.MAX_VALUE;
		}
		return (toPoint.latitude - fromPoint.latitude)
				/ (toPoint.longitude - fromPoint.longitude);
	}
}
