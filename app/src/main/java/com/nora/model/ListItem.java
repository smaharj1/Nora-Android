package com.nora.model;

/**
 * Created by sujil on 10/22/2016.
 */

public class ListItem {
    private String title;
    private String url;

    public ListItem(String t, String url) {
        title = t;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

}
