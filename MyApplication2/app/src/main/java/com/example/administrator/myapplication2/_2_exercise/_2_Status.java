package com.example.administrator.myapplication2._2_exercise;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplication2.R;
import com.example.administrator.myapplication2._2_exercise._2_End._2_EndMain;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by GIL on 2015-09-21.
 */
public class _2_Status extends FragmentActivity implements View.OnClickListener {
    TextView tvTime;
    boolean someCondition = true;
    Runnable m_display_run;
    long m_current_time, m_start_time;

    String m_display_string;
    Timer m_timer;

    /*gps정보*/
    private GoogleMap map;
    private SensorManager mSensorManager;
    private boolean mCompassEnabled;

    LocationManager manager;
    GPSListener gpsListener;

    private LatLng curPoint; // 현재 위치 정보
    private ArrayList<LatLng> arrayPoints;
    private ArrayList<LatLng> arrayPoints_network;

    private int sendingMinute = 0;
    private int hours = 0;

    ImageButton ibPause, ibStop;

    private double totalDistance = 0; //총 이동거리
    private double totalCalorie = 0; //최종 칼로리

    String expect_weight, distance;

    String id = "";
    String seq = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout._2_status);

        tvTime = (TextView) findViewById(R.id.tvTime);
        ibPause = (ImageButton) findViewById(R.id.ibPause);
        ibStop = (ImageButton) findViewById(R.id.ibStop);

        ibPause.setOnClickListener(this);
        ibStop.setOnClickListener(this);


        m_display_run = new Runnable() {
            public void run() {
// 텍스트뷰는 시간을 출력한다.
                tvTime.setText(m_display_string);
            }
        };

        /*gps정보*/
        // 센서 관리자 객체 참조 - 센서 매니저 참조
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        arrayPoints = new ArrayList<LatLng>();
        arrayPoints_network = new ArrayList<LatLng>();

        postJsonData1();


        // 위치 확인하여 위치 표시 시작
        startLocationService();

    }

    @Override
    protected void onResume() {
        super.onResume();

        //화면에 액티비티가 보일 시점에서 센서 매니저에 센서 리스너 등록
        if (mCompassEnabled) {
            mSensorManager.registerListener(mListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                    SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //화면의 액티비티가 중지될 시점에서 센서 매니저의 센서 리스너 해제
        if (mCompassEnabled) {
            mSensorManager.unregisterListener(mListener);
        }
    }

    /**
     * 현재 위치 확인을 위해 정의한 메소드
     */
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

     /*   // 위치 확인이 안되는 경우에도 최근에 확인된 위치 정보 먼저 확인
        try {
            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                Double latitude = lastLocation.getLatitude();
                Double longitude = lastLocation.getLongitude();

            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }*/

    }

    /**
     * 위치 리스너 클래스 정의
     */
    private class GPSListener implements LocationListener {
        /**
         * 위치 정보가 확인되었을 때 호출되는 메소드
         */
        public void onLocationChanged(Location location) {
            // gps확인되면 기존의 마커들 초기화시킴
            // map.clear();

            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();


            if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {

            /*경로 배열에 저장*/
                LatLng point = new LatLng(latitude, longitude);

                postMapJsonData(latitude, longitude);

                arrayPoints.add(point);

                Log.i("arrayPoints", "array:  " + arrayPoints.toString());

            } else {
                //Network 위치제공자에 의한 위치변화
                //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.

                /*경로 배열에 저장*/
                LatLng point = new LatLng(latitude, longitude);
                arrayPoints_network.add(point);
            }
        }

        //GPS가 꺼져있을때 토스트 메시지 창을 띄운다.
        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "GPS가 꺼져있습니다.", Toast.LENGTH_SHORT).show();
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    }

    /**
     * 센서의 정보를 받기 위한 센서 리스너 객체 생성
     */
    private final SensorEventListener mListener = new SensorEventListener() {
        private int iOrientation = -1;

        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        // 센서의 값을 받을 수 있도록 호출되는 메소드 - 센서값이 바뀔 때 마다 호출
        public void onSensorChanged(SensorEvent event) {
            if (iOrientation < 0) {
                iOrientation = ((WindowManager)
                        getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
            }
        }
    };


    /*스톱워치 소스*/
    class UpdateTimeTask extends TimerTask {
        public void run() {
// 현재 시간을 밀리초 단위로 얻어온다.
            m_current_time = System.currentTimeMillis();
// 현재 시간에서 시작 시간을 빼서 경과시간을 얻는다.
            long millis = m_current_time - m_start_time;
// 밀리초에 1000을 나누어 초단위로 변경한다.
            int seconds = (int) (millis / 1000);
// 초단위의 시간정보를 60으로 나누어 분단위로 변경한다.
            int minutes = seconds / 60;
// 초단위의 시간정보를 60으로 나머지 연산하여 초 값을 얻는다.
            seconds = seconds % 60;
// 밀리초 단위의 시간정보를 10으로 나눈 몫에 100으로 나머지 연산을 하여
// 100의 자리와 10의 자리만 출력될 수 있도록 한다.
            millis = (millis / 10) % 100;
            if (minutes <= 60) {
                // 출력할 문자열을 구성한다.
                m_display_string = String.format("%02d:%02d:%02d", minutes, seconds, millis);
            } else {
                hours += 1; //60분 지나면 시간증가
                m_display_string = String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, millis);
            }
// 해당 문자열을 출력하도록 메인 쓰레드에 전달한다.
            tvTime.post(m_display_run);

            sendingMinute = minutes;
            Log.i("boogill", "beforebefore: " + String.valueOf(sendingMinute));


        }
    }

    ;

    public void startStopWatch() {
        if (someCondition == true) {
// 현재 시간에서 이전에 중지했던 시간(경과시간 - 시작시간)을 뺀다.
// 5초에서 중지를 누른 후 다시 시작을 누르면 1초후에 6초가 될 수 있도록 연산한다.
            m_start_time = System.currentTimeMillis() - (m_current_time - m_start_time);
// 타이머 객체를 생성한다.
            m_timer = new Timer();
// 사용자정의 TimerTask 객체를 넘겨주고, 100 밀리초 후에 수행되며,
// 200 밀리마다 반복 수행되도록 설정한다.
            m_timer.schedule(new UpdateTimeTask(), 100, 200);
// Reset 버튼의 문자열(캡션명)을 Record 로 변경한다.
            ibPause.setImageResource(R.drawable._2_pause);
        } else if (someCondition == false) {
// 해당 타이머가 수행할 모든 행위들을 정지시킵니다.
            m_timer.cancel();

// Record 버튼의 문자열(캡션명)을 Reset 로 변경한다.
            ibPause.setImageResource(R.drawable._2_continue);
        }
    }

    public void stopWatch() {
        // 해당 타이머가 수행할 모든 행위들을 정지시킵니다.
        m_timer.cancel();
// 대기중이던 취소된 행위가 있는 경우 모두 제거한다.
        m_timer.purge();
        m_timer = null;

    }


    @Override
    protected void onStart() {
        super.onStart();

        startStopWatch();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibPause:
                if (someCondition == true) {
                    someCondition = false;
                } else if (someCondition == false) {
                    someCondition = true;
                }
                startStopWatch();
                break;

            case R.id.ibStop:

                Intent myIntent = new Intent(getApplicationContext(), _2_EndMain.class);
                myIntent.putExtra("seq", seq);

                dataProcess();

                postJsonData2(); //위에서 가져온 seq값으로 update

                stopWatch();
                manager.removeUpdates(gpsListener); //리스너 자원 해제
                startActivity(myIntent);
                break;

        }
    }

    public void dataProcess() {
        totalCalorie = calCalrorie(60, (double) (sendingMinute + hours * 60) / 60);

                 /* 예상 체중감량 공식 (x:예상체중감량 y:소모한 칼로리)
       1kg : 7,700kcal = x : y
       따라서 x = 1/7700 * y * 1000 (g/그람)
       */
        expect_weight = String.valueOf((int) ((double) 1 / 7700 * (int) totalCalorie * 1000));  //예상 체중감량 값

        if (arrayPoints.size() > 1) {
            for (int i = 0; i < arrayPoints.size() - 1; i++)
                totalDistance += calDistance(arrayPoints.get(i), arrayPoints.get(i + 1));
        }
        double tmp = ((int) totalDistance) / (double) 1000;
        double tmp2 = Math.round(tmp * 10) / (double) 10;
        distance = String.valueOf(tmp2);
    }

    //칼로리 계산
    public double calCalrorie(double weight, double hour) {
        return (6.5 * hour * weight * 1.05);
    }

    //두 좌표의 거리를 구하는 함수
    public double calDistance(LatLng lt1, LatLng lt2) {
        double distance;

        Location locationA = new Location("point A");

        locationA.setLatitude(lt1.latitude);
        locationA.setLongitude(lt1.longitude);

        Location locationB = new Location("point B");

        locationB.setLatitude(lt2.latitude);
        locationB.setLongitude(lt2.longitude);

        distance = locationA.distanceTo(locationB);

        return distance;
    }

    public void getRecentSeq() {
        AsyncHttpClient client1 = new AsyncHttpClient();
        client1.get("http://14.63.219.140:8080/han5/webresources/han5.log/returnseq/" + id, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.i("boogil1", "receive json data start! ");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("boogil1", "success! ");
                Log.i("boogil1", response.toString());

                try {
                    seq = response.getString("seq");

                    Log.i("boogil1", "seq=" + seq);
                } catch (JSONException e) {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("boogil1", "fail! ");
            }
        });
    }

    public void postJsonData1() {
        try {

            SharedPreferences myPrefs = getSharedPreferences("login", Context.MODE_PRIVATE);
            if ((myPrefs != null) && (myPrefs.contains("id"))) {
                id = myPrefs.getString("id", "");
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
            String str_date = df.format(new Date());

            RequestParams a = new RequestParams();
            a.put("id", id);
            a.put("ldate", str_date);
            a.put("ldistance", "0.0");
            a.put("kal", "0");
            a.put("hb", "0");
            a.put("weight", "0");
            a.put("runtime","0");
            a.setUseJsonStreamer(true);

            Log.i("boogil", a.toString());

            HttpPostDateTest1(a);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void HttpPostDateTest1(RequestParams rp) {
        AsyncHttpClient client = new AsyncHttpClient();


        client.post(this.getApplicationContext(), "http://14.63.219.140:8080/han5/webresources/han5.log", null, rp, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
                // TODO Auto-generated method stub
                Log.i("boogil", "onStart! ");

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("boogil", "success ");

                getRecentSeq();   //최근 seq가져온다.(처음에 insert한 레코드 찾기)

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("boogil", "fail ");

            }
        });

    }

    public void postJsonData2() {
        try {
            AsyncHttpClient client = new AsyncHttpClient();
/*
        http://14.63.219.140:8080/han5/webresources/han5.log/updatelog/
        {ldistance}/{kal}/{hb}/{weight}/{seq}*/

            int updateCalorie= (int) Math.round(totalCalorie);

            client.get("http://14.63.219.140:8080/han5/webresources/han5.log/updatelog/" + distance + "/" + updateCalorie + "/0/" +
                    expect_weight + "/" +seq+"/"+m_display_string
                   , new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    // called before request is started
                    // TODO Auto-generated method stub
                    Log.i("boogil", "postJsonData2 onStart! ");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.i("boogil", "postJsonData2 success ");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.i("boogil", "postJsonData2 fail ");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void postMapJsonData(Double latitude, Double longitude) {

        String lat= String.valueOf(latitude);
        String lng= String.valueOf(longitude);

        RequestParams rp = new RequestParams();
        rp.put("seq", seq);
        rp.put("gpslat", lat);
        rp.put("gpslng", lng);
        rp.setUseJsonStreamer(true);

        AsyncHttpClient client = new AsyncHttpClient();


        client.post(this.getApplicationContext(), "http://14.63.219.140:8080/han5/webresources/han5.map", null, rp, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
                // TODO Auto-generated method stub
                Log.i("boogil", "onStart! ");

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("boogil", "success ");

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("boogil", "fail ");

            }
        });
    }


}


