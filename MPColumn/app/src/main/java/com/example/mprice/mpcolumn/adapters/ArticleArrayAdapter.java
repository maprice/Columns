package com.example.mprice.mpcolumn.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class ArticleArrayAdapter extends RecyclerView.Adapter<ArticleArrayAdapter.ViewHolder> {


    public static interface IOpenArticle {
        public void openArticle(ArticleModel articleModel);
    }

    List<ArticleModel> mArticles;
    IOpenArticle mListener;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_article_result, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView, new ViewHolder.IMyViewHolderClicks() {

            @Override
            public void onArticleClicked(View caller, int position) {
                ArticleModel model = mArticles.get(position);
                mListener.openArticle(model);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ArticleModel model = mArticles.get(position);

        holder.tvHeadline.setText(model.headline.main);
        holder.ivThumbnail.setImageResource(0);

        if (model.thumbnails.size() > 0) {
            holder.tvSnippet.setText("");
            String imageUrl = model.thumbnails.get(0).url;

            Glide.with(holder.ivThumbnail.getContext())
                    .load("http://www.nytimes.com/" + imageUrl)
                    .into(holder.ivThumbnail);
        } else {
            holder.tvSnippet.setText(model.snippet);
        }
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.ivThumbnail)
        ImageView ivThumbnail;

        @Bind(R.id.tvHeadline)
        TextView tvHeadline;

        @Bind(R.id.tvSnippet)
        TextView tvSnippet;

        public IMyViewHolderClicks mListener;

        public ViewHolder(View view,  IMyViewHolderClicks listener) {
            super(view);
            mListener = listener;

            view.setOnClickListener(this);
            ButterKnife.bind(this, view);
        }

        @Override
        public void onClick(View v) {
            mListener.onArticleClicked(v, getAdapterPosition());
        }

        public  interface IMyViewHolderClicks {
             void onArticleClicked(View caller, int position);
        }
    }



    public ArticleArrayAdapter(List<ArticleModel> objects, IOpenArticle listener) {
        mArticles = objects;
        mListener = listener;
    }


}
