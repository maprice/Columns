package com.example.mprice.mpcolumn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.example.mprice.mpcolumn.R;
import com.example.mprice.mpcolumn.adapters.ArticleArrayAdapter;
import com.example.mprice.mpcolumn.models.ArticleModel;
import com.example.mprice.mpcolumn.models.SortModel;
import com.example.mprice.mpcolumn.providers.ArticleProvider;
import com.example.mprice.mpcolumn.serializer.ArticleDeserializer;
import com.example.mprice.mpcolumn.utils.EndlessRecyclerViewScrollListener;
import com.example.mprice.mpcolumn.utils.HidingScrollListener;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity implements SortDialogFragment.OnFragmentInteractionListener, ArticleArrayAdapter.IOpenArticle {

    public static class ToolBarHider extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

    }

    @Bind(R.id.rvResults)
    RecyclerView rvResults;

    Toolbar mToolbar;
    private LinearLayout mToolbarContainer;
    private SortModel mSortModel;
    private String lastQuery;
    private ArticleProvider mArticleProvider;
    private ArrayList<ArticleModel> articles;
    private ArticleArrayAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);







        mToolbarContainer = (LinearLayout) findViewById(R.id.toolbarContainer);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        articles = new ArrayList<>();
        mArticleProvider = new ArticleProvider();
        mAdapter = new ArticleArrayAdapter(articles, this);
        mSortModel = new SortModel();
        rvResults.setAdapter(mAdapter);

        rvResults.setOnScrollListener(new HidingScrollListener(this) {
            @Override
            public void onMoved(int distance) {
                mToolbarContainer.setTranslationY(-distance);
            }
        });

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        rvResults.setLayoutManager(gridLayoutManager);

        rvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                customLoadMoreDataFromApi(page);
            }
        });

    }

    public void customLoadMoreDataFromApi(int offset) {
        // Send an API request to retrieve appropriate data using the offset value as a parameter.
        // Deserialize API response and then construct new objects to append to the adapter
        // Add the new objects to the data source for the adapter
        mArticleProvider.fetchArticle(lastQuery, mSortModel, offset, new ArticleProvider.HttpCallback() {
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


                            articles.addAll(articleDeserializer.response.articles);
                            // For efficiency purposes, notify the adapter of only the elements that got changed
                            // curSize will equal to the index of the first element inserted because the list is 0-indexed
                            int curSize = mAdapter.getItemCount();
                            mAdapter.notifyItemRangeInserted(curSize, articles.size() - 1);


                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }
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
        lastQuery = query;
        mArticleProvider.fetchArticle(query, mSortModel, 0, new ArticleProvider.HttpCallback() {
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

    @Override
    public void openArticle(ArticleModel articleModel) {
        Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
        i.putExtra("url", articleModel.webUrl);
        startActivity(i);
    }

    boolean isShowing = true;


//    @Override
//    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//        float y = ((RecyclerView)findViewById(R.id.rvResults)).getScrollY();
//        if (y >= mToolbar.getHeight() && isShowing) {
//            isShowing = false;
//            mToolbar.animate().translationY(-mToolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
//
//        } else if ( y==0 && !isShowing) {
//            isShowing = true;
//            mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
//
//        }
//    }
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
