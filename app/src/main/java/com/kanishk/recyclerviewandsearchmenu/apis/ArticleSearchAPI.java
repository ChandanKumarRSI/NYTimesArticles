package com.kanishk.recyclerviewandsearchmenu.apis;

import com.kanishk.recyclerviewandsearchmenu.apiresponse.ArticleSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ArticleSearchAPI {
    //region Constants
    static final String API_KEY = "d31fe793adf546658bd67e2b6a7fd11a";
    static final String FIELDS = "web_url, headline, multimedia";
    static final String BASE_URL = "https://api.nytimes.com/";
    //endregion Constants

    //region API call signature
    @GET("svc/search/v2/articlesearch.json?api-key="+ API_KEY + "&" + "fl=" + FIELDS)
    Call<ArticleSearchResponse> loadData(@Query("q") String searchString, @Query("page") Integer pageNum );
    //endregion API call signature
}
