package com.engineerkoghar.engineerkoghar;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostsFragment extends Fragment {
    public static final String PREFS_NAME = "erkogr";
    public static String favLabels = "";
    static private long latestPost = 0;
    static private int postViewed = 0;
    Activity referenceActivity;
    View parentHolder;
    FragmentActivity listener;
    CustomListAdapter adapter;
    String pageLink, url;
    String pageTitle;
    int totalPosts;
    View networkRetry, footerView;
    private ArrayList<FeedItem> feedList = null;
    private ProgressBar progressbar = null;
    private TextView progressTitle = null, noMorePosts = null;
    private ListView feedListView = null;
    private Button loadMoreButton, networkRetryButton;
    private int appStarted = 0;


    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.listener = (FragmentActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        referenceActivity = getActivity();
        parentHolder = inflater.inflate(R.layout.fragment_posts, container, false);

        SharedPreferences postData = referenceActivity.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        latestPost = postData.getLong("latestPostTime", 0);
        favLabels = postData.getString("favLabels", "");

        progressbar = (ProgressBar) parentHolder.findViewById(R.id.progressBar);
        progressTitle = (TextView) parentHolder.findViewById(R.id.progressTitle);
        networkRetry = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.network_retry, null, false);
        footerView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_footer, null, false);
        feedList = new ArrayList<FeedItem>();

        url = "http://www.engineerkoghar.blogspot.com/feeds/posts/default?max-results=7&alt=json";
        if (HomePageActivity.currFrag.equals("Favourites")) {
            if (favLabels.isEmpty()) {
                Toast.makeText(getContext(), "You have not set your favourite labels.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Showing posts with labels:\n" + favLabels.substring(1).replace("/", " & "), Toast.LENGTH_LONG).show();

                url = "http://www.engineerkoghar.blogspot.com/feeds/posts/default/-" + favLabels + "?max-results=7&alt=json";
            }
        }
        showContent(url);

        return parentHolder;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) referenceActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null && activeNetworkInfo.isConnected());

    }

    private void showContent(final String url) {
        if (isNetworkAvailable()) {
            new PostsFragment.DownloadFilesTask().execute(url);
        } else {
            noConnection();
            networkRetryButton = (Button) parentHolder.findViewById(R.id.nwRetryButt);
            networkRetryButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    yesConnection();
                    showContent(url);
                }
            });
        }
    }

    public void updateList() {
        if (appStarted == 0) {
            appStarted++;
            feedListView = (ListView) parentHolder.findViewById(R.id.custom_list);
            feedListView.setVisibility(View.VISIBLE);
            progressbar.setVisibility(View.GONE);
            progressTitle.setVisibility(View.GONE);
            feedListView.addFooterView(footerView);
            adapter = new CustomListAdapter(referenceActivity, feedList);
            feedListView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        feedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = feedListView.getItemAtPosition(position);
                FeedItem postData = (FeedItem) o;

                Intent intent = new Intent(getContext(), FeedDetailsActivity.class);
                intent.putExtra("feed", postData);
                intent.putExtra("pageUrl", "");
                startActivity(intent);
            }
        });
        feedListView.setOnScrollListener(new PostsFragment.EndlessScrollListener());
    }

    public void noNewData() {
    }

    public void noConnection() {
        Toast.makeText(getContext(), "Network Not Available", Toast.LENGTH_SHORT).show();
        parentHolder.findViewById(R.id.networkRetry).setVisibility(View.VISIBLE);
        parentHolder.findViewById(R.id.progressBar).setVisibility(View.GONE);
        parentHolder.findViewById(R.id.progressTitle).setVisibility(View.GONE);
    }

    public void yesConnection() {
        parentHolder.findViewById(R.id.networkRetry).setVisibility(View.GONE);
        parentHolder.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        parentHolder.findViewById(R.id.progressTitle).setVisibility(View.VISIBLE);
    }

    public JSONObject getJSONFromUrl(String url) {
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

    public void parseJson(JSONObject json) {
        try {
            // parsing json object
            if (json.getString("encoding").equalsIgnoreCase("UTF-8")) {
                JSONObject feed = json.getJSONObject("feed");
                totalPosts=Integer.parseInt(feed.getJSONObject("openSearch$totalResults").getString("$t"));
                String attachment1 = "";
                JSONArray author = feed.getJSONArray("author");
                for (int i = 0; i < author.length(); i++) {
                    JSONObject aut = author.getJSONObject(i);
                    attachment1 = aut.getJSONObject("gd$image").getString("src");
                }
                pageTitle = feed.getJSONObject("title").getString("$t");
                JSONArray mainLink = feed.getJSONArray("link");
                for (int j = 0; j < mainLink.length(); j++) {
                    if (mainLink.getJSONObject(j).getString("rel").equalsIgnoreCase("alternate")) {
                        pageLink = mainLink.getJSONObject(j).getString("href");
                    }
                }
                JSONArray entry = feed.getJSONArray("entry");
                for (int i = 0; i < entry.length(); i++) {
                    JSONObject post = (JSONObject) entry.getJSONObject(i);
                    String title = post.getJSONObject("title").getString("$t");
                    String date = post.getJSONObject("published").getString("$t");
                    String id = post.getJSONObject("id").getString("$t");
                    JSONArray link = post.getJSONArray("link");
                    String url = "";
                    for (int j = 0; j < link.length(); j++) {
                        if (link.getJSONObject(j).getString("rel").equalsIgnoreCase("alternate")) {
                            url = link.getJSONObject(j).getString("href");
                        }
                    }
                    String attachment = "";
                    String content = post.getJSONObject("content").getString("$t");
                    if (post.isNull("media$thumbnail")) {
                        attachment = attachment1;
                    } else {
                        attachment = post.getJSONObject("media$thumbnail").getString("url");
                    }
                    JSONArray labels = post.getJSONArray("category");
                    String label = "";
                    for (int j = 0; j < labels.length(); j++) {
                        label = label + labels.getJSONObject(j).getString("term") + ", ";
                    }
                    label = label.substring(0, label.length() - 2);

                    FeedItem item = new FeedItem();
                    item.setTitle(title);
                    item.setDate(date);
                    //item.setId(id);
                    item.setUrl(url);
                    item.setContent(content);
                    item.setAttachmentUrl(attachment);
                    item.setAttachmentState(false);
                    item.setLabel(label);

                    try {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                        Date result = df.parse(date);

                        long mils = result.getTime();
                        item.setId(mils);
                        if (mils > latestPost) {
                            item.setReadState(false);
                            if (postViewed == 0) {
                                SharedPreferences postData = referenceActivity.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = postData.edit();
                                editor.putLong("latestPostTime", mils);
                                editor.putInt("totalPosts",totalPosts);
                                editor.apply();
                            }
                        } else {
                            item.setReadState(true);
                        }

                    } catch (Exception e) {
                        Log.e("JSON Parser", "Error parsing data " + e.toString());
                        item.setReadState(true);
                    }
                    feedList.add(item);
                    postViewed++;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class EndlessScrollListener implements AbsListView.OnScrollListener {
        private int visibleThreshold = 7;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListener() {
        }

        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            updateContent(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

        public void updateContent(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
            boolean morePosts = false;
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    morePosts = true;
                    previousTotal = totalItemCount;
                    currentPage++;
                } else {
                    morePosts = false;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                if (feedListView.getLastVisiblePosition() == totalItemCount - 1) {
                    final int positionToSave = feedListView.getFirstVisiblePosition();
                    int startIndex = totalItemCount;
                    if (morePosts) {
                        parentHolder.findViewById(R.id.noMorePosts).setVisibility(View.VISIBLE);
                        parentHolder.findViewById(R.id.loadMore).setScaleX(0);
                    } else {
                        parentHolder.findViewById(R.id.noMorePosts).setVisibility(View.GONE);
                    }
                    url = "http://www.engineerkoghar.blogspot.com/feeds/posts/default?start-index=" + startIndex + "&max-results=7&alt=json";
                    if (HomePageActivity.currFrag.equals("Favourites")) {
                        if (!favLabels.isEmpty())
                            url = "http://www.engineerkoghar.blogspot.com/feeds/posts/default" + "/-" + favLabels + "?start-index=" + startIndex + "&max-results=7&alt=json";
                    }
                    if (isNetworkAvailable()) {
                        parentHolder.findViewById(R.id.loadRetry).setVisibility(View.GONE);
                        parentHolder.findViewById(R.id.loadMore).setVisibility(View.VISIBLE);

                        new PostsFragment.DownloadFilesTask().execute(url);
                        loading = true;
                        feedListView.setSelection(positionToSave);
                    } else {
                        parentHolder.findViewById(R.id.loadMore).setVisibility(View.GONE);
                        parentHolder.findViewById(R.id.loadRetry).setVisibility(View.VISIBLE);
                        loadMoreButton = (Button) parentHolder.findViewById(R.id.loadMoreButt);
                        loadMoreButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                parentHolder.findViewById(R.id.loadMore).setVisibility(View.VISIBLE);
                                parentHolder.findViewById(R.id.loadRetry).setVisibility(View.GONE);
                                updateContent(view, firstVisibleItem, visibleItemCount, totalItemCount);
                            }
                        });
                    }
                }
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }
    }

    private class DownloadFilesTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(Void result) {
            if (null != feedList) {
                updateList();
            }
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
                noNewData();
            }
            return null;
        }
    }
}
