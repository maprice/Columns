package com.example.mprice.mpcolumn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.mprice.mpcolumn.R;
import com.example.mprice.mpcolumn.adapters.ArticleArrayAdapter;
import com.example.mprice.mpcolumn.models.ArticleModel;
import com.example.mprice.mpcolumn.models.SortModel;
import com.example.mprice.mpcolumn.providers.ArticleProvider;
import com.example.mprice.mpcolumn.serializer.ArticleDeserializer;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity implements SortDialogFragment.OnFragmentInteractionListener {

    @Bind(R.id.gvResults)
    GridView gvResults;

    private SortModel mSortModel;

    private ArticleProvider mArticleProvider;
    private ArrayList<ArticleModel> articles;
    private ArticleArrayAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        articles = new ArrayList<>();
        mArticleProvider = new ArticleProvider();
        mAdapter = new ArticleArrayAdapter(this, articles);
        mSortModel = new SortModel();
        gvResults.setAdapter(mAdapter);

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                ArticleModel articleModel = articles.get(position);

                i.putExtra("url", articleModel.webUrl);

                startActivity(i);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            FragmentManager fm = getSupportFragmentManager();
            SortDialogFragment editNameDialog = SortDialogFragment.newInstance(mSortModel);
            editNameDialog.show(fm, "fragment_edit_name");

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                searchForArticle(query);

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void searchForArticle(String query) {


        mArticleProvider.fetchArticle(query, mSortModel, new ArticleProvider.HttpCallback() {
            @Override
            public void onFailure(Response response, Throwable throwable) {

            }

            @Override
            public void onSuccess(Response response) {
                try {
                    String stringResponse = response.body().string();
                    final ArticleDeserializer articleDeserializer = ArticleDeserializer.parseJSON(stringResponse);

                    SearchActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            articles.clear();
                            articles.addAll(articleDeserializer.response.articles);
                            mAdapter.notifyDataSetChanged();
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onSaveSelected(SortModel sortModel) {
        mSortModel = sortModel;
    }
//
//    public void onArticleSearch(View view) {
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//       String query = etQuery.getText().toString();
//        searchForArticle(query);
//
//    }
}
