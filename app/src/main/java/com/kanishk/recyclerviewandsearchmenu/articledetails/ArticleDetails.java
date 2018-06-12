package com.kanishk.recyclerviewandsearchmenu.articledetails;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kanishk.recyclerviewandsearchmenu.ApplicationContextProvider;
import com.kanishk.recyclerviewandsearchmenu.R;

public class ArticleDetails extends AppCompatActivity {

    //region Private Member variables
    private WebView wv_ArticleDetail;
    private ProgressBar progressBar;
    private String webURL;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private String headline;
    //endregion Private Member variables

    //region Life cycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.wv_ArticleDetail = (WebView) findViewById(R.id.wv_ArticleDetail);
        this.progressBar = (ProgressBar) findViewById(R.id.newProgressBar);
        //setContentView(this.wv_ArticleDetail);
        webURL = getIntent().getStringExtra("ARTICLE_WEB_PAGE");
        headline = getIntent().getStringExtra("ARTICLE_HEADLINE");
        getSupportActionBar().setTitle(headline);

        ConnectivityManager connectivityManager
                = (ConnectivityManager) ApplicationContextProvider.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo != null && activeNetworkInfo.isConnected()) {

            if (webURL != null && !webURL.trim().equals("")) {
                this.wv_ArticleDetail.setWebViewClient(new WebViewClient() {

                    //Show loader on url load
                    public void onLoadResource(final WebView view, String url) {
                        //progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    //Hide loader on page load complete
                    public void onPageFinished(WebView view, String url) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        super.onReceivedError(view, request, error);
                        progressBar.setVisibility(View.GONE);
                    }

                });
                this.wv_ArticleDetail.getSettings().setLoadWithOverviewMode(true);
                this.wv_ArticleDetail.getSettings().setUseWideViewPort(true);
                this.wv_ArticleDetail.loadUrl(webURL);
            }


            mySwipeRefreshLayout.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            wv_ArticleDetail.reload();
                            mySwipeRefreshLayout.setRefreshing(false);

                        }
                    }
            );
        } else {
            Toast.makeText(this,"No internet connection",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = super.getMenuInflater();
        menuInflater.inflate(R.menu.details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                return true;

            case R.id.share:
                shareURL();
                return true;
        }
        return true;
    }
    //endregion Life cycle Methods

    //region Private Helper Methods
    private void shareURL() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = webURL;
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }
    //endregion Private Helper Methods
}
