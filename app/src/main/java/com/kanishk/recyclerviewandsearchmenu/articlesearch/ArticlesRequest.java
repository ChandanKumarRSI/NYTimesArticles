package com.kanishk.recyclerviewandsearchmenu.articlesearch;

import com.kanishk.recyclerviewandsearchmenu.apiresponse.ResponseContent;

import java.util.List;

public interface ArticlesRequest {
    public String getSearchTerm();
    public Integer getPageNum();
    public void searchForArticles(Integer pageNumber);
    public interface ArticleResponseCallback {
        public void onSuccessResponse(List<ResponseContent> articles);
        public void onFailureResponse(String failureReason);
        public void onErrorResponse(String errorReason);
    }
}
