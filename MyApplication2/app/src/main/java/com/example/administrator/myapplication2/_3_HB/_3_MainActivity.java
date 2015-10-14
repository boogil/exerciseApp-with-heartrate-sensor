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

package com.example.administrator.myapplication2._3_HB;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplication2.Main;
import com.example.administrator.myapplication2.R;
import com.example.administrator.myapplication2._3_HB.fragments.ExampleFragment;
import com.example.administrator.myapplication2._3_HB.fragments.FragmentAdapter;
import com.example.administrator.myapplication2._3_HB.fragments.IFragmentListener;
import com.example.administrator.myapplication2._3_HB.service.BTCTemplateService;
import com.example.administrator.myapplication2._3_HB.utils.AppSettings;
import com.example.administrator.myapplication2._3_HB.utils.Constants;
import com.example.administrator.myapplication2._3_HB.utils.Logs;
import com.example.administrator.myapplication2._3_HB.utils.RecycleUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;



public class _3_MainActivity extends FragmentActivity implements ActionBar.TabListener, IFragmentListener {
    //심박수 정보가 나누어 질때의 문제점을 해결하기 위한 변수
    private static String a="";
    // Debugging
    private static final String TAG = "RetroWatchActivity";
    
	// Context, System
	private Context mContext;
	private BTCTemplateService mService;
	private ActivityHandler mActivityHandler;
	
	// Global
	
	// UI stuff
	private FragmentManager mFragmentManager;
	private FragmentAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	
	private ImageView mImageBT = null;
	private TextView mTextStatus = null;

	// Refresh timer
	private Timer mRefreshTimer = null;



	/*****************************************************
	 *	 Overrided methods
	 ******************************************************/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_ACTION_BAR);
		//----- System, Context
		mContext = this;	//.getApplicationContext();
		mActivityHandler = new ActivityHandler();
		AppSettings.initializeAppSettings(mContext);

        setContentView(R.layout._3_activity_main);

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
		
		// Setup views
		mImageBT = (ImageView) findViewById(R.id.status_title);
		mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_invisible));
		mTextStatus = (TextView) findViewById(R.id.status_text);
		mTextStatus.setText(getResources().getString(R.string.bt_state_init));
		
		// Do data initialization after service started and binded
		doStartService();
	}

	@Override
	public synchronized void onStart() {
		super.onStart();
	}
	
	@Override
	public synchronized void onPause() {
		super.onPause();
	}
	
	@Override
	public void onStop() {
		// Stop the timer
		if(mRefreshTimer != null) {
			mRefreshTimer.cancel();
			mRefreshTimer = null;
		}
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		finalizeActivity();
	}
	
	@Override
	public void onLowMemory (){
		super.onLowMemory();
		// onDestroy is not always called when applications are finished by Android system.
		finalizeActivity();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_scan:
			// Launch the DeviceListActivity to see devices and do scan
			doScan();
			return true;
		case R.id.action_discoverable:
			// Ensure this device is discoverable by others
			ensureDiscoverable();
			return true;
		}
		return false;
	}

    @Override
    public void onBackPressed() {

        ExampleFragment frg = (ExampleFragment) mSectionsPagerAdapter.getItem(FragmentAdapter.FRAGMENT_POS_EXAMPLE);
        frg.stopThread();
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
			if(mService != null)
				mService.startServiceMonitoring();
			break;
		case IFragmentListener.CALLBACK_SEND_MESSAGE:
			if(mService != null && arg2 != null)
				mService.sendMessageToRemote(arg2);

		default:
			break;
		}
	}
	
	
	/*****************************************************
	 *	Private methods
	 ******************************************************/
	
	/**
	 * Service connection
	 */
	private ServiceConnection mServiceConn = new ServiceConnection() {
		
		public void onServiceConnected(ComponentName className, IBinder binder) {
			Log.d(TAG, "Activity - Service connected");
			
			mService = ((BTCTemplateService.ServiceBinder) binder).getService();
			
			// Activity couldn't work with mService until connections are made
			// So initialize parameters and settings here. Do not initialize while running onCreate()
			initialize();
		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
		}
	};
	
	/**
	 * Start service if it's not running
	 */
	private void doStartService() {
		Log.d(TAG, "# Activity - doStartService()");
		startService(new Intent(this, BTCTemplateService.class));
		bindService(new Intent(this, BTCTemplateService.class), mServiceConn, Context.BIND_AUTO_CREATE);
	}
	
	/**
	 * Stop the service
	 */
	private void doStopService() {
		Log.d(TAG, "# Activity - doStopService()");
		mService.finalizeService();
		stopService(new Intent(this, BTCTemplateService.class));
	}
	
	/**
	 * Initialization / Finalization
	 */
	private void initialize() {
		Logs.d(TAG, "# Activity - initialize()");
		mService.setupService(mActivityHandler);
		
		// If BT is not on, request that it be enabled.
		// RetroWatchService.setupBT() will then be called during onActivityResult
		if(!mService.isBluetoothEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
		}
		
		// Load activity reports and display
		if(mRefreshTimer != null) {
			mRefreshTimer.cancel();
		}
		
		// Use below timer if you want scheduled job
		//mRefreshTimer = new Timer();
		//mRefreshTimer.schedule(new RefreshTimerTask(), 5*1000);
	}
	
	private void finalizeActivity() {
		Logs.d(TAG, "# Activity - finalizeActivity()");
		
		if(!AppSettings.getBgService()) {
			doStopService();
		} else {
		}

		// Clean used resources
		RecycleUtils.recursiveRecycle(getWindow().getDecorView());
		System.gc();
	}
	
	/**
	 * Launch the DeviceListActivity to see devices and do scan
	 */
	private void doScan() {
		Intent intent = new Intent(this, DeviceListActivity.class);
		startActivityForResult(intent, Constants.REQUEST_CONNECT_DEVICE);
	}
	
	/**
	 * Ensure this device is discoverable by others
	 */
	private void ensureDiscoverable() {
		if (mService.getBluetoothScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(intent);
		}
	}
	
	
	/*****************************************************
	 *	Public classes
	 ******************************************************/
	
	/**
	 * Receives result from external activity
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Logs.d(TAG, "onActivityResult " + resultCode);

		switch(requestCode) {
		case Constants.REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				// Attempt to connect to the device
				if(address != null && mService != null)
					mService.connectDevice(address);
			}
			break;
			
		case Constants.REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a BT session
				mService.setupBT();
			} else {
				// User did not enable Bluetooth or an error occured
				Logs.e(TAG, "BT is not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
			}
			break;
		}	// End of switch(requestCode)
	}
	
	
	
	/*****************************************************
	 *	Handler, Callback, Sub-classes
	 ******************************************************/
	
	public class ActivityHandler extends Handler {
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what) {
			// Receives BT state messages from service 
			// and updates BT state UI
			case Constants.MESSAGE_BT_STATE_INITIALIZED:
				mTextStatus.setText(getResources().getString(R.string.bt_title) + ": " + 
						getResources().getString(R.string.bt_state_init));
				mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_invisible));
				break;
			case Constants.MESSAGE_BT_STATE_LISTENING:
				mTextStatus.setText(getResources().getString(R.string.bt_title) + ": " + 
						getResources().getString(R.string.bt_state_wait));
				mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_invisible));
				break;
			case Constants.MESSAGE_BT_STATE_CONNECTING:
				mTextStatus.setText(getResources().getString(R.string.bt_title) + ": " + 
						getResources().getString(R.string.bt_state_connect));
				mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_away));
				break;
			case Constants.MESSAGE_BT_STATE_CONNECTED:
				if(mService != null) {
					String deviceName = mService.getDeviceName();
					if(deviceName != null) {
						mTextStatus.setText(getResources().getString(R.string.bt_title) + ": " + 
								getResources().getString(R.string.bt_state_connected) + " " + deviceName);
						mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_online));
					}
				}
				break;
			case Constants.MESSAGE_BT_STATE_ERROR:
				mTextStatus.setText(getResources().getString(R.string.bt_state_error));
				mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_busy));
				break;
			
			// BT Command status
			case Constants.MESSAGE_CMD_ERROR_NOT_CONNECTED:
				mTextStatus.setText(getResources().getString(R.string.bt_cmd_sending_error));
				mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_busy));
				break;
				
			///////////////////////////////////////////////
			// When there's incoming packets on bluetooth
			// do the UI works like below
			///////////////////////////////////////////////
			case Constants.MESSAGE_READ_CHAT_DATA:
				if(msg.obj != null) {
					ExampleFragment frg = (ExampleFragment) mSectionsPagerAdapter.getItem(FragmentAdapter.FRAGMENT_POS_EXAMPLE);

					frg.showMessage((String)msg.obj);


                   String msgValue = (String)msg.obj;


                   // Log.i("boogil",msgValue+", 사이즈: "+msgValue.length());


/*
                    //조건을 줘서 몇글자 이상이면 밑에꺼 실행하는 걸 넣자 (if문)
                    if(msgValue.length()==1){
                        a+=msgValue;
                    }else{
                    //if(msgValue.length() >=2 ) {
                        try {
                            String tmp=a+msgValue;
                            JSONStringer json = new JSONStringer()
                                    .object()
                                    .key("name").value(tmp)
                                    *//*.key("member_id").value("a")
                                    .key("member_pwd").value("b")
                                    .key("member_name").value("c")
                                    .key("member_gen").value("male")
                                    .key("member_birth").value("2015-11-06")
                                    .key("member_email").value("toy116@naver.com")*//*
                                    .endObject();
                            String b = json.toString();
                            Log.i("boogil", b);


                            StringEntity entity = new StringEntity(b + "\n", HTTP.UTF_8);

                            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

                            HttpPostDateTest(entity);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        a="";  // a변수 초기화
                    }*/
				}
				break;

			default:
				break;
			}

			super.handleMessage(msg);
		}
	}	// End of class ActivityHandler

    public void HttpPostDateTest(StringEntity entity){
        Log.i("boogil", "httpPost 도입1: " + entity.toString());

              StringEntity entity1 = entity;


        AsyncHttpClient client = new AsyncHttpClient();

        client.post(this.getApplicationContext(),"http://14.63.219.140:8080/fstepApp/webresources/fsteppack.test",entity1,  "application/json",new JsonHttpResponseHandler() {
        //client.post(this.getApplicationContext(),"http://14.63.219.140:8080/fstepApp/webresources/fsteppack.member1",entity1,  "application/json",new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
                // TODO Auto-generated method stub
                Log.i("boogil", "onStart! ");

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("boogil", "success!! ");

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.i("boogil", "success!! ");
            }



            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("boogil", "fail3!! ");

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.i("boogil", "success!! ");
            }


        });


        AsyncHttpClient client1 = new AsyncHttpClient();
        client1.get("http://14.63.219.140:8080/fstepApp/webresources/fsteppack.test/alarm/0",new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                // TODO Auto-generated method stub
                Log.i("boogil1", "onStart! ");

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("boogil1", "success!! ");
                Log.i("boogil1", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.i("boogil1", "success!! ");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.i("boogil1", "success!! ");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("boogil1", "fail3!! ");

            }
        });



    }
    /**
     * Auto-refresh Timer
     */
	private class RefreshTimerTask extends TimerTask {
		public RefreshTimerTask() {}
		
		public void run() {
			mActivityHandler.post(new Runnable() {
				public void run() {
					// TODO:
					mRefreshTimer = null;
				}
			});
		}
	}

}
