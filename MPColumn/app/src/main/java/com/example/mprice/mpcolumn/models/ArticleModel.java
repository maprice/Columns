package com.example.mprice.mpcolumn.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mprice on 2/7/16.
 */
public class ArticleModel {

    @SerializedName("web_url")
    public String webUrl;

    @SerializedName("headline")
    public ArticleHeadline headline;

    @SerializedName("multimedia")
    public List<ArticleThumbnail> thumbnails;

    public ArticleModel() {
        thumbnails = new ArrayList<>();
    }

}
