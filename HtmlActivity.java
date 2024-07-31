package com.engineerkoghar.engineerkoghar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Created by janardan on 12/4/16.
 */

public class HtmlActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    SwipeRefreshLayout mySwipeRefreshLayout;
    private WebView webView;
    private ProgressBar progressBar;
    private View networkRetry;
    private Button networkRetryButton;
    private String pageUrl, pageTitle;
    private String htmlContents, datesStyle, dldStyle,htmlHeader="",htmlFooter="";
    private String linkTitle = "";
    private AdView mAdView;
    private String backgroundColor="", textColor="";
    private boolean fromNotify;

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
        if(AppWrapper.isDarkModeSet(getApplicationContext())){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.html_activity_layout);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeAsUpIndicator(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayShowHomeEnabled(true);

        /**AdMob Code Starts*/
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        /**AdMob Code Ends*/

        webView = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.webProgress);
        networkRetry = (View) findViewById(R.id.networkRetry);
        if(AppWrapper.darkModeSet){
            Context context=getApplicationContext();
            backgroundColor="#" + Integer.toHexString(ContextCompat.getColor(context, R.color.tintedFinal) & 0x00ffffff);
            textColor="#" + Integer.toHexString(ContextCompat.getColor(context, R.color.textColorPrimary) & 0x00ffffff);
        }
        webView.setBackgroundColor(Color.TRANSPARENT);

        mySwipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipeContainer);
        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                networkRetry.setVisibility(View.GONE);
                getContent(pageUrl);

                //Toast.makeText(getApplicationContext(),"Refreshed Done.",Toast.LENGTH_SHORT).show();
            }
        });

        pageUrl = this.getIntent().getStringExtra("pageUrl");
        pageTitle = this.getIntent().getStringExtra("pageTitle");
        fromNotify=this.getIntent().getExtras().getBoolean("fromNotify",false);
        getSupportActionBar().setTitle(pageTitle);
        datesStyle = "font-size:5dp;a:link{color: #006fef;}a:visited{color: #297cba;}a{text-decoration:none;}#deadline_na{border-collapse:collapse;}#deadline_na .dates-month{background-color: rgba(255,153,0,0.8);}#deadline_na tr{border-bottom: 1px solid silver;background-color:#ddd;}" +
                "td{padding-left:5px;padding-right:5px;}.dates-month{background-color: rgba(255,153,0,0.8); font-size: 18px;}.dates-month td{border-bottom: 1px solid silver;}" +
                ".month-content tr:hover{background-color: orange;background-color:#f7f7f7;}" +
                ".dates-header{background-color:silver;text-size:16px;font-weight:bold;padding:5px;}.month-content table td{border-bottom: 1px solid silver;}";

        dldStyle = ".list-header{background-color: orange;background-color:rgba(255,153,0,0.8); font-size: 16px; padding: 5px;}.syllabus-faculty{width:25%;}" +
                ".syllabus table{width:100%;}.syllabus table tr:nth-child(odd) {background-color:#eee;}.syllabus table tr:nth-child(even) {background-color:#ccc;}.anchorer{position:absolute; top:-65px;}" +
                ".syllabus{border-radius: 5px; border: 1px solid silver; width: 100%;position:relative;}.syllabus div{border-top-right-radius:5px;border-top-left-radius:5px;}.syllabus .src-link a{color:#0f6fff;}";

        htmlHeader="<style>a{text-decoration:none;color:#2f8fff;}img{display: inline;height: auto;max-width: 100%;}#container{background-color:"+backgroundColor+"; color:"+textColor+";}"+datesStyle + dldStyle+"</style><div id='container'>";
        htmlFooter="</div>";
        getContent(pageUrl);

        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("engineerkoghar.blogspot.com")) {
                    if (url.contains("#")) {
                        return true;
                    } else {
                        Intent intentDetails = new Intent(HtmlActivity.this, FeedDetailsActivity.class);
                        intentDetails.putExtra("pageUrl", url);
                        intentDetails.putExtra("pageTitle", "Post Details");
                        startActivity(intentDetails);
                        return true;
                    }
                } else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    return true;
                }
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.contains("engineerkoghar.blogspot.com")) {
                    if (url.contains("#")) {
                        return true;
                    } else {
                        Intent intentDetails = new Intent(HtmlActivity.this, FeedDetailsActivity.class);
                        intentDetails.putExtra("pageUrl", url);
                        intentDetails.putExtra("pageTitle", "Post Details");
                        startActivity(intentDetails);
                        return true;
                    }
                } else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    return true;
                }
            }
        });
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
                mySwipeRefreshLayout.setRefreshing(true);
                networkRetry.setVisibility(View.GONE);
                getContent(pageUrl);
                return true;
            case R.id.menu_share:
                shareContent();
                return true;
            case R.id.menu_open_browser:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(pageUrl));
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
                if(fromNotify) {
                    Intent myIntent = new Intent(HtmlActivity.this, HomePageActivity.class);
                    startActivity(myIntent);
                }
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){
        if(fromNotify) {
            Intent myIntent = new Intent(HtmlActivity.this, HomePageActivity.class);
            startActivity(myIntent);
        }
        this.finish();
    }

    private String getContent(final String pageUrl) {
        String htmlContents = "";
        if (isNetworkAvailable()) {
            new HtmlActivity.DownloadFilesTask().execute(pageUrl);
        } else {
            Toast.makeText(getApplicationContext(), "Network Not Available", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            networkRetry.setVisibility(View.VISIBLE);
            mySwipeRefreshLayout.setRefreshing(false);
            networkRetryButton = (Button) findViewById(R.id.nwRetryButt);
            networkRetryButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    networkRetry.setVisibility(View.GONE);
                    mySwipeRefreshLayout.setRefreshing(true);
                    getContent(pageUrl);
                }
            });
        }
        return htmlContents;
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

    private void shareContent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, pageTitle + "\n" + pageUrl);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share using"));

    }

    private void saveContent() {
        // Parse your HTML:
// 1. From string:
        Document doc = Jsoup.parse(getContent("a"));
// 2. Or from an URL:
//        Document doc = JSoup.connect("http://engineerkoghar.blogspot.com
        Elements images = doc.select("img");
// Then iterate
        String imageUrl = "";
        for (Element el : images) {
            int index = el.attr("src").lastIndexOf("/");
            imageUrl = imageUrl + el.attr("src").substring(index + 1) + "\n";

            // TODO: Do something with the URL
        }
    }

    private class DownloadFilesTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(Void result) {
            //webView.loadDataWithBaseURL(null, "<style>img{display: inline;height: auto;max-width: 100%;}" + datesStyle + dldStyle + "</style>" + "<h2>" + linkTitle + "</h2>" + htmlContents, "text/html", "utf-8", null);
            webView.loadDataWithBaseURL(null, htmlHeader + "<h2>" + linkTitle + "</h2>" + htmlContents + htmlFooter, "text/html", "utf-8", null);
            progressBar.setVisibility(View.GONE);
            mySwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected Void doInBackground(String... params) {
            String pageUrl = params[0];
            try {
                Document doc = Jsoup.connect(pageUrl).ignoreContentType(true).get();
                //System.out.println(doc);
                Elements htmlC = doc.select("div[id=Blog1]").select(".post-body");
                htmlContents = htmlC.html();
                linkTitle = getMetaTag(doc, "og:title");
                if (linkTitle.equals("Dates") || linkTitle.equals("Downloads")) {
                    linkTitle = "";
                }
                //linkTitle=doc.title();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
