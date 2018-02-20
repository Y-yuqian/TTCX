package com.example.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sdust.im.R;

public class Info implements Serializable{
	private static final long serialVersionUID = 1L;
	private double latitube;
	private double longitube;
	private int imageId;
	private String name;
	private String distance;
	private int zan;

	public static List<Info> infos = new ArrayList<Info>();

	static {
		infos.add(new Info(23.359004070783, 103.397982935478,
				R.drawable.location, "�Ϻ���԰", "����200��", 1201));
		infos.add(new Info(23.358498792354, 103.398472609209,
				R.drawable.location, "�տ�", "����600��", 1301));
		infos.add(new Info(23.359301644319, 103.397013364800,
				R.drawable.location, "һ����", "����200��", 200));
		infos.add(new Info(23.359889529774, 103.396689383264,
				R.drawable.location, "����", "����500��", 100));
	}

	public Info(double latitube, double longitube, int imageId, String name,
			String distance, int zan) {
		super();
		this.latitube = latitube;
		this.longitube = longitube;
		this.imageId = imageId;
		this.name = name;
		this.distance = distance;
		this.zan = zan;
	}

	/**
	 * @return the latitube
	 */
	public double getLatitube() {
		return latitube;
	}

	/**
	 * @param latitube
	 *            the latitube to set
	 */
	public void setLatitube(double latitube) {
		this.latitube = latitube;
	}

	/**
	 * @return the longitube
	 */
	public double getLongitube() {
		return longitube;
	}

	/**
	 * @param longitube
	 *            the longitube to set
	 */
	public void setLongitube(double longitube) {
		this.longitube = longitube;
	}

	/**
	 * @return the imageId
	 */
	public int getImageId() {
		return imageId;
	}

	/**
	 * @param imageId
	 *            the imageId to set
	 */
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the distance
	 */
	public String getDistance() {
		return distance;
	}

	/**
	 * @param distance
	 *            the distance to set
	 */
	public void setDistance(String distance) {
		this.distance = distance;
	}

	/**
	 * @return the zan
	 */
	public int getZan() {
		return zan;
	}

	/**
	 * @param zan
	 *            the zan to set
	 */
	public void setZan(int zan) {
		this.zan = zan;
	}

	/**
	 * @return the infos
	 */
	public static List<Info> getInfos() {
		return infos;
	}

	/**
	 * @param infos
	 *            the infos to set
	 */
	public static void setInfos(List<Info> infos) {
		Info.infos = infos;
	}
}
