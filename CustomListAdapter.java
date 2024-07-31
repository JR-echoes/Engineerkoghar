package com.engineerkoghar.engineerkoghar;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by User on 11/10/13.
 */
public class CustomListAdapter extends BaseAdapter {
    private ArrayList listData;
    private LayoutInflater layoutInflater;
    private Context mContext;
    private String imageUrl;
    private LinkedHashMap<String, Bitmap> bitmapCache = new LinkedHashMap<String, Bitmap>();

    public CustomListAdapter(Context context, ArrayList listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_row_layout, null);
            holder = new ViewHolder();
            holder.readStateView = (TextView) convertView.findViewById(R.id.readState);
            holder.headlineView = (TextView) convertView.findViewById(R.id.title);
            holder.labelView = (TextView) convertView.findViewById(R.id.label);
            holder.reportedDateView = (TextView) convertView.findViewById(R.id.date);
            holder.imageView = (ImageView) convertView.findViewById(R.id.thumbImage);
            holder.setAttachment = false;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.setAttachment = true;
        }
        FeedItem newsItem = (FeedItem) listData.get(position);
        if (newsItem.getReadState()) {
            holder.readStateView.setVisibility(View.GONE);
        } else {
            holder.readStateView.setVisibility(View.VISIBLE);
            holder.readStateView.setText("NEW");
        }
        holder.headlineView.setText(newsItem.getTitle());
        holder.labelView.setText(newsItem.getLabel());
        holder.reportedDateView.setText(newsItem.getDate().substring(0, 10));
        String thumbUrl = newsItem.getAttachmentUrl();
        if (holder.imageView != null && thumbUrl != null && holder.setAttachment != true) {
            Bitmap bitmap = fetchBitmapFromCache(thumbUrl);
            if (bitmap != null) {
                holder.imageView.setImageBitmap(bitmap);
            } else {
                holder.imageView.setImageResource(R.drawable.placeholder_list);
                new ImageDownloaderTask(holder.imageView).execute(thumbUrl);
            }
        }

/**
 if (newsItem.getAttachmentState()==false&& holder.setAttachment==false) {//holder.imageView != null &&
 holder.imageView.setImageBitmap(null);
 new ImageDownloaderTask(holder.imageView).execute(newsItem.getAttachmentUrl());
 newsItem.setAttachmentState(true);
 }*/
        return convertView;
    }

    private void addBitmapToCache(String url, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (bitmapCache) {
                bitmapCache.put(url, bitmap);
            }
        }
    }

    private Bitmap fetchBitmapFromCache(String url) {

        synchronized (bitmapCache) {
            final Bitmap bitmap = bitmapCache.get(url);
            if (bitmap != null) {
                return bitmap;
            }
        }

        return null;

    }

    static class ViewHolder {
        TextView readStateView;
        TextView headlineView;
        TextView labelView;
        TextView reportedDateView;
        ImageView imageView;
        Boolean setAttachment;
    }
}
