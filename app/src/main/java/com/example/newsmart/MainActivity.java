package com.example.newsmart;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.newsmart.Data.NewsObject;
import com.example.newsmart.Data.NewsViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

public class MainActivity extends AppCompatActivity implements androidx.loader.app.LoaderManager.LoaderCallbacks<List<NewsObject>>, NewsObjectAdapter.OnNewsListener {

//    https://newsapi.org/v2/top-headlines/?

    private static final String TAG = "TAG_MAIN_ACTIVITY";
    private int pageNumber = 0;
    String Top_headlines_sources = "http://api.mediastack.com/v1/news?access_key=7bf295a1f5f1ac32c3bf480f279d816e";
    NewsObjectAdapter mAdapter;
    private NewsViewModel newsViewModel;
    RecyclerView recyclerView;
    private SharedPreferences.OnSharedPreferenceChangeListener listner;
    SharedPreferences preferences;
    SwipeRefreshLayout swipeRefreshLayout;
    SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
    SwipeRefreshLayout.OnRefreshListener refreshListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Preference.OnPreferenceChangeListener changeListener = new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                Log.e(TAG, "onPreferenceChange: lol");

                return true;
            }
        };

//        pref.setOnPreferenceChangeListener(changeListener);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (preferences.getBoolean("dark_mode", false)) {

            setTheme(com.google.android.material.R.style.Base_Theme_Material3_Dark);

        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);

        // Declaring a layout (changes are to be made to this)
        // Declaring a textview (which is inside the layout)

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);

        refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // This line is important as it explicitly
                // refreshes only once
                // If "true" it implicitly refreshes forever
                swipeRefreshLayout.setRefreshing(true);
                findViewById(R.id.empty_list).setVisibility(View.GONE);
//                findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
                UpdateData();
            }
        };

        // Refresh  the layout
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

//        if (Build.VERSION.SDK_INT >= 30) {
//            if (!Environment.isExternalStorageManager()) {
//                Intent getpermission = new Intent();
//                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                startActivity(getpermission);
//            }
//        }

        recyclerView = findViewById(R.id.list);

        mAdapter = new NewsObjectAdapter(new ArrayList<NewsObject>(), this,getApplicationContext());

        recyclerView.setAdapter(mAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listner = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                boolean toggled = sharedPreferences.getBoolean("dark_mode", false);

                if (key.equals("countries") || key.equals("categories")) {

                    refreshListener.onRefresh();

                }

                Log.e("TAG", "onSharedPreferenceChanged:;");

                if (toggled) {

                    setTheme(com.google.android.material.R.style.Base_Theme_Material3_Dark);
                    recreate();
//                    new AlertDialog.Builder(MainActivity.this)
//                            .setMessage("Please restart the app to apply changes")
//                            .setPositiveButton("OK", null)
//                            .show();

                } else {

                    setTheme(com.google.android.material.R.style.Theme_AppCompat_DayNight_DarkActionBar);
                    recreate();

                }
            }
        };

        preferences.registerOnSharedPreferenceChangeListener(listner);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public androidx.loader.content.Loader<List<NewsObject>> onCreateLoader(int id, Bundle args) {

        Toast.makeText(this, "Please wait while we prepare your feed", Toast.LENGTH_LONG).show();

        DataLoader dataLoader = new DataLoader(this, Top_headlines_sources, newsViewModel);

        return dataLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<NewsObject>> loader, List<NewsObject> data) {

//        findViewById(R.id.progressbar).setVisibility(View.GONE);

        Log.e("TAG", "Inside onLoadFinished: ");

//        // Clear the adapter of previous news data
//        mAdapter.NewsObjectList.clear();
//        mAdapter.notifyDataSetChanged();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {

//            findViewById(R.id.empty_list).setVisibility(View.GONE);
            AddToScreen();

        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<NewsObject>> loader) {

        Log.e("TAG", "Inside onLoaderReset: ");

        // Clear the adapter of previous news data
        mAdapter.NewsObjectList.clear();
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void OnNewsClick(int position) {
        String url = mAdapter.NewsObjectList.get(position).getWebsiteLink();
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        startActivity(intent);

    }

    @Override
    public void OnViewClick(int position) {

        String weburl = mAdapter.NewsObjectList.get(position).getWebsiteLink();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, weburl);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Date dateNow = new Date();
        Log.e(TAG, "onStart: " + format.format(dateNow).toString());
        Date datSaved = null;

        if (preferences.contains("date")) {

            try {
                datSaved = format.parse(preferences.getString("date", format.format(dateNow).toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Log.e(TAG, "Date Now " + dateNow.toString());

            Log.e(TAG, "Date Saved " + datSaved.toString());

            long diff = dateNow.getTime() - datSaved.getTime();

            long diffHours = diff / (60 * 60 * 1000);

            long diffSeconds = diff / 1000 % 60;

            Log.e(TAG, "Time in seconds: " + diffSeconds + " seconds.");

            Log.e(TAG, "Time in hours: " + diffHours + " hours.");

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("date", format.format(dateNow).toString());
            editor.apply();

            if (diffHours >= 1) {

                refreshListener.onRefresh();

            } else {

                AddToScreen();

            }

        } else {

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("date", format.format(dateNow).toString());
            editor.apply();

            Log.e(TAG, "onStart: setRefreshing");

            refreshListener.onRefresh();

        }

    }

    public void UpdateData() {

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            androidx.loader.app.LoaderManager loaderManager = getSupportLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            // Log.e(TAG, "calling the init manager");
            Log.e("TAG", "onCreate: calling init manager");
//            findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
            loaderManager.initLoader(0, null, this).startLoading();

        } else {

            Toast.makeText(this, "No Internet connection", Toast.LENGTH_SHORT).show();
            AddToScreen();

        }

    }

    public void AddToScreen() {

        newsViewModel.getNews().observe(this, newsData -> {

            swipeRefreshLayout.setRefreshing(false) ;
            if (newsData.isEmpty()) {

                findViewById(R.id.empty_list).setVisibility(View.VISIBLE);

            }else {

                findViewById(R.id.empty_list).setVisibility(View.GONE);

            }

            mAdapter.NewsObjectList.clear();
            mAdapter.NewsObjectList.addAll(newsData);
            mAdapter.notifyDataSetChanged();

        });

//        findViewById(R.id.progressbar).setVisibility(View.GONE);

    }

}