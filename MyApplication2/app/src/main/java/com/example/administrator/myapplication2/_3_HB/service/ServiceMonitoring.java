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

package com.example.administrator.myapplication2._3_HB.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.example.administrator.myapplication2._3_HB.utils.AppSettings;

import java.util.List;

public class ServiceMonitoring {
	
	private static long SERVICE_RESTART_INTERVAL = 60*1000;
	

	/**
	 * Check if specified service is running or not
	 * @param context
	 * @param cls			name of service
	 * @return	boolean		is running or not
	 */
	private static boolean isRunningService(Context context, Class<?> cls) {
		boolean isRunning = false;

		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> info = activityManager.getRunningServices(Integer.MAX_VALUE);

		if (info != null) {
			for(ActivityManager.RunningServiceInfo serviceInfo : info) {
				ComponentName compName = serviceInfo.service;
				String className = compName.getClassName();

				if(className.equals(cls.getName())) {
					isRunning = true;
					break;
				}
			}
		}
		return isRunning;
	}
	
	/**
	 * Start service monitoring to recover from unintended termination
	 * @param context
	 */
	public static void startMonitoring(Context context) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, ServiceMonitoringBR.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), SERVICE_RESTART_INTERVAL, pi);
	}

	/**
	 * Stop service monitoring
	 * @param context
	 */
	public static void stopMonitoring(Context context) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, ServiceMonitoringBR.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		am.cancel(pi);
	}
	
	
	/**
	 *	Broadcast receiver
	 */
	public static class ServiceMonitoringBR extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Logs.d("# Monitoring service");
			
			// Check settings value
			AppSettings.initializeAppSettings(context);
			if(!AppSettings.getBgService()) {
				stopMonitoring(context);
				return;
			}
			// If service is running, start service.
			if(isRunningService(context, BTCTemplateService.class) == false) {
				context.startService(new Intent(context, BTCTemplateService.class));
			}
		}
	}
	
}
