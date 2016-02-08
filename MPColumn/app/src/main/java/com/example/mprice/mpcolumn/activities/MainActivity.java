package com.example.mprice.mpcolumn.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.example.mprice.mpcolumn.R;
import com.example.mprice.mpcolumn.adapters.ArticleArrayAdapter;
import com.example.mprice.mpcolumn.models.ArticleModel;
import com.example.mprice.mpcolumn.providers.ArticleProvider;
import com.example.mprice.mpcolumn.providers.ArticleResponse;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.btnSearch)
    Button btnSearch;

    @Bind(R.id.etQuery)
    EditText etQuery;

    @Bind(R.id.gvResults)
    GridView gvResults;

    private ArticleProvider mArticleProvider;
    private ArrayList<ArticleModel> articles;
    private ArticleArrayAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        articles = new ArrayList<>();
        mArticleProvider = new ArticleProvider();
        mAdapter = new ArticleArrayAdapter(this, articles);

        gvResults.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(View view) {

        String query = etQuery.getText().toString();

        mArticleProvider.fetchArticle(query, new ArticleProvider.HttpCallback() {
            @Override
            public void onFailure(Response response, Throwable throwable) {

            }

            @Override
            public void onSuccess(Response response) {
                try {
                    String stringResponse = response.body().string();
                   final ArticleResponse articleResponse = ArticleResponse.parseJSON(stringResponse);

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            articles.clear();
                            articles.addAll(articleResponse.response.articles);
                            mAdapter.notifyDataSetChanged();
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
