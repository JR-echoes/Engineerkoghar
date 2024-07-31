package com.engineerkoghar.engineerkoghar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.NotificationManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by janardan on 5/6/17.
 */

public class ShowNotification extends AppCompatActivity {

    private PendingIntent pendingIntent;
    public static NotificationManager notificationManager;
    public static final String PREFS_NAME = "erkogr";
    SharedPreferences postData;
    static int totalPosts=13, oldTotalPosts;
    static Context context;

    InterstitialAd mInterstitialAd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notification_activity);
        postData = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        long latestPost = postData.getLong("latestPostTime", 0);
        String favLabels = postData.getString("favLabels", "");
        oldTotalPosts = postData.getInt("totalPosts",0);
        context=getApplicationContext();

        Calendar time1=Calendar.getInstance();
        //calendar.set(Calendar.YEAR,2017);
        //calendar.set(Calendar.MONTH,06);
        //calendar.set(Calendar.DAY_OF_MONTH,29);
        time1.setTimeInMillis((System.currentTimeMillis()));
        time1.set(Calendar.HOUR_OF_DAY,2);
        time1.set(Calendar.MINUTE,30);
        time1.set(Calendar.SECOND,0);
        time1.set(Calendar.AM_PM,Calendar.PM);
        if(time1.before(new GregorianCalendar())){
            time1.add(GregorianCalendar.DAY_OF_MONTH,1);
        }
/**
        Calendar time2=Calendar.getInstance();
        time2.setTimeInMillis((System.currentTimeMillis()));
        time2.set(Calendar.HOUR_OF_DAY,8);
        time2.set(Calendar.MINUTE,0);
        time2.set(Calendar.SECOND,0);
        time2.set(Calendar.AM_PM,Calendar.PM);
        if(time2.before(new GregorianCalendar())){
            time2.add(GregorianCalendar.DAY_OF_MONTH,1);
        }
 */

        Intent myIntent = new Intent(ShowNotification.this,MyReceiver.class);
        pendingIntent=PendingIntent.getBroadcast(ShowNotification.this,0,myIntent,0);
        AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC,time1.getTimeInMillis(),AlarmManager.INTERVAL_HALF_DAY,pendingIntent);
        //alarmManager.setInexactRepeating(AlarmManager.RTC,time2.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);


        Button newButton=(Button) findViewById(R.id.notificationButton);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getBaseContext(), HomePageActivity.class);
                //startActivity(intent);
                //checkNotification(getApplicationContext());
                showNotification();
            }
        });
        //checkNotification(context);
    }

    public static void sendNotification(Context context, int newPostsNum){
        //Get an instance of NotificationManager//
        //Bitmap bigIcon = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_stat_logo_notification)
                .setColor(Color.BLACK)
                .setContentTitle(newPostsNum+" new posts")
                .setContentText("Tap for details.");

        //Create the intent that'll fire when the user taps the notificaiton
        Intent intent = new Intent(context,HomePageActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,0);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        //Get an instance of the NotificationManager service
        notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        //When you issue multiple notification about the same type of event, it's best practice for system to be notified
        notificationManager.notify(001,mBuilder.build());
    }

    public  static void checkNotification(Context context){
        String url="http://www.engineerkoghar.blogspot.com/feeds/posts/default?max-results=7&alt=json";
        //new DownloadFilesTask().execute(url);
    }

    public static void showNotification(){
        sendNotification(context,0);
        //if(totalPosts>=oldTotalPosts){
        //    sendNotification(context, totalPosts-20);
        //}
    }

    public static JSONObject getJSONFromUrl(String url) {
        InputStream is = null;
        JSONObject jObj = null;
        String json = "";

        // Making HTTP request
        try {
            java.net.URL url1 = new java.net.URL(url);
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            conn.setRequestMethod("GET");

            // read the response
            System.out.println("Response Code: " + conn.getResponseCode());
            is = conn.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);//iso-8859-1
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!json.equals("")) {
            try {
                jObj = new JSONObject(json);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
        } else {
            Log.e("jObj Creation", "Error creating json object");
        }
        // return JSON String
        return jObj;
    }

    public static void parseJson(JSONObject json) {
        try {
            // parsing json object
            if (json.getString("encoding").equalsIgnoreCase("UTF-8")) {
                JSONObject feed = json.getJSONObject("feed");
                totalPosts=Integer.parseInt(feed.getJSONObject("openSearch$totalResults").getString("$t"));
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static class DownloadFilesTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(Void result) {
            showNotification();
        }

        @Override
        protected Void doInBackground(String... params) {
            String url = params[0];
            // getting JSON string from URL
            JSONObject json = (JSONObject) getJSONFromUrl(url);
            //parsing json data
            if (null != json) {
                parseJson(json);
            } else {
                Log.e("No Connection", "Error Connecting to the server");
            }
            return null;
        }
    }
}
