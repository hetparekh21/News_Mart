package com.example.newsmart;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.newsmart.Data.NewsObject;
import com.example.newsmart.Data.NewsViewModel;

import java.io.File;
import java.util.List;


public class DataLoader extends androidx.loader.content.AsyncTaskLoader<List<NewsObject>> {

    String murl;
    Context mcontext;
    private NewsViewModel mnewsViewModel;

    /**
     * @param context
     * @deprecated
     */
    public DataLoader(Context context, String url, NewsViewModel newsViewModel) {
        super(context);
        this.mnewsViewModel = newsViewModel;
        mcontext = context;
        murl = url;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.e("TAG", "Inside onStartLoading (ForceLoad) ");
        forceLoad();
    }

    @Override
    public List<NewsObject> loadInBackground() {

        Log.e("TAG", "Inside loadInBackground (retrieving news data)");
        if (murl == null) {
            return null;
        }

        mnewsViewModel.dropData();
        File dir = mcontext.getFilesDir();
        if (dir.isDirectory()) {

            Log.e("TAG", "loadInBackground: deleting the files" );
            String[] children = dir.list();

            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }

        }

        List<NewsObject> data = Utils.fetchNewsData(mcontext, murl);

        if (!data.isEmpty()) {

            mnewsViewModel.insertData(data);

        }

        return data;
    }

}
