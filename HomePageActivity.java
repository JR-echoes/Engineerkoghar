package com.engineerkoghar.engineerkoghar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

/**
 * Created by janardan on 11/11/16.
 */

public class HomePageActivity extends AppCompatActivity {

    public static String currFrag = "Recent Posts";
    private DrawerLayout aDrawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle aToggle;
    private FragmentTransaction fragmentTransaction;
    private PendingIntent pendingIntent;
    public static NotificationManager notificationManager;

    public static final int OPEN_NEW_ACTIVITY = 13456;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(AppWrapper.isDarkModeSet(getApplicationContext())){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_page_layout);
        aDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        aToggle = new ActionBarDrawerToggle(this, aDrawerLayout, R.string.open, R.string.close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        aDrawerLayout.addDrawerListener(aToggle);
        aToggle.syncState();

        if (!currFrag.equals("Favourites")) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.main_container, new PostsFragment());
            fragmentTransaction.commit();
        } else {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container, new PostsFragment());
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Favourites");
            currFrag = "Favourites";
        }

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.recentPostsNav:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new PostsFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Recent Posts");
                        currFrag = "Recent Posts";
                        aDrawerLayout.closeDrawers();
                        break;
                    case R.id.favsNav:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new PostsFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Favourites");
                        currFrag = "Favourites";
                        aDrawerLayout.closeDrawers();
                        break;
                    /**
                     case R.id.savedPostsNav:
                     getSupportActionBar().setTitle("Saved Posts");
                     aDrawerLayout.closeDrawers();
                     break;*/
                    case R.id.datesNav:
                        aDrawerLayout.closeDrawers();
                        String datesUrl = "http://www.engineerkoghar.blogspot.com/p/dates.html";
                        Intent intentDates = new Intent(HomePageActivity.this, HtmlActivity.class);
                        intentDates.putExtra("pageUrl", datesUrl);
                        intentDates.putExtra("pageTitle", "Dates");
                        startActivity(intentDates);
                        break;
                    case R.id.downloadsNav:
                        aDrawerLayout.closeDrawers();
                        String dldUrl = "http://engineerkoghar.blogspot.com/p/downloads.html";
                        Intent intentDld = new Intent(HomePageActivity.this, HtmlActivity.class);
                        intentDld.putExtra("pageUrl", dldUrl);
                        intentDld.putExtra("pageTitle", "Downloads");
                        startActivity(intentDld);
                        break;
                    case R.id.settingsNav:
                        aDrawerLayout.closeDrawers();
                        Intent intentSettings = new Intent(HomePageActivity.this, SettingsActivity.class);
                        startActivityForResult(intentSettings, OPEN_NEW_ACTIVITY);
                        //startActivity(intentSettings);
                        /**
                        Intent intentFavSet = new Intent(HomePageActivity.this, GeneralActivity.class);
                        intentFavSet.putExtra("fragmentID", "favLabelsSet");
                        intentFavSet.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intentFavSet);*/
                        break;
                    case R.id.aboutNav:
                        aDrawerLayout.closeDrawers();
                        Intent intentAbout = new Intent(HomePageActivity.this, AboutPage.class);
                        startActivity(intentAbout);
                        break;
                }
                return true;
            }
        });
        setNotificationAlarm();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (aToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_refresh:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container, new PostsFragment());
                fragmentTransaction.commit();
                //getSupportActionBar().setTitle("Recent Posts");
                /**
                 Intent intent = getIntent();
                 intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                 startActivity(intent);
                 finish();*/
                return false;
            case R.id.action_settings:
                Intent intentFavSet = new Intent(HomePageActivity.this, GeneralActivity.class);
                intentFavSet.putExtra("fragmentID", "favLabelsSet");
                intentFavSet.putExtra("callingActivity", "HomePageActivity");
                intentFavSet.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentFavSet);
                return true;
            case R.id.menu_share:
                shareContent();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setNotificationAlarm(){
        Calendar time1=Calendar.getInstance();
        //calendar.set(Calendar.YEAR,2017);
        //calendar.set(Calendar.MONTH,06);
        //calendar.set(Calendar.DAY_OF_MONTH,29);
        time1.setTimeInMillis((System.currentTimeMillis()));
        time1.set(Calendar.HOUR_OF_DAY,7);
        time1.set(Calendar.MINUTE,0);
        time1.set(Calendar.SECOND,0);
        time1.set(Calendar.AM_PM,Calendar.AM);
        if(time1.before(new GregorianCalendar())){
            time1.add(GregorianCalendar.DAY_OF_MONTH,1);
        }
        Intent myIntent = new Intent(HomePageActivity.this,MyReceiver.class);
        pendingIntent= PendingIntent.getBroadcast(HomePageActivity.this,0,myIntent,0);
        AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC,time1.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
        //alarmManager.setInexactRepeating(AlarmManager.RTC,time2.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }

    public static void showNotification(Context context){
        String notifyTitle, notifyDescription, datesUrl;
        Intent intent;
        Random rand=new Random();
        int randNum=rand.nextInt(4)+1;
        switch (randNum){
            case 1:
                notifyTitle="Dates";
                notifyDescription="Tap for current job openings.";
                datesUrl = "http://www.engineerkoghar.blogspot.com/p/dates.html";
                intent = new Intent(context, HtmlActivity.class);
                intent.putExtra("pageUrl", datesUrl);
                intent.putExtra("pageTitle", "Dates");
                intent.putExtra("fromNotify",true);
                break;
            case 2:
                notifyTitle="Engineerको घर";
                notifyDescription="Tap for recent job openings.";
                intent = new Intent(context, HomePageActivity.class);
                break;
            case 3:
                notifyTitle="Dates";
                notifyDescription="Don't miss any job application deadline.";
                datesUrl = "http://www.engineerkoghar.blogspot.com/p/dates.html";
                intent = new Intent(context, HtmlActivity.class);
                intent.putExtra("pageUrl", datesUrl);
                intent.putExtra("pageTitle", "Dates");
                intent.putExtra("fromNotify",true);
                break;
            default:
                notifyTitle="Engineerको घर";
                notifyDescription="Jobs. More.";
                intent = new Intent(context, HomePageActivity.class);
        }

        Bitmap bigIcon = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_stat_logo_notification)
                .setLargeIcon(bigIcon)
                .setColor(Color.BLACK)
                .setContentTitle(notifyTitle)
                .setContentText(notifyDescription);

        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        //When you issue multiple notification about the same type of event, it's best practice for system to be notified
        notificationManager.notify(001,mBuilder.build());
    }

    private void shareContent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Engineerको घर\nwww.engineerkoghar.blogspot.com");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share using"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_NEW_ACTIVITY) {
            if ((resultCode == RESULT_CANCELED)&&(AppWrapper.isSettingChanged)) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                AppWrapper.isSettingChanged=false;
            }
        }
    }
}
