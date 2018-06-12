package com.kanishk.recyclerviewandsearchmenu.articlesearch;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kanishk.recyclerviewandsearchmenu.ApplicationContextProvider;
import com.kanishk.recyclerviewandsearchmenu.apiresponse.ArticleSearchResponse;
import com.kanishk.recyclerviewandsearchmenu.apiresponse.ResponseContent;
import com.kanishk.recyclerviewandsearchmenu.apis.ArticleSearchAPI;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArticlesRequestImpl implements ArticlesRequest, retrofit2.Callback<ArticleSearchResponse> {

    //region Constants
    static final String BASE_URL = "https://api.nytimes.com/";
    //endregion Constants

    //region Member variables
    private String searchTerm;
    private Integer pageNum;
    private ArticleSearchAPI articleSearchAPI;
    private Retrofit retrofit;
    private Gson gson;
    private ArticleResponseCallback callback;
    //endregion Member variables

    //region Constructors
    public ArticlesRequestImpl() {
        this.gson = new GsonBuilder()
                .setLenient()
                .create();

        this.retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        this.callback = new DummyCallback();
    }

    public ArticlesRequestImpl(String searchTerm) {
        this();
        this.searchTerm = searchTerm;
    }

    public ArticlesRequestImpl(String searchTerm, ArticleResponseCallback callback) {
        this(searchTerm);
        this.callback = callback;
    }
    //endregion Constructors

    //region Getters and Setters
    public void setCallback(ArticleResponseCallback callback) {
        if (callback != null)
            this.callback = callback;
        else
            this.callback = new DummyCallback();
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    @Override
    public Integer getPageNum() {
        return pageNum;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
    //endregion Getters and Setters

    //region Override methods for ArticlesRequest interface
    @Override
    public void searchForArticles(Integer pageNumber) {
        this.pageNum = pageNumber;
        this.articleSearchAPI = this.retrofit.create(ArticleSearchAPI.class);
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ApplicationContextProvider.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            Call<ArticleSearchResponse> call = this.articleSearchAPI.loadData(this.searchTerm, pageNumber);
            call.enqueue(this);
        } else {
            callback.onFailureResponse("No internet connection");
        }

    }
    //endregion Override methods for ArticlesRequest interface

    //region Override methods for retrofit2.Callback
    @Override
    public void onResponse(Call<ArticleSearchResponse> call, Response<ArticleSearchResponse> response) {
        if (response.isSuccessful()) {
            ArticleSearchResponse articleSearchResponse = response.body();
            if (articleSearchResponse == null) {
                callback.onFailureResponse("No response");
            } else if (articleSearchResponse.getResponse() == null) {
                callback.onFailureResponse("No response body");
            } else if (articleSearchResponse.getResponse().getDocs() == null) {
                callback.onFailureResponse("No articles found for search string : " + this.searchTerm);
            } else {
                List<ResponseContent> responseContentList = articleSearchResponse.getResponse().getDocs();
                callback.onSuccessResponse(responseContentList); }
        } else {
            try {
                callback.onFailureResponse(response.errorBody().string());
            } catch (IOException e) {
                callback.onFailureResponse(e.getMessage());
            }
        }
    }

    @Override
    public void onFailure(Call<ArticleSearchResponse> call, Throwable t) {
        if (callback != null)
            callback.onErrorResponse("Failure");
    }

    //endregion Override methods for retrofit2.Callback

    //region Dummy Callback
    class DummyCallback implements ArticleResponseCallback {


        @Override
        public void onSuccessResponse(List<ResponseContent> articles) {

        }

        @Override
        public void onFailureResponse(String failureReason) {

        }

        @Override
        public void onErrorResponse(String errorReason) {

        }
    }

    //endregion Dummy Callback
}
