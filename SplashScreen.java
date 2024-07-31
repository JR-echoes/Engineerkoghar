package com.engineerkoghar.engineerkoghar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

public class SplashScreen extends Activity {

    private static int SPLASH_TIME_OUT = 1000; // Splash screen timer
    public static boolean isDarkModeSet;
    private FirebaseAnalytics mFirebaseAnalytics;
    //AppWrapper appWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //appWrapper=new AppWrapper();
        //isDarkModeSet=appWrapper.isDarkModeSet(getApplicationContext());
        //isDarkModeSet=AppWrapper.isDarkModeSet(getApplicationContext());
        if(isDarkModeSet=AppWrapper.isDarkModeSet(getApplicationContext())){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_layout);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start HomePageActivity

                Intent i = new Intent(SplashScreen.this, HomePageActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                finish(); // close this activity
            }
        }, SPLASH_TIME_OUT);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }
}
