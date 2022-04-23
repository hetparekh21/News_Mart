package com.example.newsmart.Data;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "news")
public class NewsObject {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo()
    private int id;

    @ColumnInfo()
    private String title;

    @ColumnInfo()
    private String description;

    @ColumnInfo()
    private String publisher;

    @ColumnInfo()
    private String publishDate;

    @ColumnInfo()
    private String websiteLink;

    @ColumnInfo()
    private boolean thumbnail;


    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public void setWebsiteLink(String websiteLink) {
        this.websiteLink = websiteLink;
    }

    public void setThumbnail(boolean thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public boolean getThumbnail() {
        return thumbnail;
    }

    public String getWebsiteLink() {
        return websiteLink;
    }
}
