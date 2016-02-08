package com.example.mprice.mpcolumn.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mprice.mpcolumn.R;
import com.example.mprice.mpcolumn.models.ArticleModel;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mprice on 2/7/16.
 */
public class ArticleArrayAdapter extends ArrayAdapter<ArticleModel> {

    public static class ViewHolder {
        @Bind(R.id.ivThumbnail)
        ImageView ivThumbnail;

        @Bind(R.id.tvHeadline)
        TextView tvHeadline;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public ArticleArrayAdapter(Context context, List<ArticleModel> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ArticleModel articleModel = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_article_result, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
        viewHolder.tvHeadline.setText(articleModel.headline.main);

        if (articleModel.thumbnails.size() > 0) {
            String imageUrl = articleModel.thumbnails.get(0).url;
            viewHolder.ivThumbnail.setImageResource(0);
            Glide.with(getContext())
                    .load("http://www.nytimes.com/" + imageUrl)
                    .into(viewHolder.ivThumbnail);
        }
        return convertView;
    }
}
