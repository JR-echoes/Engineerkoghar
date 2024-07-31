package com.engineerkoghar.engineerkoghar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by janardan on 12/3/16.
 */

public class SavedPostActivity extends AppCompatActivity {
    static private int postViewed = 0;
    CustomListAdapter adapter;
    String pageLink;
    String pageTitle;
    private ArrayList<FeedItem> feedList = null;
    private ProgressBar progressbar = null;
    private TextView progressTitle = null;
    private ListView feedListView = null;
    private Button loadMoreButton, networkRetryButton;
    private int appStarted = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_page_layout);

        progressbar = (ProgressBar) findViewById(R.id.progressBar);
        progressTitle = (TextView) findViewById(R.id.progressTitle);
        //networkRetry =  ((LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.network_retry, null, false);
        //footerView =  ((LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_footer, null, false);

        feedList = new ArrayList<FeedItem>();

        String url = "http://www.engineerkoghar.blogspot.com/feeds/posts/default?max-results=7&alt=json";
        // showContent(url);
    }
}
