package com.example.newsmart;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.newsmart.Data.NewsObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static final String LOG_TAG = "Utils Tag";

    /**
     * Returns new URL object from the given string URL.
     */
    public static URL createUrl(Context context, String stringUrl) {
        URL url = null;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String country = sharedPreferences.getString(context.getString(R.string.settings_key_country), context.getString(R.string.settings_country_default));
        String category = sharedPreferences.getString(context.getString(R.string.settings_key_category), context.getString(R.string.settings_category_default));

        Uri baseUri = Uri.parse(stringUrl);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter(context.getString(R.string.settings_key_category), category);
        uriBuilder.appendQueryParameter(context.getString(R.string.settings_key_country), country);
        uriBuilder.appendQueryParameter("limit", "5");

        Log.e(LOG_TAG, "createUrl: " + uriBuilder.toString());
        try {
            url = new URL(uriBuilder.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        Log.e(LOG_TAG, stringUrl);
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link NewsObject} objects that has been built up from
     * parsing the given JSON response.
     */
    private static ArrayList<NewsObject> extractFeatureFromJson(String NewsJson , Context context) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(NewsJson)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        ArrayList<NewsObject> newsObjects = new ArrayList<NewsObject>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(NewsJson);

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or earthquakes).
            JSONArray articles = baseJsonResponse.getJSONArray("data");

            // For each earthquake in the earthquakeArray, create an {@link Earthquake} object
            for (int i = 0; i < articles.length(); i++) {

                String title = null, description = null, publisher = null, publishDate = null, websiteLink = null;
                boolean thumbnail = false;

                try {

                    // Get a single earthquake at position i within the list of earthquakes
                    JSONObject currentNews = articles.getJSONObject(i);

                    title = currentNews.getString("title");

                    description = currentNews.getString("description");

                    publisher = currentNews.getString("source");

                    publishDate = currentNews.getString("published_at");

                    websiteLink = currentNews.getString("url");

                    String imageurl = currentNews.getString("image");

                    if (!imageurl.equals("null")) {

                        thumbnail = true;
                        getBitmap(imageurl, title , context);

                    } else {

                        Log.d("TAG", "extractFeatureFromJson: Imageurl == null");

                    }


                } catch (JSONException e) {

                    Log.e(LOG_TAG, "extractFeatureFromJson: not present problem");

                } finally {
                    // Create a new {@link Earthquake} object with the magnitude, location, time,
                    // and url from the JSON response.
                    NewsObject newsObject = new NewsObject();
                    newsObject.setTitle(title);
                    newsObject.setDescription(description);
                    newsObject.setPublisher(publisher);
                    newsObject.setPublishDate(publishDate);
                    newsObject.setThumbnail(thumbnail);
                    newsObject.setWebsiteLink(websiteLink);

                    // Add the new {@link Earthquake} to the list of earthquakes.
                    newsObjects.add(newsObject);
                }

            }

        } catch (JSONException | IOException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return newsObjects;
    }

    /**
     * get Image bitmap
     */

    private static void getBitmap(String imageurl, String title , Context context) throws IOException {

        // todo : get the image from the url and save it as a file withe it's title as it's filename

        Log.d(LOG_TAG, "getBitmap: " + imageurl);
        URL urlConnection;
        try {
            urlConnection = new URL(imageurl);
        } catch (MalformedURLException malformedURLException) {

            return;

        }
        // http connection
        HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
        connection.setDoInput(true);
        try {
            connection.connect();

        } catch (IOException ioException) {

            return;
        }
        InputStream input = null;
        try {

            input = connection.getInputStream();

        } catch (java.io.FileNotFoundException e) {

            Log.e("TAG", "getBitmap: File not found");
            return;

        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = calculateInSampleSize(options, 300, 250);
        options.inMutable = true;
        options.inJustDecodeBounds = true;
        Rect rect = new Rect();
        rect.set(1, 2, 3, 4);

        Bitmap bitmap = BitmapFactory.decodeStream(input);

//        if (bitmap != null) {

//            bitmap.setConfig(Bitmap.Config.RGB_565);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String[] gg = title.split(" ");

//        String togo = gg[0];
//
//        if(gg[0].contains("\"")){
//
//            togo = gg[1] ;
//
//        }

        //you can create a new file name "test.jpg" in sdcard folder.
        File f = new File(context.getFilesDir()
                + File.separator + gg[0] + ".jpg");
        Log.e("TAG", "getBitmap: " + context.getFilesDir()
                + File.separator + gg[0] + ".jpg");

        f.createNewFile();
        //write the bytes in file
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());

        // remember close de FileOutput
        fo.close();
//        }

    }

    public static byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(byteBuffer);
        byteBuffer.rewind();
        return byteBuffer.array();
    }

    public static byte[] bitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, output);
        return output.toByteArray();
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * to find the user preference for the search
     */
    private static String findprefrence(String userprefrence) {

        switch (userprefrence) {

            case "Title":
                return "title";

            case "Description":
                return "description";

            case "Content":
                return "content";

            default:
                return "";

        }

    }

    /**
     * Query the Google Books API dataset and return a list of {@link NewsObject} objects.
     */
    public static List<NewsObject> fetchNewsData(Context context, String requestUrl) {
        // Create URL object
        URL url = createUrl(context, requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        List<NewsObject> newsObjects = extractFeatureFromJson(jsonResponse, context);

        // Return the list of {@link Earthquake}s
        return newsObjects;
    }

}
