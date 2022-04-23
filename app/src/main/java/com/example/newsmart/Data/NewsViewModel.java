package com.example.newsmart.Data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NewsViewModel extends AndroidViewModel {

    private NewsRepository mRepository;

    private LiveData<List<NewsObject>> mNews;

    public NewsViewModel(Application application) {
        super(application);
        mRepository = new NewsRepository(application);
    }

    public void insertData(List<NewsObject> data){

        mRepository.insertData(data);

    }

    public LiveData<List<NewsObject>> getNews() {

        mNews = mRepository.getNews();

        return mNews;
    }

    public void dropData(){

        mRepository.dropData();

    }


}
