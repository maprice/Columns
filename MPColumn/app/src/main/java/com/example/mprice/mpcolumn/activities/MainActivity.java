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
import com.example.mprice.mpcolumn.models.ArticleModel;
import com.example.mprice.mpcolumn.providers.ArticleProvider;
import com.example.mprice.mpcolumn.providers.ArticleResponse;

import java.io.IOException;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mArticleProvider = new ArticleProvider();
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

        String query = btnSearch.getText().toString();

        mArticleProvider.fetchArticle(query, new ArticleProvider.HttpCallback() {
            @Override
            public void onFailure(Response response, Throwable throwable) {

            }

            @Override
            public void onSuccess(Response response) {
                try {
                    String stringResponse = response.body().string();
                    ArticleResponse articleResponse = ArticleResponse.parseJSON(stringResponse);

                    List<ArticleModel> articles = articleResponse.articles;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
