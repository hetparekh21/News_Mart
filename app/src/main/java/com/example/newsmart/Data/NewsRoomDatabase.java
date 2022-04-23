package com.example.newsmart.Data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {NewsObject.class}, version = 1, exportSchema = false)
//@TypeConverters({Converters.class})
public abstract class NewsRoomDatabase extends RoomDatabase {

    public abstract NewsDao newsDao();

    public static volatile NewsRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
//    static final ExecutorService databaseReadExecutor =
//            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static NewsRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (NewsRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            NewsRoomDatabase.class, "News_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
