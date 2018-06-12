package com.kanishk.recyclerviewandsearchmenu.articlesearch;

import com.kanishk.recyclerviewandsearchmenu.apiresponse.ResponseContent;

import java.util.List;

public interface ArticleSearchPresenter {
    public List<ResponseContent> getArticles();
    public String getSearchText();
    public Integer getCurrentPageNumber();
    public void fetchSearchedArticles(String strSearchString);
    public void fetchNextPageOfSearchedArticles();
    public void loadAllSavedArticles();
}
