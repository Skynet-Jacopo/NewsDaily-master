package com.example.liuqun.newsdaily.ui.adapter;

import com.example.liuqun.newsdaily.R;
import com.example.liuqun.newsdaily.common.LoadImage;
import com.example.liuqun.newsdaily.model.entity.News;
import com.example.liuqun.newsdaily.ui.base.MyBaseAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NewsAdapter extends MyBaseAdapter<News> {
	private Bitmap defaultBitmap;
	private LoadImage loadImage;
	private ListView listView;

	public NewsAdapter(Context context, ListView listView) {
		super(context);
		defaultBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.defaultpic);
		loadImage = new LoadImage(context, listener);
		this.listView = listView;
	}

	@Override
	public View getMyView(int position, View convertView, ViewGroup parent) {
		HoldView holdView = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_list_news, null);
			holdView = new HoldView(convertView);
			convertView.setTag(holdView);
		} else {
			holdView = (HoldView) convertView.getTag();
		}
		News news = myList.get(position);
		holdView.tv_title.setText(news.getTitle());
		holdView.tv_text.setText(news.getSummary());
		holdView.tv_from.setText(news.getStamp());
		holdView.iv_icon.setImageBitmap(defaultBitmap);// 默认图片

		String url = news.getIcon();
		holdView.iv_icon.setTag(url);
		loadImage.geBitmap(url, holdView.iv_icon);

		return convertView;
	}

	public class HoldView {
		public ImageView iv_icon;
		public TextView tv_title;
		public TextView tv_text;
		public TextView tv_from;

		public HoldView(View view) {
			iv_icon = (ImageView) view.findViewById(R.id.imageView1);
			tv_title = (TextView) view.findViewById(R.id.textView1);
			tv_text = (TextView) view.findViewById(R.id.textView2);
			tv_from = (TextView) view.findViewById(R.id.textView3);
		}
	}

	private LoadImage.ImageLoadListener listener = new LoadImage.ImageLoadListener() {

		@Override
		public void imageLoadOk(Bitmap bitmap, String url) {
			ImageView iv = (ImageView) listView.findViewWithTag(url);
			if (iv != null)
				iv.setImageBitmap(bitmap);
		}
	};
}
