package com.example.liuqun.newsdaily.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.liuqun.newsdaily.R;
import com.example.liuqun.newsdaily.model.entity.Comment;
import com.example.liuqun.newsdaily.ui.base.MyBaseAdapter;

/**
 * Created by 90516 on 6/3/2016.
 */
public class CommentsAdapter extends MyBaseAdapter<Comment> {

    private ListView listView;

    public CommentsAdapter(Context context, ListView listView) {
        super(context);
        this.listView = listView;
    }

    @Override
    public View getMyView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView =inflater.inflate(R.layout.item_list_comment,null);
            viewHolder =new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Comment comment =myList.get(position);

        viewHolder.tv_comment.setText(comment.getContent());
        viewHolder.tv_time.setText(comment.getStamp());
        viewHolder.tv_user.setText(comment.getUid());
        return convertView;
    }
    public class ViewHolder{
        public ImageView iv_list_image;
        public TextView tv_user;
        public TextView tv_time;
        public TextView tv_comment;

        public ViewHolder(View view) {
            iv_list_image = (ImageView) view.findViewById(R.id.imageView1);
            tv_user = (TextView) view.findViewById(R.id.textView2);
            tv_time = (TextView) view.findViewById(R.id.textView3);
            tv_comment = (TextView) view.findViewById(R.id.textView1);
        }
    }
}
