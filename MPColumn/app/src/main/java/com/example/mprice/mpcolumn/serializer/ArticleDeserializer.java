package com.example.mprice.mpcolumn.serializer;

import com.example.mprice.mpcolumn.models.ArticleModel;
import com.google.gson.Gson;

import java.util.ArrayList;

import okhttp3.Response;

/**
 * Created by mprice on 2/7/16.
 */
public class ArticleDeserializer {
    public static ArticleModel deserializeToArticleModel(Response response) {

      Gson gson = new Gson();

        ArticleModel model = gson.fromJson(response.body().charStream(), ArticleModel.class);


        return model;
    }

    public static ArrayList<ArticleModel> deserializeToArticleModels(Response response) {

        return null;
    }

}
