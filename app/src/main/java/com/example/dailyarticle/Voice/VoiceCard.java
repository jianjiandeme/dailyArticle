package com.example.dailyarticle.Voice;

/**
 * Created by ZZP on 2017/7/7.
 */

public class VoiceCard {
    private String author;
    private String image;
    private String id;
    private String name;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author.replace("&nbsp;&nbsp;","\n");
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
