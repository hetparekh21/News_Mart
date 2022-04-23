package com.example.newsmart.Data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class NewsRepository {

    private LiveData<List<NewsObject>> mNews;
    private NewsDao mnewsDao;

    public NewsRepository(Application application) {

        NewsRoomDatabase db = NewsRoomDatabase.getDatabase(application);
        mnewsDao = db.newsDao();
        mNews = mnewsDao.getNews();

    }

    void insertData(List<NewsObject> data){

        mnewsDao.insertNews(data);

    }

    LiveData<List<NewsObject>> getNews() {
        return mNews;
    }

    void dropData(){

        mnewsDao.dropData();

    }

}
