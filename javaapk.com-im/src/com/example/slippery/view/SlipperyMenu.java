package com.example.slippery.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.sdust.im.R;

public class SlipperyMenu extends HorizontalScrollView {
	private LinearLayout mLinearLayout;// 总布局
	private ViewGroup mMenu;
	private ViewGroup mContent;

	private int mMenuWidth;
	private int mScreenWidth;// 存放屏幕宽度
	private int mMenuRightPadding = 50;// 右边距(dp)
	private boolean mOnce;
	private boolean isOpen;

	/**
	 * 没有使用自定义属性的时候使用该方法
	 * 
	 * @param context
	 * @param attrs
	 */
	public SlipperyMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);// 调用3个构造参数的方法
	}

	/**
	 * 当使用了自定义属性时会调用这个构造方法
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SlipperyMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray array = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.SlidingMenu, defStyle, 0);// 获取自定义属性

		int index = array.getIndexCount();// 数量
		for (int i = 0; i < index; i++) {
			int attr = array.getIndex(i);// 根据下标得到值
			switch (attr) {// 根据这个值来判断
			case R.styleable.SlidingMenu_rightPadding:// 判断是否为当前定义的属性
				mMenuRightPadding = array.getDimensionPixelSize(attr,
						(int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 50, context
										.getResources().getDisplayMetrics()));// 传入当前的下标和默认值(默认值为50)
				break;
			}
		}

		array.recycle();// 释放资源

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);

		mScreenWidth = outMetrics.widthPixels;// 获取宽度
	}

	public SlipperyMenu(Context context) {
		this(context, null);// 反调两参构造方法

	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		/*
		 * 决定内部View的子视图高和宽以及自己的高和宽
		 */
		if (!mOnce) {
			mLinearLayout = (LinearLayout) getChildAt(0);

			mMenu = (ViewGroup) mLinearLayout.getChildAt(0);
			mContent = (ViewGroup) mLinearLayout.getChildAt(1);

			mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth
					- mMenuRightPadding;// 设置左边菜单的宽度
			mContent.getLayoutParams().width = mScreenWidth;// 设置右边显示内容的宽度
			mOnce = true;
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		/*
		 * 决定子视图的位置 设置偏移量将菜单隐藏
		 */
		super.onLayout(changed, l, t, r, b);
		if (changed) {// 判断是否发生变化
			this.scrollTo(mMenuWidth, 0);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		/*
		 * 判断用户的手势
		 */
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_UP:
			int scrollX = getScrollX();// 隐藏在屏幕左边的宽度

			if (scrollX >= mMenuWidth / 2) {
				this.smoothScrollTo(mMenuWidth, 0);// 隐藏
				isOpen = false;
			} else {
				this.smoothScrollTo(0, 0);// 显示
				isOpen = true;
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}
	
	/**
	 * 打开菜单
	 */
	public void openMenu(){
		if(isOpen){
			return;
		}else{
			this.smoothScrollTo(0, 0);// 表示已经打开
			isOpen = true;
		}
	}
	
	/**
	 * 关闭菜单
	 */
	public void closeMenu(){
		if(!isOpen){
			return;
		}else{
			this.smoothScrollTo(mMenuWidth, 0);// 表示关闭
			isOpen = false;
		}
	}
	
	/**
	 * 点击显示菜单
	 */
	public void toggle(){
		if(isOpen){
			closeMenu();
		}else{
			openMenu();
		}
	}
}
