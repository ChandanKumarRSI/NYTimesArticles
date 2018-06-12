package com.kanishk.recyclerviewandsearchmenu.articlesearch;

import com.kanishk.recyclerviewandsearchmenu.apiresponse.ResponseContent;

import java.util.List;

public interface ArticleSearchView {
    public void showAllSavedResults(List<ResponseContent> responseContents);
    public void showFirstPageOfSearchedResults(List<ResponseContent> responseContents);
    public void startProgress();
    public void endProgress();
    public void showNextPageOfSearchedResults(List<ResponseContent> responseContents);
    public void showNoSearchResultPage();
    public void showErrorResponse(String errorMessage);
    public void showNoArticleMessage();
}
