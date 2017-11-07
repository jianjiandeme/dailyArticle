package com.example.dailyarticle.BookCase;

/**
 * Created by ZZP on 2017/7/3.
 */

public class BookCase {
    //private String id;
    private String bookName;
    private String bookAuthor;
    private String imageAddress;
    private String bookAddress;

    public String getBookAddress() {
        return bookAddress;
    }

    public void setBookAddress(String bookAddress) {
        this.bookAddress = bookAddress;
    }



   /* public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }*/

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getImageAddress() {
        return imageAddress;
    }

    public void setImageAddress(String imageAddress) {
        this.imageAddress = imageAddress;
    }
}
