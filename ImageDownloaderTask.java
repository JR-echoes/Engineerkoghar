package com.engineerkoghar.engineerkoghar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.ImageView;

//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
//import com.engineerkoghar.jsonparsing.R;

public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private String url;
    /********ADDED LATER FOR IMAGE BUG***//////
    private LinkedHashMap<String, Bitmap> bitmapCache = new LinkedHashMap<String, Bitmap>();

    public ImageDownloaderTask(ImageView imageView) {
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    static Bitmap downloadBitmap(String url) {
        if (URLUtil.isValidUrl(url)) {/**
         /////////////////////////////////////////////////////////////////////////////////////////////
         final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
         final HttpGet getRequest = new HttpGet(url);
         /////////////////////////////////////////////////////////////////////////////**/
            try {
                URL url1 = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();/**
                 /////////////////////////////////////////////////////////////////////////////////
                 HttpResponse response = client.execute(getRequest);
                 final int statusCode = response.getStatusLine().getStatusCode();
                 /////////////////////////////////////////////////////////////////////////////////**/
                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    Log.w("ImageDownloader", "Error " + responseCode + " while retrieving bitmap from " + url);
                    return null;
                } else {/**
                 /////////////////////////////////////////////////////////////////////////////////
                 if (statusCode != HttpStatus.SC_OK) {
                 Log.w("ImageDownloader", "Error " + statusCode
                 + " while retrieving bitmap from " + url);
                 return null;
                 }

                 final HttpEntity entity = response.getEntity();

                 if (entity != null) {
                 //////////////////////////////////////////////////////////////////////////////**/
                    InputStream inputStream = conn.getInputStream();
                    try {
                        //inputStream = entity.getContent();
                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        return bitmap;
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        //entity.consumeContent();
                    }
                }
            } catch (Exception e) {
                // Could provide a more explicit error message for IOException or
                // IllegalStateException
                //getRequest.abort();
                Log.w("ImageDownloader", "Error while retrieving bitmap from " + url);
            } finally {
                //if (client != null) {
                //    client.close();
                //}
            }
            return null;

        }
        return null;
    }

    @Override
    // Actual download method, run in the task thread
    protected Bitmap doInBackground(String... params) {
        url = params[0];
        // params comes from the execute() call: params[0] is the url.
        return downloadBitmap(params[0]);
    }

    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }
        addBitmapToCache(url, bitmap);

        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {

                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageDrawable(imageView.getContext().getResources().getDrawable(R.drawable.placeholder_list));
                }
            }

        }
    }

    private void addBitmapToCache(String url, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (bitmapCache) {
                bitmapCache.put(url, bitmap);
            }
        }
    }

    private Bitmap fetchBitmapFromCache(String url) {

        synchronized (bitmapCache) {
            final Bitmap bitmap = bitmapCache.get(url);
            if (bitmap != null) {
                return bitmap;
            }
        }

        return null;

    }


}
