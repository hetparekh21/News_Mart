package com.example.newsmart.Data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NewsDao {

    @Insert()
    void insertNews(List<NewsObject> data);

    @Query("SELECT * FROM news")
    LiveData<List<NewsObject>> getNews();

    @Query("DELETE FROM news")
    void dropData();

}
