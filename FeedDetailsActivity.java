package com.engineerkoghar.engineerkoghar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

public class FeedDetailsActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FeedItem feed;
    private WebView webView;
    private ProgressBar webProgress;
    private ImageView thumb;
    private TextView title, pubDate;
    private FrameLayout thumbFrame;
    private String htmlContents, pageTitle, publishDate, htmlHeader="",htmlFooter="";
    private AdView mAdView;
    private String backgroundColor="", textColor="";

    InterstitialAd myInterstitialAd;//Interstitial Ads
    private int detailsViewed=0;

    SharedPreferences postData;
    public static final String PREFS_NAME = "erkogr";

    public static void writeData(long id, String title, String date, String label, String content) {
        /**final String xmlFile="savedPosts";
         try (FileOutputStream ofs = new FileOutputStream("savedPosts.xml");
         FileOutputStream fileos=getApplicationContext().openFileOutput(xmlFile, Context.MODE_PRIVATE);
         XmlSerializer xmlSerializer = Xml.newSerializer();
         StringWriter writer = new StringWriter();
         xmlSerializer.setOutput(writer);
         xmlSerializer.startDocument("UTF-8", true);
         xmlSerializer.startTag(null, "userData");
         xmlSerializer.startTag(null, "userName");
         xmlSerializer.text(username_String_Here);
         xmlSerializer.endTag(null, "userName");
         xmlSerializer.startTag(null,"password");
         xmlSerializer.text(password_String);
         xmlSerializer.endTag(null, "password");
         xmlSerializer.endTag(null, "userData");
         xmlSerializer.endDocument();
         xmlSerializer.flush();
         String dataWrite = writer.toString();
         fileos.write(dataWrite.getBytes());
         fileos.close();
         } catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         }**/

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.feed_details_layout);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeAsUpIndicator(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayShowHomeEnabled(true);

        //SharedPreferences Starts
        postData = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        detailsViewed = postData.getInt("detailsViewed",0);
        SharedPreferences.Editor editor = postData.edit();
        editor.putInt("detailsViewed",detailsViewed+1);
        editor.apply();
        //SharedPreferences Ends

        //AdMob Code Starts
        myInterstitialAd = new InterstitialAd(this);
        myInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        myInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                SharedPreferences postData = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = postData.edit();
                editor.putInt("detailsViewed",0);
                // Commit the edits!
                editor.apply();
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView = (AdView) findViewById(R.id.adView);
        if(detailsViewed>=6) {
            myInterstitialAd.loadAd(adRequest);
        }
        mAdView.loadAd(adRequest);
        //AdMob Code Ends

        thumbFrame = (FrameLayout) findViewById(R.id.thumbFrame);
        thumb = (ImageView) findViewById(R.id.thumbImage);
        title = (TextView) findViewById(R.id.title);
        pubDate = (TextView) findViewById(R.id.date);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webProgress = (ProgressBar) findViewById(R.id.webProgress);
        if(AppWrapper.darkModeSet){
            Context context=getApplicationContext();
            backgroundColor="#" + Integer.toHexString(ContextCompat.getColor(context, R.color.tintedFinal) & 0x00ffffff);
            textColor="#" + Integer.toHexString(ContextCompat.getColor(context, R.color.textColorPrimary) & 0x00ffffff);
        }
        webView.setBackgroundColor(Color.TRANSPARENT);
        htmlHeader="<style>a{text-decoration:none;color:#2f8fff;}img{display: inline;height: auto;max-width: 100%;}#container{background-color:"+backgroundColor+"; color:"+textColor+";}</style><div id='container'>";
        htmlFooter="</div>";
        String pageUrl = (String) this.getIntent().getStringExtra("pageUrl");
        if (pageUrl.isEmpty()) {
            feed = (FeedItem) this.getIntent().getSerializableExtra("feed");

            if (null != feed) {

                new ImageDownloaderTask(thumb).execute(feed.getAttachmentUrl());

                title.setText(feed.getTitle());
                pubDate.setText(feed.getDate().substring(0, 10));
                webView.loadDataWithBaseURL(null, htmlHeader + feed.getContent()+htmlFooter, "text/html", "utf-8", null);
                webProgress.setVisibility(View.GONE);
            }
        } else {
            thumbFrame.setVisibility(View.GONE);
            loadFromURL(pageUrl);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                webView.reload();
                return true;
            case R.id.menu_share:
                shareContent();
                return true;
            case R.id.menu_open_browser:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(feed.getUrl()));
                startActivity(i);
                return true;
            /**
             case R.id.menu_save:
             saveContent();
             return true;
             case R.id.details_settings:
             shareContent();
             return true;*/

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                showInterstitialAd();
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){
        showInterstitialAd();
        this.finish();
    }

    public void showInterstitialAd(){
            if (myInterstitialAd.isLoaded()) {
                myInterstitialAd.show();
            } else {
                //Begin Game, continue with app
            }
    }

    private void shareContent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, feed.getTitle() + "\n" + feed.getUrl());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share using"));

    }

    private void saveContent() {
        // Parse your HTML:
// 1. From string:
        Document doc = Jsoup.parse(feed.getContent());
// 2. Or from an URL:
//        Document doc = JSoup.connect("http://engineerkoghar.blogspot.com/").get();
// Then select images inside it:
        Elements images = doc.select("img");
// Then iterate
        String imageUrl = "";
        for (Element el : images) {
            int index = el.attr("src").lastIndexOf("/");
            imageUrl = imageUrl + el.attr("src").substring(index + 1) + "\n";

            // TODO: Do something with the URL
        }
        new AlertDialog.Builder(this)
                .setMessage(feed.getId() + "\n" + feed.getTitle() + "\n" + feed.getDate().substring(0, 10) + "\n" + feed.getLabel() + "\n" + imageUrl)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                    }
                })
                .create()
                .show()
        ;
    }

    public void loadFromURL(String url) {
        //thumb.setVisibility(View.GONE);
        if (isNetworkAvailable()) {
            new DownloadFilesTask().execute(url);
        } else {
            Toast.makeText(getApplicationContext(), "Network Not Available", Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadFilesTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(Void result) {

            //webView.loadDataWithBaseURL(null, "<style>img{display: inline;height: auto;max-width: 100%;}"+datesStyle+dldStyle+"</style>"+"<h2>"+linkTitle+"</h2>"+htmlContents, "text/html", "utf-8", null);
            //progressBar.setVisibility(View.GONE);
            //mySwipeRefreshLayout.setRefreshing(false);
            thumbFrame.setVisibility(View.VISIBLE);
            title.setText(pageTitle);
            pubDate.setText(publishDate);
            webView.loadDataWithBaseURL(null, htmlHeader + htmlContents+htmlFooter, "text/html", "utf-8", null);
            webProgress.setVisibility(View.GONE);

        }

        @Override
        protected Void doInBackground(String... params) {
            String pageUrl = params[0];
            try {
                Document doc = Jsoup.connect(pageUrl).ignoreContentType(true).get();
                //System.out.println(doc);
                Elements htmlC = doc.select("div[id=Blog1]").select(".post-body");
                htmlContents = htmlC.html();
                pageTitle = getMetaTag(doc, "og:title");
                publishDate = doc.select("h2").select(".date-header").select("span").html();
                //linkTitle=doc.title();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        String getMetaTag(Document document, String attr) {
            Elements elements = document.select("meta[name=" + attr + "]");
            for (Element element : elements) {
                final String s = element.attr("content");
                if (s != null) return s;
            }
            elements = document.select("meta[property=" + attr + "]");
            for (Element element : elements) {
                final String s = element.attr("content");
                if (s != null) return s;
            }
            return null;
        }
    }
}
