package com.example.baidumap;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.slippery.view.SlipperyMenu;
import com.example.util.BottomDialog;
import com.example.util.NoScrollViewPagerUtil;
import com.sdust.im.R;
import com.sdust.im.activity.LoginActivity;
import com.sdust.im.activity.MainActivity;
import com.sdust.im.fragment.FriendListFragment;
import com.sdust.im.fragment.MessageFragment;
import com.sdust.im.network.NetService;

public class SlipperysActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {
	private SlipperyMenu mSlipperyMenu;
	// list相关
	private ListView mListView;
	private List<LeftInformation> mLeftList = new ArrayList<LeftInformation>();

	// 无滑动实现
	private NoScrollViewPagerUtil mViewpager;// 自定义控件
	private Button mFriend, mMap, mMessage;
	private List<Fragment> mFragmentList;

	// 界面按钮
	private Button mReSet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.slippery_home_layout);

		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		mSlipperyMenu = (SlipperyMenu) findViewById(R.id.id_menu);

		initLeftInf();// 初始化左边菜单

		LeftAdapter adapter = new LeftAdapter(this,
				R.layout.left_menu_item_layout, mLeftList);
		mListView = (ListView) findViewById(R.id.id_ListView);
		mListView.setAdapter(adapter);// 设置适配器
		mListView.setOnItemClickListener(this);

		mReSet = (Button) findViewById(R.id.reset_id);
		mReSet.setOnClickListener(this);

		mFriend = (Button) findViewById(R.id.friend_button_id);
		mFriend.setOnClickListener(this);

		mMap = (Button) findViewById(R.id.map_button_id);
		mMap.setOnClickListener(this);

		mMessage = (Button) findViewById(R.id.friendMessage_id);
		mMessage.setOnClickListener(this);

		// 添加碎片
		mFragmentList = new ArrayList<Fragment>();
		// 加载下面三个碎片到布局
		mFragmentList.add(new MessageFragment());// 消息列表
		mFragmentList.add(new FriendListFragment());// 好友列表
		mFragmentList.add(new MapFragment());// 地图

		mViewpager = (NoScrollViewPagerUtil) findViewById(R.id.ViewPage_id);
		// 加载adapter
		mViewpager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),
				mFragmentList));
		mViewpager.setNoScroll(true);// 不可以滑动

		mViewpager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
					@Override
					public void onPageScrolled(int position,
							float positionOffset, int positionOffsetPixels) {
						setdate(position);// 将当前的下标颜色改为红色
					}

					@Override
					public void onPageSelected(int position) {

					}

					@Override
					public void onPageScrollStateChanged(int state) {

					}
				});
	}

	/*
	 * 点击按钮改变字体的颜色
	 */
	public void setdate(int index) {
		switch (index) {
		case 0:
			mMessage.setTextColor(Color.RED);
			mMap.setTextColor(Color.BLACK);
			mFriend.setTextColor(Color.BLACK);
			break;
		case 1:
			mFriend.setTextColor(Color.RED);
			mMap.setTextColor(Color.BLACK);
			mMessage.setTextColor(Color.BLACK);
			break;
		case 2:
			mMap.setTextColor(Color.RED);
			mFriend.setTextColor(Color.BLACK);
			mMessage.setTextColor(Color.BLACK);
		}
	}

	/**
	 * 初始化菜单布局
	 */
	private void initLeftInf() {
		LeftInformation li1 = new LeftInformation(R.drawable.ic_launcher,
				"个人信息");
		mLeftList.add(li1);

		LeftInformation li2 = new LeftInformation(R.drawable.ic_launcher,
				"我的足迹");
		mLeftList.add(li2);
	}

	@Override
	public void onClick(View view) {
		// 点击事件
		switch (view.getId()) {
		case R.id.friendMessage_id:// 消息
			mViewpager.setCurrentItem(0);
			setdate(0);
			break;

		case R.id.friend_button_id:// 好友
			mViewpager.setCurrentItem(1);
			setdate(1);
			break;

		case R.id.map_button_id:// 地图
			mViewpager.setCurrentItem(2);
			setdate(2);
			break;

		case R.id.reset_id:// 设置按钮
			new BottomDialog(this) {
			}.show();
			// Toast.makeText(this, "实现逻辑", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {
		LeftInformation li = mLeftList.get(position);
		Toast.makeText(this, li.getName(), Toast.LENGTH_SHORT).show();
	}
}
