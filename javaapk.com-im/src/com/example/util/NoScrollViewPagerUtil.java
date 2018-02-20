package com.example.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * �ֶ�����viewpager�Ƿ�������һ��� ȥ����������
 * 
 * @author Diviner
 * @date 2018-1-19 ����1:52:22
 */
public class NoScrollViewPagerUtil extends ViewPager {
	private boolean noScroll = false;

	public NoScrollViewPagerUtil(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NoScrollViewPagerUtil(Context context) {
		super(context);
	}

	/**
	 * �������Ϊtrue��ȡ������Ч�� �������Ϊfalse��ȡ������Ч��
	 * 
	 * @param noScroll
	 */
	public void setNoScroll(boolean noScroll) {
		this.noScroll = noScroll;
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		}

		return !noScroll && super.onTouchEvent(event);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return !noScroll && super.onInterceptTouchEvent(arg0);
	}

	@Override
	public void setCurrentItem(int item, boolean smoothScroll) {
		super.setCurrentItem(item, smoothScroll);
	}

	@Override
	public void setCurrentItem(int item) {
		// false ȥ������Ч��
		super.setCurrentItem(item, false);
	}
}
