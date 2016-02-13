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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.mprice.mpcolumn.R;
import com.example.mprice.mpcolumn.adapters.ArticleArrayAdapter;
import com.example.mprice.mpcolumn.models.ArticleModel;
import com.example.mprice.mpcolumn.models.SortModel;
import com.example.mprice.mpcolumn.providers.ArticleProvider;
import com.example.mprice.mpcolumn.serializer.ArticleDeserializer;
import com.example.mprice.mpcolumn.utils.EndlessRecyclerViewScrollListener;
import com.example.mprice.mpcolumn.utils.HidingScrollListener;
import com.example.mprice.mpcolumn.utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import okhttp3.Response;

/**
 * Created by mprice on 2/12/16.
 */
public class SearchActivity extends AppCompatActivity implements SortDialogFragment.OnFragmentInteractionListener, ArticleArrayAdapter.IOpenArticle {

    @Bind(R.id.rvResults)
    RecyclerView rvResults;

    @Bind(R.id.svNull_state)
    ScrollView svNull_state;

    private Toolbar mToolbar;
    private LinearLayout mToolbarContainer;

    private String lastQuery;
    private SortModel mSortModel;
    private ArticleProvider mArticleProvider;
    private ArrayList<ArticleModel> articles;
    private ArticleArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        svNull_state.setVisibility(View.VISIBLE);
        rvResults.setVisibility(View.GONE);

        mToolbarContainer = (LinearLayout) findViewById(R.id.toolbarContainer);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        articles = new ArrayList<>();
        mArticleProvider = new ArticleProvider();
        mAdapter = new ArticleArrayAdapter(articles, this);
        mSortModel = new SortModel();

        setupRecyclerView();
    }

    private void customLoadMoreDataFromApi(int offset) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            NetworkUtils.showNetworkError(getApplicationContext());
            return;
        }

        mArticleProvider.fetchArticle(lastQuery, mSortModel, offset, new ArticleProvider.HttpCallback() {
            @Override
            public void onFailure(Response response, Throwable throwable) {
                NetworkUtils.showNetworkError(getApplicationContext());
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
        int id = item.getItemId();

        if (id == R.id.action_filter) {
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
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchForArticle(query);
                searchView.clearFocus();
                searchItem.collapseActionView();

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
        if (!NetworkUtils.isNetworkAvailable(this)) {
          NetworkUtils.showNetworkError(getApplicationContext());
            return;
        }
        lastQuery = query;
        int start = articles.size();
        articles.clear();
        if (start > 0) {
            mAdapter.notifyItemRangeRemoved(0, start);
        }

        mArticleProvider.fetchArticle(query, mSortModel, 0, new ArticleProvider.HttpCallback() {
            @Override
            public void onFailure(Response response, Throwable throwable) {
                if (response != null) {
                    Log.e("NetworkError", response.toString());
                }
                SearchActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        NetworkUtils.showSearchError(getApplicationContext());
                    }
                });
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

                            mAdapter.notifyItemRangeInserted(0, articles.size());

                            svNull_state.setVisibility(View.GONE);
                            rvResults.setVisibility(View.VISIBLE);
                            rvResults.scrollToPosition(0);
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
        setFilterIcon(true);
    }

    @Override
    public void onResetSelected() {
        mSortModel = new SortModel();
        setFilterIcon(false);
    }

    @Override
    public void openArticle(ArticleModel articleModel) {
        Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
        i.putExtra("url", articleModel.webUrl);
        startActivity(i);
    }

    public void onCategoryClick(View view) {
        int ID = view.getId();
        if (ID == R.id.ivArts) {
            mSortModel.newDeskArts = true;
        } else if (ID == R.id.ivSports) {
            mSortModel.newDeskSports = true;
        } else if (ID == R.id.ivScience) {
            mSortModel.newDeskScience = true;
        } else if (ID == R.id.ivTechnology) {
            mSortModel.newDeskTechnology = true;
        } else if (ID == R.id.ivWorld) {
            mSortModel.newDeskWorld = true;
        }

        setFilterIcon(true);
        searchForArticle("");
    }

    private void setFilterIcon(boolean full) {
        int drawableId = full ? R.drawable.filter_full : R.drawable.filter_empty;
        mToolbar.getMenu().getItem(1).setIcon(getResources().getDrawable(drawableId));
    }

    private void setupRecyclerView() {
        // Recycler View Setup
        ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(mAdapter);
        alphaAdapter.setFirstOnly(false);
        rvResults.setAdapter(alphaAdapter);

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
}
