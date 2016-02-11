package com.example.mprice.mpcolumn.providers;


import com.example.mprice.mpcolumn.models.SortModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mprice on 2/7/16.
 */
public class ArticleProvider {

    private final OkHttpClient client;


    public interface HttpCallback  {
        public void onFailure(Response response, Throwable throwable);
        public void onSuccess(Response response);
    }

    public ArticleProvider() {
         client = new OkHttpClient();
    }

    public void fetchArticle(String query, SortModel sortModel, int offset, final HttpCallback callback) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://api.nytimes.com/svc/search/v2/articlesearch.json").newBuilder();
        urlBuilder.addQueryParameter("api-key", "611aafdd0c335311e4cc4c14a8917c9c:3:74321718");
        urlBuilder.addQueryParameter("q", query);
        urlBuilder.addQueryParameter("page", String.valueOf(offset/10));
        if (sortModel.newDeskSports) {
            urlBuilder.addQueryParameter("fq", "news_desk:(\"Sports\")");
        }

        if (sortModel.order != SortModel.SortOrder.DEFAULT) {
            urlBuilder.addQueryParameter("sort", sortModel.order.toParam());
        }


        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                callback.onFailure(null, e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure(response, new IOException("Unexpected code " + response));
                } else {
                    callback.onSuccess(response);
                }
            }
        });
    }
}
