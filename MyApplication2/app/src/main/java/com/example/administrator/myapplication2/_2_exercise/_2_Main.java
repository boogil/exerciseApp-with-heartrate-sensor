package com.example.administrator.myapplication2._2_exercise;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;

import com.example.administrator.myapplication2.Main;
import com.example.administrator.myapplication2.R;
import com.example.administrator.myapplication2._2_exercise._2_Status_heart._2_Status_heart_Main;

/**
 * Created by GIL on 2015-09-21.
 */
public class _2_Main extends FragmentActivity implements View.OnClickListener{
    LocationManager manager;
    GPSListener gpsListener;
    public static boolean gpsOnBoolean = true;
    boolean radio = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout._2_activity_main);

        SharedPreferences myPrefs=this.getSharedPreferences("radio", Context.MODE_PRIVATE);
        if((myPrefs != null) && (myPrefs.contains("radio"))){
            radio=myPrefs.getBoolean("radio",false);
        }

        ImageButton ibStart = (ImageButton)findViewById(R.id.ibStart);

        ibStart.setOnClickListener(this);

        startLocationService();

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ibStart && gpsOnBoolean==true){
            manager.removeUpdates(gpsListener); //리스너 자원 해제
            if(radio == false) {
                startActivity(new Intent(getApplicationContext(), _2_Status.class));
            }else if(radio == true){
                startActivity(new Intent(getApplicationContext(), _2_Status_heart_Main.class));
            }
        }

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

    private void startLocationService() {
        // 위치 관리자 객체 참조
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 위치 정보를 받을 위치 리스너 객체 생성
        gpsListener = new GPSListener();
        long minTime = 5000;//GPS정보 전달 시간 지정 - 20초마다 위치정보 전달
        float minDistance = 1;//이동거리 지정(10m)

        // GPS 기반 위치 요청
        manager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime,
                minDistance,
                gpsListener);

        // 네트워크 기반 위치 요청
        manager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                minTime,
                minDistance,
                gpsListener);

    }

    /**
     * 위치 리스너 클래스 정의
     */
    private class GPSListener implements LocationListener {
        /**
         * 위치 정보가 확인되었을 때 호출되는 메소드
         */


        //GPS가 꺼져있을때 토스트 메시지 창을 띄운다.
        public void onProviderDisabled(String provider) {

            gpsOnBoolean=false;

            new AlertDialog.Builder(_2_Main.this)
                                      .setMessage("GPS가 꺼져있습니다.\n‘위치 서비스’에서 ‘Google 위치 서비스’를 체크해주세요")
                                      .setPositiveButton("설정", new DialogInterface.OnClickListener() {
                                          // 설정 창을 띄운다
                                          @Override
                                          public void onClick(DialogInterface dialog, int which) {
                                              Intent intent1 = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                                              startActivity(intent1);
                                          }
                                      })
                                    .setNegativeButton("취소", null).show();
        }

        public void onProviderEnabled(String provider) {
            gpsOnBoolean=true;
        }

        @Override
        public void onLocationChanged(Location location) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    }
}
