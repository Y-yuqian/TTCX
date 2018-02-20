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
 * ��߲˵�������
 * @author Diviner
 * @date 2018-1-23 ����1:30:33
 */
public class LeftAdapter extends ArrayAdapter<LeftInformation> {

	private int mResourceId;

	/**
	 * �������Ĺ��췽��
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
		LeftInformation li = getItem(position);// ȡ����ǰ��
		View view;// ����һ����ͼ�洢��
		ViewHolder vh;

		if (convertView == null) {
			/*
			 * ���û����ͼ�����һ����ͼ
			 */
			view = LayoutInflater.from(getContext()).inflate(mResourceId, null);
			vh = new ViewHolder();
			/*
			 * ��ʼ���ؼ�
			 */
			vh.mImageView = (ImageView) view.findViewById(R.id.item_img_id);
			vh.mTextView = (TextView) view.findViewById(R.id.item_view_id);

			view.setTag(vh);
		} else {
			/*
			 * �������ͼ��ʱ��
			 */
			view = convertView;// ֱ�Ӱѵ�ǰ��ͼ��ֵ��View
			vh = (ViewHolder) view.getTag();
		}
		vh.mImageView.setImageResource(li.getImage());
		vh.mTextView.setText(li.getName());

		return view;
	}

	/**
	 * ��װ����
	 * @author Diviner
	 * @date 2018-1-23 ����1:30:46
	 */
	class ViewHolder {
		private ImageView mImageView;// ������ϢͼƬ
		private TextView mTextView;// ������Ϣ����
	}
}
