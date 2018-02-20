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
	private LinearLayout mLinearLayout;// �ܲ���
	private ViewGroup mMenu;
	private ViewGroup mContent;

	private int mMenuWidth;
	private int mScreenWidth;// �����Ļ���
	private int mMenuRightPadding = 50;// �ұ߾�(dp)
	private boolean mOnce;
	private boolean isOpen;

	/**
	 * û��ʹ���Զ������Ե�ʱ��ʹ�ø÷���
	 * 
	 * @param context
	 * @param attrs
	 */
	public SlipperyMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);// ����3����������ķ���
	}

	/**
	 * ��ʹ�����Զ�������ʱ�����������췽��
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SlipperyMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray array = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.SlidingMenu, defStyle, 0);// ��ȡ�Զ�������

		int index = array.getIndexCount();// ����
		for (int i = 0; i < index; i++) {
			int attr = array.getIndex(i);// �����±�õ�ֵ
			switch (attr) {// �������ֵ���ж�
			case R.styleable.SlidingMenu_rightPadding:// �ж��Ƿ�Ϊ��ǰ���������
				mMenuRightPadding = array.getDimensionPixelSize(attr,
						(int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 50, context
										.getResources().getDisplayMetrics()));// ���뵱ǰ���±��Ĭ��ֵ(Ĭ��ֵΪ50)
				break;
			}
		}

		array.recycle();// �ͷ���Դ

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);

		mScreenWidth = outMetrics.widthPixels;// ��ȡ���
	}

	public SlipperyMenu(Context context) {
		this(context, null);// �������ι��췽��

	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		/*
		 * �����ڲ�View������ͼ�ߺͿ��Լ��Լ��ĸߺͿ�
		 */
		if (!mOnce) {
			mLinearLayout = (LinearLayout) getChildAt(0);

			mMenu = (ViewGroup) mLinearLayout.getChildAt(0);
			mContent = (ViewGroup) mLinearLayout.getChildAt(1);

			mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth
					- mMenuRightPadding;// ������߲˵��Ŀ��
			mContent.getLayoutParams().width = mScreenWidth;// �����ұ���ʾ���ݵĿ��
			mOnce = true;
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		/*
		 * ��������ͼ��λ�� ����ƫ�������˵�����
		 */
		super.onLayout(changed, l, t, r, b);
		if (changed) {// �ж��Ƿ����仯
			this.scrollTo(mMenuWidth, 0);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		/*
		 * �ж��û�������
		 */
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_UP:
			int scrollX = getScrollX();// ��������Ļ��ߵĿ��

			if (scrollX >= mMenuWidth / 2) {
				this.smoothScrollTo(mMenuWidth, 0);// ����
				isOpen = false;
			} else {
				this.smoothScrollTo(0, 0);// ��ʾ
				isOpen = true;
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}
	
	/**
	 * �򿪲˵�
	 */
	public void openMenu(){
		if(isOpen){
			return;
		}else{
			this.smoothScrollTo(0, 0);// ��ʾ�Ѿ���
			isOpen = true;
		}
	}
	
	/**
	 * �رղ˵�
	 */
	public void closeMenu(){
		if(!isOpen){
			return;
		}else{
			this.smoothScrollTo(mMenuWidth, 0);// ��ʾ�ر�
			isOpen = false;
		}
	}
	
	/**
	 * �����ʾ�˵�
	 */
	public void toggle(){
		if(isOpen){
			closeMenu();
		}else{
			openMenu();
		}
	}
}
