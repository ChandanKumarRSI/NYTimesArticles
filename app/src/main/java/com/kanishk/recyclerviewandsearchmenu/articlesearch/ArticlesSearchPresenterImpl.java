package com.kanishk.recyclerviewandsearchmenu.articlesearch;

import com.kanishk.recyclerviewandsearchmenu.apiresponse.ResponseContent;

import java.util.ArrayList;
import java.util.List;

public class ArticlesSearchPresenterImpl implements ArticleSearchPresenter,ArticlesRequest.ArticleResponseCallback {

    //region Member variables

    private ArticlesRequest articlesRequest;
    private ArticleSearchView view;
    private List<ResponseContent> articlesList;
    private Integer currentPageNum = -1;
    private String searchText;

    //endregion Member variables

    //region Constructors
    public ArticlesSearchPresenterImpl(ArticleSearchView view) {
        this.view = view;
    }

    public ArticlesSearchPresenterImpl(ArticleSearchView view, List<ResponseContent> articlesList,Integer currentPageNum,String searchText) {
        this.view = view;
        this.articlesList = articlesList;
        this.currentPageNum = currentPageNum;
        this.searchText = searchText;
        this.articlesRequest = new ArticlesRequestImpl(this.searchText,this);
    }
    //endregion Constructors

    //region Getters and Setters
    public ArticlesRequest getArticlesRequest() {
        return articlesRequest;
    }

    public void setArticlesRequest(ArticlesRequest articlesRequest) {
        this.articlesRequest = articlesRequest;
    }

    @Override
    public List<ResponseContent> getArticles() {
        return this.articlesList;
    }

    @Override
    public String getSearchText() {
        return this.articlesRequest.getSearchTerm();
    }

    @Override
    public Integer getCurrentPageNumber() {
        return this.currentPageNum;
    }

    //endregion Getters and Setters

    //region Override methods for interface ArticleSearchPresenter

    @Override
    public void fetchSearchedArticles(String strSearchString) {
        this.searchText = strSearchString;
        this.articlesRequest = new ArticlesRequestImpl(strSearchString,this);
        this.view.startProgress();
        this.currentPageNum = 0;
        this.articlesRequest.searchForArticles(this.currentPageNum);
        this.articlesList = new ArrayList<ResponseContent>();
    }

    @Override
    public void fetchNextPageOfSearchedArticles() {
        if(this.articlesRequest != null) {
            this.currentPageNum++;
            this.articlesRequest.searchForArticles(this.currentPageNum);
        }
    }

    @Override
    public void loadAllSavedArticles() {
        this.view.showAllSavedResults(this.articlesList);
    }

    //endregion Override methods for interface ArticleSearchPresenter

    //region Override ArticleResponseCallback methods

    @Override
    public void onSuccessResponse(List<ResponseContent> articles) {
        this.view.endProgress();
        this.articlesList.addAll(articles);
        if(this.articlesRequest.getPageNum() == 0) {
            if (articles.size() > 0)
                this.view.showFirstPageOfSearchedResults(articles);
            else
                this.view.showNoSearchResultPage();
        } else {
            this.view.showNextPageOfSearchedResults(articles);
        }

    }

    @Override
    public void onFailureResponse(String failureReason) {
        this.view.endProgress();
        this.view.showErrorResponse(failureReason);
    }

    @Override
    public void onErrorResponse(String errorReason) {
        this.view.endProgress();
        this.view.showErrorResponse(errorReason);

    }

    //endregion Override ArticleResponseCallback methods

}
