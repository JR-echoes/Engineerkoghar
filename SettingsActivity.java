package com.engineerkoghar.engineerkoghar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by janardan on 4/8/17.
 */

public class SettingsActivity extends AppCompatActivity {
    LinearLayout setFavs;
    Switch notificationSwitch, darkSwitch;
    static Activity settingsActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsActivity=this;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Settings");

        setFavs=(LinearLayout)findViewById(R.id.setFavsButton);
        setFavs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentFavSet = new Intent(SettingsActivity.this, GeneralActivity.class);
                intentFavSet.putExtra("fragmentID", "favLabelsSet");
                intentFavSet.putExtra("callingActivity","SettingsActivity");
                intentFavSet.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentFavSet);
            }
        });
        //notificationSwitch=(Switch)findViewById(R.id.notification_switch);
        darkSwitch=(Switch)findViewById(R.id.darkmode_switch);
        darkSwitch.setChecked(SplashScreen.isDarkModeSet);
        darkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SplashScreen.isDarkModeSet=isChecked;
                if(isChecked){
                    AppWrapper.setDarkMode(getApplicationContext(),true);

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    recreate();
                    Toast.makeText(getApplicationContext(),"Dark Mode ON",Toast.LENGTH_SHORT).show();
                }else{
                    AppWrapper.setDarkMode(getApplicationContext(),false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    recreate();
                    Toast.makeText(getApplicationContext(),"Dark Mode OFF",Toast.LENGTH_SHORT).show();
                }
                AppWrapper.isSettingChanged=true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
