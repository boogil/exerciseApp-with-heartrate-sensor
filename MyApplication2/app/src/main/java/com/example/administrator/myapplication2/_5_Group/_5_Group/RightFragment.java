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

package com.example.administrator.myapplication2._5_Group._5_Group;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.administrator.myapplication2.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;


public class RightFragment extends Fragment {

    private GoogleMap map;

    private LatLng curPoint; // 현재 위치 정보

    private ArrayList<LatLng> arrayPoints;
    private ArrayList<LatLng> arrayPoints_network;

    String id="";
    String name="";

    Switch switchBtn;
    boolean switchbool=false;
    String groupName="";

    private ArrayList<HashMap<String,String>> listItem;
    private HashMap<String,String> hsmap;


    private SensorManager mSensorManager;
    LocationManager manager;
    GPSListener gpsListener;
    private boolean mCompassEnabled;

    int count=0;
    int count_camera=1;

    public RightFragment(Context c, IFragmentListener l) {}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		
		View rootView = inflater.inflate(R.layout._5_community_right, container, false);
        switchBtn=(Switch)rootView.findViewById(R.id.switchBtn);


        SharedPreferences myPrefs=this.getActivity().getSharedPreferences("switch", Context.MODE_PRIVATE);
        if((myPrefs != null) && (myPrefs.contains("switch"))){
            switchbool=myPrefs.getBoolean("switch",false);
        }

        switchBtn.setChecked(switchbool);
        listItem = new ArrayList<HashMap<String, String>>();

        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences myPrefs = getActivity().getSharedPreferences("switch", Context.MODE_PRIVATE);
                SharedPreferences.Editor myEditor = myPrefs.edit();
                if(isChecked==true){
                     /*switch 정보 등록*/
                    myEditor.putBoolean("switch",true);
                    myEditor.commit();
                                  /*끝*/
                }else if(isChecked==false){
                    setGpsNull();
                    myEditor.putBoolean("switch",false);
                    myEditor.commit();
                }
            }

        });


        SharedPreferences myPrefs2=this.getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        if((myPrefs2 != null) && (myPrefs2.contains("id"))){
            id=myPrefs2.getString("id","");
            name=myPrefs2.getString("name","");
        }

        getGroupName();


        // 지도 객체 참조
        map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map2)).getMap();
        map.setMyLocationEnabled(true);
        map.setOnMarkerClickListener(onMarkerClickedListener); //마커 항상 표시하기
         /*gps정보*/
        // 센서 관리자 객체 참조 - 센서 매니저 참조
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        if(switchbool==true) {
            // 위치 확인하여 위치 표시 시작
            startLocationService();
        }

		return rootView;
	}


    @Override
    public void onResume() {
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

    public void setGpsNull(){
        //http://14.63.219.140:8080/han5/webresources/han5.nowgps/updateNowgps/{id}/{gpslat}/{gpslng}
        AsyncHttpClient client1 = new AsyncHttpClient();
        client1.get("http://14.63.219.140:8080/han5/webresources/han5.nowgps/updateNowgps/"+id+"/null/null", new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.i("boogil1", "postMapJsonData receive json data start! ");}
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("boogil1", "postMapJsonData success! ");}
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("boogil1", "postMapJsonData fail!");}
        });
    }

    public void getGroupName(){

        AsyncHttpClient client1 = new AsyncHttpClient();
        client1.get("http://14.63.219.140:8080/han5/webresources/han5.grouping/getGroupname/" + id, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.i("boogil1", "getGroupName receive json data start! ");}
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("boogil1", "getGroupName success! ");
                Log.i("boogil1", response.toString());

                try {
                    groupName = response.getString("gname");
                } catch (JSONException e) {}
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("boogil1", "getGroupName fail!");
            }
        });

    }
    /**
     * 현재 위치 확인을 위해 정의한 메소드
     */
    private void startLocationService() {
        // 위치 관리자 객체 참조
        manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

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
        public void onLocationChanged(Location location) {
            // gps확인되면 기존의 마커들 초기화시킴
            // map.clear();

            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            if(count==0) {
                LatLng curll1 = new LatLng(latitude, longitude);
                getGroupsPosition();
                showCurrentLocation(curll1);
            }
            count=1;


            if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            /*경로 배열에 저장*/
                LatLng curll=new LatLng(latitude,longitude);
                postMapJsonData(latitude, longitude);
                if(count_camera % 10 == 0){showCurrentLocation(curll);} // gps데이터 10번에 한번 카메라 이동을 한다.
                count_camera++;

                getGroupsPosition();

            }
           /* LatLng curll=new LatLng(latitude,longitude);
            showCurrentLocation(curll);
            getGroupsPosition();*/
        }

        //GPS가 꺼져있을때 토스트 메시지 창을 띄운다.
        public void onProviderDisabled(String provider) {
            Toast.makeText(getActivity(), "GPS가 꺼져있습니다.", Toast.LENGTH_SHORT).show();
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    }
    private GoogleMap.OnMarkerClickListener onMarkerClickedListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            if (marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
            } else {
                marker.showInfoWindow();
            }
            return true;
        }
    };


    public void showGroupsPosition(){

        HashMap<String,String> tmpmap=new HashMap<>();

        ArrayList<MarkerOptions> arrayMarker=new ArrayList<>();

        if(listItem.size()>=0) {

            map.clear(); //초기화(1/3)

            for (int i = 0; i < listItem.size(); i++) {
                tmpmap = listItem.get(i);

                if (!tmpmap.get("lat").equals("null")) {
                    MarkerOptions marker = new MarkerOptions();//마커 표시 위치 지정
                    LatLng pos = new LatLng(Double.parseDouble(tmpmap.get("lat")), Double.parseDouble(tmpmap.get("lng")));
                    marker.position(pos);
                    if(name.equals(tmpmap.get("id"))){
                        marker.title("●내위치\n");//마커 타이틀
                        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_spot));

                    }else {
                        try {
                            marker.title("●" + URLDecoder.decode(tmpmap.get("id"), "UTF-8") + "\n");//마커 타이틀
                            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_spot2));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    arrayMarker.add(marker);
                }
            }

            for (int i = 0; i < arrayMarker.size(); i++) {
                map.addMarker(arrayMarker.get(i)).showInfoWindow();
            }

            listItem.clear();       //초기화(2/3)
            arrayMarker.clear();    //초기화(3/3)
        }
    }

    private void showCurrentLocation(LatLng curPoint) {
        // 현재 위치를 이용해 LatLon 객체 생성
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));

        // 지도 유형 설정. 지형도인 경우에는 GoogleMap.MAP_TYPE_TERRAIN,
        // 위성 지도인 경우에는 GoogleMap.MAP_TYPE_SATELLITE
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);


    }
    public void getGroupsPosition(){
       // http://14.63.219.140:8080/han5/webresources/han5.nowgps/getGroup/{gname}

        AsyncHttpClient client1 = new AsyncHttpClient();
        client1.get("http://14.63.219.140:8080/han5/webresources/han5.nowgps/getGroup/" + groupName, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.i("boogil1", "getGroupsPosition receive json data start! ");}
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.i("boogil1", "getGroupsPosition success! ");
                Log.i("boogil1", response.toString());

                try {
                    JSONArray jArray = response;

                    for(int i=0; i<jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);

                        hsmap = new HashMap<String, String>();
                        hsmap.put("id", URLDecoder.decode(json_data.getString("name"), "UTF-8"));
                        hsmap.put("lat", json_data.getString("gpslat"));
                        hsmap.put("lng", json_data.getString("gpslng"));

                        listItem.add(hsmap);
                    }
                } catch (JSONException e) {} catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Log.i("boogil1", listItem.toString());


                showGroupsPosition();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i("boogil1", "getGroupMembers fail! ");}
        });
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
                        getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
            }
        }
    };

    public void postMapJsonData(double lat,double lng) {
        //http://14.63.219.140:8080/han5/webresources/han5.nowgps/updateNowgps/{id}/{gpslat}/{gpslng}
        AsyncHttpClient client1 = new AsyncHttpClient();
        client1.get("http://14.63.219.140:8080/han5/webresources/han5.nowgps/updateNowgps/"+id+"/"+lat+"/"+lng, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.i("boogil1", "postMapJsonData receive json data start! ");}
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("boogil1", "postMapJsonData success! ");}
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("boogil1", "postMapJsonData fail!");}
        });
    }
}
