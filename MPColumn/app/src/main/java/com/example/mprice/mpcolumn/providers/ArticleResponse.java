package com.example.mprice.mpcolumn.providers;

import com.example.mprice.mpcolumn.models.ArticleModel;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mprice on 2/7/16.
 */

public class ArticleResponse {

    @SerializedName("response")
    public Response response;

    public static class Response {

        public Response() {
            articles = new ArrayList<>();
        }

        @SerializedName("docs")
        public List<ArticleModel> articles;
    }


        public ArticleResponse() {
            response = new Response();
        }

        public static ArticleResponse parseJSON(String response) {
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
            ArticleResponse articleResponse = gson.fromJson(response, ArticleResponse.class);
            return articleResponse;
        }
}
