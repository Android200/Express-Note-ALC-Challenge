package com.alc.app.expressnote.model;

/**
 * Created by Umar Saidu Auna on 06/26/18.
 */

public class Entry {

    private String title, content, image, date;



    public Entry(String title, String content, String image, String date) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.date = date;
    }

    public Entry(){

    }
    public void setDate(String date) {

        this.date = date;
    }
    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
