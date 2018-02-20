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
	// list���
	private ListView mListView;
	private List<LeftInformation> mLeftList = new ArrayList<LeftInformation>();

	// �޻���ʵ��
	private NoScrollViewPagerUtil mViewpager;// �Զ���ؼ�
	private Button mFriend, mMap, mMessage;
	private List<Fragment> mFragmentList;

	// ���水ť
	private Button mReSet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.slippery_home_layout);

		init();
	}

	/**
	 * ��ʼ��
	 */
	private void init() {
		mSlipperyMenu = (SlipperyMenu) findViewById(R.id.id_menu);

		initLeftInf();// ��ʼ����߲˵�

		LeftAdapter adapter = new LeftAdapter(this,
				R.layout.left_menu_item_layout, mLeftList);
		mListView = (ListView) findViewById(R.id.id_ListView);
		mListView.setAdapter(adapter);// ����������
		mListView.setOnItemClickListener(this);

		mReSet = (Button) findViewById(R.id.reset_id);
		mReSet.setOnClickListener(this);

		mFriend = (Button) findViewById(R.id.friend_button_id);
		mFriend.setOnClickListener(this);

		mMap = (Button) findViewById(R.id.map_button_id);
		mMap.setOnClickListener(this);

		mMessage = (Button) findViewById(R.id.friendMessage_id);
		mMessage.setOnClickListener(this);

		// �����Ƭ
		mFragmentList = new ArrayList<Fragment>();
		// ��������������Ƭ������
		mFragmentList.add(new MessageFragment());// ��Ϣ�б�
		mFragmentList.add(new FriendListFragment());// �����б�
		mFragmentList.add(new MapFragment());// ��ͼ

		mViewpager = (NoScrollViewPagerUtil) findViewById(R.id.ViewPage_id);
		// ����adapter
		mViewpager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),
				mFragmentList));
		mViewpager.setNoScroll(true);// �����Ի���

		mViewpager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
					@Override
					public void onPageScrolled(int position,
							float positionOffset, int positionOffsetPixels) {
						setdate(position);// ����ǰ���±���ɫ��Ϊ��ɫ
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
	 * �����ť�ı��������ɫ
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
	 * ��ʼ���˵�����
	 */
	private void initLeftInf() {
		LeftInformation li1 = new LeftInformation(R.drawable.ic_launcher,
				"������Ϣ");
		mLeftList.add(li1);

		LeftInformation li2 = new LeftInformation(R.drawable.ic_launcher,
				"�ҵ��㼣");
		mLeftList.add(li2);
	}

	@Override
	public void onClick(View view) {
		// ����¼�
		switch (view.getId()) {
		case R.id.friendMessage_id:// ��Ϣ
			mViewpager.setCurrentItem(0);
			setdate(0);
			break;

		case R.id.friend_button_id:// ����
			mViewpager.setCurrentItem(1);
			setdate(1);
			break;

		case R.id.map_button_id:// ��ͼ
			mViewpager.setCurrentItem(2);
			setdate(2);
			break;

		case R.id.reset_id:// ���ð�ť
			new BottomDialog(this) {
			}.show();
			// Toast.makeText(this, "ʵ���߼�", Toast.LENGTH_SHORT).show();
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
