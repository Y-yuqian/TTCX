package com.example.baidumap;

/**
 * 菜单数据实体类
 * @author 胡涂涂i
 *
 */
public class LeftInformation {
	private int image;
	private String name;

	public LeftInformation() {
	}

	public LeftInformation(int image, String name) {
		super();
		this.image = image;
		this.name = name;
	}

	/**
	 * @return the image
	 */
	public int getImage() {
		return image;
	}

	/**
	 * @param image
	 *            the image to set
	 */
	public void setImage(int image) {
		this.image = image;
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

}
