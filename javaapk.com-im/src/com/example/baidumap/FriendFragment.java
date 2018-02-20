package com.example.baidumap;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdust.im.R;

/**
 * ºÃÓÑÄ£¿éËéÆ¬
 * @author Diviner
 * @date 2018-1-19 ÏÂÎç3:26:30
 */
public class FriendFragment extends Fragment {
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.viewpage_friend_layout,
				container, false);
		return view;
	}
}
