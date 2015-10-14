/*
 * Copyright (C) 2014 Bluetooth Connection Template
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.administrator.myapplication2._2_exercise._2_End;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.example.administrator.myapplication2.Main;
import com.example.administrator.myapplication2.R;

import java.util.TimerTask;

public class _2_EndMain extends FragmentActivity implements ActionBar.TabListener, IFragmentListener {
    //심박수 정보가 나누어 질때의 문제점을 해결하기 위한 변수
    private static String a="";
    // Debugging
    private static final String TAG = "RetroWatchActivity";

    // Context, System
    private Context mContext;
    private ActivityHandler mActivityHandler;

    // Global

    // UI stuff
    private FragmentManager mFragmentManager;
    private FragmentAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;





    /*****************************************************
     *	 Overrided methods
     ******************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //----- System, Context
        mContext = this;	//.getApplicationContext();
        mActivityHandler = new ActivityHandler();
        AppSettings.initializeAppSettings(mContext);

        setContentView(R.layout._2_end_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the primary sections of the app.
        mFragmentManager = getSupportFragmentManager();
        mSectionsPagerAdapter = new FragmentAdapter(mFragmentManager, mContext, this, mActivityHandler);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            actionBar.addTab(actionBar.newTab()
                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan:
                // Launch the DeviceListActivity to see devices and do scan

                return true;
            case R.id.action_discoverable:
                // Ensure this device is discoverable by others

                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        //Set Class to Top of App and no history
        Intent launchNextActivity;
        launchNextActivity = new Intent(this,Main.class);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(launchNextActivity);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        // This prevents reload after configuration changes
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Implements TabListener
     */
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void OnFragmentCallback(int msgType, int arg0, int arg1, String arg2, String arg3, Object arg4) {
        switch(msgType) {
            case IFragmentListener.CALLBACK_RUN_IN_BACKGROUND:


                break;
            case IFragmentListener.CALLBACK_SEND_MESSAGE:


            default:
                break;
        }
    }


    /*****************************************************
     *	Private methods
     ******************************************************/






    /*****************************************************
     *	Handler, Callback, Sub-classes
     ******************************************************/

    public class ActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg)
        {


            super.handleMessage(msg);
        }
    }	// End of class ActivityHandler


    /**
     * Auto-refresh Timer
     */
    private class RefreshTimerTask extends TimerTask {
        public RefreshTimerTask() {}

        public void run() {
            mActivityHandler.post(new Runnable() {
                public void run() {

                }
            });
        }
    }



}
