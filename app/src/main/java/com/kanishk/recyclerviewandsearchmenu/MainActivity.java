package com.kanishk.recyclerviewandsearchmenu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kanishk.recyclerviewandsearchmenu.apiresponse.ResponseContent;
import com.kanishk.recyclerviewandsearchmenu.articledetails.ArticleDetails;
import com.kanishk.recyclerviewandsearchmenu.articlesearch.ArticleSearchPresenter;
import com.kanishk.recyclerviewandsearchmenu.articlesearch.ArticleSearchView;
import com.kanishk.recyclerviewandsearchmenu.articlesearch.ArticlesAdapter;
import com.kanishk.recyclerviewandsearchmenu.articlesearch.ArticlesSearchPresenterImpl;
import com.kanishk.recyclerviewandsearchmenu.interfaces.OnBottomReachedListener;
import com.kanishk.recyclerviewandsearchmenu.interfaces.RecyclerViewItemClickListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements ArticleSearchView, SearchView.OnQueryTextListener, OnBottomReachedListener, RecyclerViewItemClickListener {

    //region UI Elements Member variables

    private RecyclerView rv_articles;
    private ProgressBar progressBar;
    private TextView tv_Message;

    //endregion UI Elements

    //region Member variables
    private List<ResponseContent> responseContentList;

    private ArticleSearchPresenter presenter;
    private ArticlesAdapter articlesAdapter;

    //endregion Member variables
    static final String SHARED_PREF_ARTICLE_KEY = "article";
    static final String SHARED_PREF_SAVED_ARTICLES_KEY = "SAVED_ARTICLES";
    static final String SHARED_PREF_SAVED_SEARCH_TEXT_KEY = "SAVED_SEARCH_TEXT";
    static final String SHARED_PREF_SAVED_CURRENT_PAGE_KEY = "SAVED_CURRENT_PAGE";
    //region Constants


    //endregion Constants

    //region Overriding Activity Lifescycle methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv_articles = (RecyclerView) findViewById(R.id.rv_articles);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv_Message = (TextView) findViewById(R.id.tv_Message);
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        rv_articles.setLayoutManager(linearLayoutManager);
        rv_articles.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rv_articles.addItemDecoration(itemDecor);
        checkSavedStateAndLoadInitialData();
//        SharedPreferences savedArticlesPrefs = getSharedPreferences("articles", MODE_PRIVATE);
//        if (savedArticlesPrefs != null) {
//            String savedArticlesJSON = savedArticlesPrefs.getString("SAVED_ARTICLES", null);
//            String savedSearchText = savedArticlesPrefs.getString("SAVED_SEARCH_TEXT", null);
//            Integer savedPageNumber = savedArticlesPrefs.getInt("SAVED_CURRENT_PAGE",-1);
//            if (savedArticlesJSON != null && savedPageNumber != -1) {
//                List<String> savedArticlesList = new Gson().fromJson(savedArticlesJSON, List.class);
//                List<ResponseContent> savedArticlesListNew = new ArrayList<ResponseContent>();
//                savedArticlesList.forEach(item -> {
//                    savedArticlesListNew.add(new Gson().fromJson(item, ResponseContent.class));
//                });
//                this.presenter = new ArticlesSearchPresenterImpl(this,savedArticlesListNew, savedPageNumber,savedSearchText);
//                this.presenter.loadAllSavedArticles();
//            } else {
//                this.presenter = new ArticlesSearchPresenterImpl(this);
//                this.presenter.fetchSearchedArticles("");
//            }
//        }else {
//            this.presenter = new ArticlesSearchPresenterImpl(this);
//            this.presenter.fetchSearchedArticles("");
//        }
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SharedPreferences savedArticlesPrefs = getSharedPreferences("articles", MODE_PRIVATE);

        SharedPreferences.Editor edit = savedArticlesPrefs.edit();
        List<String> savedArticlesListNew = new ArrayList<String>();
        this.presenter.getArticles().forEach(item -> {
            savedArticlesListNew.add(new Gson().toJson(item));
        });
        edit.putString("SAVED_ARTICLES", new Gson().toJson(savedArticlesListNew));
        edit.putString("SAVED_SEARCH_TEXT", this.presenter.getSearchText());
        edit.putInt("SAVED_CURRENT_PAGE", this.presenter.getCurrentPageNumber());
        edit.apply();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = super.getMenuInflater();
        menuInflater.inflate(R.menu.search_menu,menu);
        MenuItem searchItem = menu.findItem(R.id.searchItem);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    //endregion Overriding Activity Lifescycle methods

    //region Override methods for SearchView.OnQueryTextListener interface

    @Override
    public boolean onQueryTextSubmit(String query) {
        this.presenter.fetchSearchedArticles(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    //endregion Override methods for SearchView.OnQueryTextListener interface

    //region Override methods for OnBottomReachedListener interface

    @Override
    public void onBottomReached(int position) {
        this.presenter.fetchNextPageOfSearchedArticles();
    }

    //endregion Override methods for OnBottomReachedListener interface

    //region Override methods for ArticleView interface

    @Override
    public void showAllSavedResults(List<ResponseContent> responseContents) {
        this.progressBar.setVisibility(View.INVISIBLE);
        this.tv_Message.setVisibility(View.INVISIBLE);
        this.rv_articles.setVisibility(View.VISIBLE);
        this.articlesAdapter = new ArticlesAdapter(responseContents);
        this.rv_articles.setAdapter(this.articlesAdapter);
        this.articlesAdapter.setOnBottomReachedListener(this);
        this.articlesAdapter.setOnItemClickListener(this);
    }

    @Override
    public void showFirstPageOfSearchedResults(List<ResponseContent> responseContents) {
        this.progressBar.setVisibility(View.INVISIBLE);
        this.tv_Message.setVisibility(View.INVISIBLE);
        this.rv_articles.setVisibility(View.VISIBLE);
        this.articlesAdapter = new ArticlesAdapter(responseContents);
        this.rv_articles.setAdapter(this.articlesAdapter);
        this.articlesAdapter.setOnBottomReachedListener(this);
        this.articlesAdapter.setOnItemClickListener(this);
    }

    @Override
    public void startProgress() {
        this.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void endProgress() {
        this.progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showNextPageOfSearchedResults(List<ResponseContent> responseContents) {
        ((ArticlesAdapter)rv_articles.getAdapter()).updateList(responseContents);
    }

    @Override
    public void showNoSearchResultPage() {
        this.tv_Message.setVisibility(View.VISIBLE);
        this.rv_articles.setVisibility(View.INVISIBLE);
        this.tv_Message.setText("No search results found");
    }

    @Override
    public void showErrorResponse(String errorMessage) {
//        this.tv_Message.setVisibility(View.VISIBLE);
//        this.rv_articles.setVisibility(View.INVISIBLE);
//        this.tv_Message.setText(errorMessage);
        Toast.makeText(this,errorMessage,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoArticleMessage() {
        Toast.makeText(this,"No details available for this article",Toast.LENGTH_SHORT).show();
    }

    //endregion Override methods for ArticleView interface

    //region Override methods for RecyclerViewItemClickListener interface
    @Override
    public void onItemClick(int position, View v) {
        List<ResponseContent> articles = this.presenter.getArticles();
        if (articles != null && articles.size() > position) {
            ResponseContent article = articles.get(position);
            Intent articleDetailIntent = new Intent(this, ArticleDetails.class);
            articleDetailIntent.putExtra("ARTICLE_HEADLINE",article.getHeadline().getMain());
            articleDetailIntent.putExtra("ARTICLE_WEB_PAGE",article.getWeb_url());
            startActivity(articleDetailIntent);
        }
    }

    @Override
    public void onItemLongClick(int position, View v) {

    }

    //endregion Override methods for RecyclerViewItemClickListener interface

    //region Miscellaneous methods

    public static boolean hasPermissions(Context context, String... permissions)
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null)
        {
            for (String permission : permissions)
            {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
        }
        return true;
    }


    //endregion Miscellaneous methods

    //region Private Helper methods

    private void checkSavedStateAndLoadInitialData(){
        SharedPreferences savedArticlesPrefs = getSharedPreferences(SHARED_PREF_ARTICLE_KEY, MODE_PRIVATE);
        ArticleSearchPresenter savedPresenter = null;
        if (savedArticlesPrefs != null) {
            String savedArticlesJSON = savedArticlesPrefs.getString(SHARED_PREF_SAVED_ARTICLES_KEY, null);
            String savedSearchText = savedArticlesPrefs.getString(SHARED_PREF_SAVED_SEARCH_TEXT_KEY, null);
            Integer savedPageNumber = savedArticlesPrefs.getInt(SHARED_PREF_SAVED_CURRENT_PAGE_KEY,-1);
            if (savedArticlesJSON != null && savedPageNumber != -1) {
                List<String> savedArticlesList = new Gson().fromJson(savedArticlesJSON, List.class);
                List<ResponseContent> savedArticlesListNew = new ArrayList<ResponseContent>();
                savedArticlesList.forEach(item -> {
                    savedArticlesListNew.add(new Gson().fromJson(item, ResponseContent.class));
                });
                presenter = new ArticlesSearchPresenterImpl(this,savedArticlesListNew, savedPageNumber,savedSearchText);
            }
        }
        if (savedPresenter != null) {
            this.presenter = savedPresenter;
            this.presenter.loadAllSavedArticles();
        } else {
            this.presenter = new ArticlesSearchPresenterImpl(this);
            this.presenter.fetchSearchedArticles("");
        }
    }

    //endregion Private Helper methods

}
