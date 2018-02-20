package com.example.baidumap;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdust.im.R;

/**
 * 左边菜单适配器
 * @author Diviner
 * @date 2018-1-23 下午1:30:33
 */
public class LeftAdapter extends ArrayAdapter<LeftInformation> {

	private int mResourceId;

	/**
	 * 适配器的构造方法
	 * 
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 */
	public LeftAdapter(Context context, int textViewResourceId,
			List<LeftInformation> objects) {
		super(context, textViewResourceId, objects);
		mResourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LeftInformation li = getItem(position);// 取到当前项
		View view;// 声明一个视图存储器
		ViewHolder vh;

		if (convertView == null) {
			/*
			 * 如果没有视图则加载一个视图
			 */
			view = LayoutInflater.from(getContext()).inflate(mResourceId, null);
			vh = new ViewHolder();
			/*
			 * 初始化控件
			 */
			vh.mImageView = (ImageView) view.findViewById(R.id.item_img_id);
			vh.mTextView = (TextView) view.findViewById(R.id.item_view_id);

			view.setTag(vh);
		} else {
			/*
			 * 如果用视图的时候
			 */
			view = convertView;// 直接把当前视图赋值给View
			vh = (ViewHolder) view.getTag();
		}
		vh.mImageView.setImageResource(li.getImage());
		vh.mTextView.setText(li.getName());

		return view;
	}

	/**
	 * 组装数据
	 * @author Diviner
	 * @date 2018-1-23 下午1:30:46
	 */
	class ViewHolder {
		private ImageView mImageView;// 歌曲信息图片
		private TextView mTextView;// 歌曲信息内容
	}
}
