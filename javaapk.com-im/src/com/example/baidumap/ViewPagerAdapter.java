package com.example.baidumap;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * 适配器
 * @author Diviner
 * @date 2018-1-19 下午2:37:36
 */
public class ViewPagerAdapter extends FragmentPagerAdapter{

	private List<Fragment> mFragmrntList;// 定义一个容器
	
	public ViewPagerAdapter(FragmentManager fm , List<Fragment> fragment) {
		super(fm);
		this.mFragmrntList = fragment;// 将滑动碎片赋值给容器
	}

	@Override
	public Fragment getItem(int arg0) {
		// 得到当前的子项
		return mFragmrntList.get(arg0);
	}

	@Override
	public int getCount() {
		// 得到当前页数
		return mFragmrntList.size();
	}
}
