package com.example.baidumap;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * ������
 * @author Diviner
 * @date 2018-1-19 ����2:37:36
 */
public class ViewPagerAdapter extends FragmentPagerAdapter{

	private List<Fragment> mFragmrntList;// ����һ������
	
	public ViewPagerAdapter(FragmentManager fm , List<Fragment> fragment) {
		super(fm);
		this.mFragmrntList = fragment;// ��������Ƭ��ֵ������
	}

	@Override
	public Fragment getItem(int arg0) {
		// �õ���ǰ������
		return mFragmrntList.get(arg0);
	}

	@Override
	public int getCount() {
		// �õ���ǰҳ��
		return mFragmrntList.size();
	}
}
