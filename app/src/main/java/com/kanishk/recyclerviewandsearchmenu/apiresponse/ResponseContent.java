package com.kanishk.recyclerviewandsearchmenu.apiresponse;

import java.util.List;

public class ResponseContent {
    //region Constants
    private static final String THUMBNAIL_FIELD = "thumbnail";
    private static final String IMAGE_BASE_URL = "http://www.nytimes.com/";
    //endregion Constants

    //region Member Variables
    private String web_url;
    private List<Multimedia> multimedia;
    private Headline headline;
    private String thumbnailURL = null;
    //endregion Member Variables

    //region Getters and Setters
    public String getWeb_url() {
        return web_url;
    }

    public void setWeb_url(String web_url) {
        this.web_url = web_url;
    }

    public List<Multimedia> getMultimedia() {
        return multimedia;
    }

    public Headline getHeadline() {
        return headline;
    }

    public String getThumbnailURL(){
        if (this.thumbnailURL == null) {
            StringBuilder imageURL = new StringBuilder("");
            this.multimedia.forEach(item ->  {
                if (item.getSubtype().equalsIgnoreCase(THUMBNAIL_FIELD)) {
                    imageURL.append(IMAGE_BASE_URL);
                    imageURL.append(item.getUrl());
                    return;
                }
            });
            this.thumbnailURL = imageURL.toString();
        }
        return this.thumbnailURL;
    }
    //endregion Getters and Setters

}
