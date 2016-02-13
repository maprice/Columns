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

    private final static String NYTIMES_URL = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
    private final static String NYTIMES_API_KEY = "api-key";
    private final static String NYTIMES_API_VALUE = "611aafdd0c335311e4cc4c14a8917c9c:3:74321718";

    private final static String NYTIMES_QUERY_KEY = "q";
    private final static String NYTIMES_PAGE_KEY = "page";
    private final static String NYTIMES_BEGIN_DATE_KEY = "begin_date";
    private final static String NYTIMES_SORT_KEY = "sort";

    private final OkHttpClient client;

    public interface HttpCallback {
        void onFailure(Response response, Throwable throwable);

        void onSuccess(Response response);
    }

    public ArticleProvider() {
        client = new OkHttpClient();
    }

    public void fetchArticle(String query, SortModel sortModel, int offset, final HttpCallback callback) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(NYTIMES_URL).newBuilder();
        // Api
        urlBuilder.addQueryParameter(NYTIMES_API_KEY, NYTIMES_API_VALUE);
        // Query
        if (!query.isEmpty()) {
            urlBuilder.addQueryParameter(NYTIMES_QUERY_KEY, query);
        }
        //Page
        urlBuilder.addQueryParameter(NYTIMES_PAGE_KEY, String.valueOf(offset));
        // Filter
        addParameterFilterQuery(sortModel, urlBuilder);
        // Date
        addParameterBeginDate(sortModel, urlBuilder);
        // Order
        if (sortModel.order != SortModel.SortOrder.DEFAULT) {
            urlBuilder.addQueryParameter(NYTIMES_SORT_KEY, sortModel.order.toParam());
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


    private void addParameterBeginDate(SortModel sortModel, HttpUrl.Builder urlBuilder) {
        if (sortModel.beginDateYear == 1970) {
            return;
        }
        String year = String.valueOf(sortModel.beginDateYear);
        String month = String.valueOf(sortModel.beginDateMonth);
        String day = String.valueOf(sortModel.beginDateDay);

        if (month.length() == 1) {
            month = "0" + month;
        }
        if (day.length() == 1) {
            day = "0" + day;
        }

        String beginDate = year + month + day;
        urlBuilder.addQueryParameter(NYTIMES_BEGIN_DATE_KEY, beginDate);
    }

    private void addParameterFilterQuery(SortModel sortModel, HttpUrl.Builder urlBuilder) {
        StringBuilder tmp = new StringBuilder();

        if (sortModel.newDeskArts) {
            tmp.append("\"Arts\" ");
        }
        if (sortModel.newDeskSports) {
            tmp.append("\"Sports\" ");
        }
        if (sortModel.newDeskScience) {
            tmp.append("\"Science\" ");
        }
        if (sortModel.newDeskTechnology) {
            tmp.append("\"Technology\" ");
        }
        if (sortModel.newDeskWorld) {
            tmp.append("\"World\" ");
        }

        if (!tmp.toString().isEmpty()) {
            tmp.insert(0, "news_desk:(");
            tmp.append(")");
            urlBuilder.addQueryParameter("fq", tmp.toString());
        }
    }
}
