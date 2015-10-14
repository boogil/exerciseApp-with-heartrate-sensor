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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.myapplication2.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class RightFragment extends Fragment {

    private GoogleMap map;

    private LatLng curPoint; // 현재 위치 정보

    private ArrayList<LatLng> arrayPoints;
    private ArrayList<LatLng> arrayPoints_network;

    String seq="";
    String id="";

	public RightFragment(Context c, IFragmentListener l) {}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		
		View rootView = inflater.inflate(R.layout._2_fragment_map, container, false);

        Intent myIntent = getActivity().getIntent();
        if(myIntent != null){seq=myIntent.getStringExtra("seq");}

        SharedPreferences myPrefs=this.getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        if((myPrefs != null) && (myPrefs.contains("id"))){
            id=myPrefs.getString("id","");
        }

        // 지도 객체 참조
        map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map2)).getMap();

        map.setMyLocationEnabled(true);

        arrayPoints=new ArrayList<LatLng>();

       // getRecentSeq();
        getRecentData();

		return rootView;
	}

    public void getRecentSeq(){

        AsyncHttpClient client1 = new AsyncHttpClient();
        client1.get("http://14.63.219.140:8080/han5/webresources/han5.log/returnseq/" + id, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.i("boogil1", "receive json data start! ");}
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("boogil1", "getRecentSeq(right) success! ");
                Log.i("boogil1", response.toString());

                try {
                    seq = response.getString("seq");

                    Log.i("boogil1", "seq=" + seq);
                } catch (JSONException e) {}
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("boogil1", "fail! ");
            }
        });
    }

    public void getRecentData(){

        AsyncHttpClient client1 = new AsyncHttpClient();
        client1.get("http://14.63.219.140:8080/han5/webresources/han5.map/getseqlist/" + seq, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.i("boogil1", "receive json data start! ");}
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.i("boogil1", "getRecentData(right) success! ");
                Log.i("boogil1", response.toString());

                try {

                    //JSONArray jArray = response.getJSONArray(""); 그리고 매개변수 JSONObject로 바꿔야댐
                    JSONArray jArray = response;

                    for(int i=0; i<jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        Double lat= Double.valueOf(json_data.getString("gpslat"));
                        Double lng= Double.valueOf(json_data.getString("gpslng"));
                        LatLng point = new LatLng(lat, lng);
                        arrayPoints.add(point);
                    }

                    Log.i("boogil1", "seq=" + seq);

                    if(arrayPoints.size()>0){ showExrLocation(arrayPoints);}
                    //if(arrayPoints_network.size()>0){ showExrLocation_network(arrayPoints_network); } //네트워크 Gps좌표

                } catch (JSONException e) {}
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("boogil1", "getRecentData(right) fail! ");
            }
        });
    }

    public void showExrLocation(ArrayList<LatLng> arrayPoints){
        showCurrentLocation(arrayPoints.get(0));

        //시작점 그리기////////////////////
        //마커 생성
        MarkerOptions marker = new MarkerOptions();
        //마커 표시 위치 지정
        LatLng pos = arrayPoints.get(0);
        marker.position(pos);
        marker.title("●시작점\n");//마커 타이틀
        //마커에 아이콘 표시
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_spot));
        //지도에 마커 등록 및 마커 title/snippet 표시
        map.addMarker(marker).showInfoWindow();



        //경로 그리기///////////////////////
        PolylineOptions line = new PolylineOptions()
                .color(Color.RED)
                .width(10);
        //arrayPoints.add(latLng);
        line.addAll(arrayPoints);
        map.addPolyline(line);

        //종료점 그리기////////////////////
        //마커 생성
        MarkerOptions marker_end = new MarkerOptions();
        //마커 표시 위치 지정
        LatLng pos_end = arrayPoints.get(arrayPoints.size()-1);
        marker_end.position(pos_end);
        marker_end.title("●종료\n");//마커 타이틀
        //마커에 아이콘 표시
        marker_end.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_spot));
        //지도에 마커 등록 및 마커 title/snippet 표시
        map.addMarker(marker_end).showInfoWindow();

    }

    public void showExrLocation_network(ArrayList<LatLng> arrayPoints_network){
        showCurrentLocation(arrayPoints_network.get(0));

        //시작점 그리기////////////////////
        //마커 생성
        MarkerOptions marker = new MarkerOptions();
        //마커 표시 위치 지정
        LatLng pos = arrayPoints_network.get(0);
        marker.position(pos);
        marker.title("●시작\n");//마커 타이틀
        //마커에 아이콘 표시
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_spot));
        //지도에 마커 등록 및 마커 title/snippet 표시
        map.addMarker(marker).showInfoWindow();



        //경로 그리기///////////////////////
        PolylineOptions line = new PolylineOptions()
                .color(Color.BLUE)
                .width(10);
        //arrayPoints.add(latLng);
        line.addAll(arrayPoints_network);
        map.addPolyline(line);


        //종료점 그리기////////////////////
        //마커 생성
        MarkerOptions marker_end = new MarkerOptions();
        //마커 표시 위치 지정
        LatLng pos_end = arrayPoints_network.get(arrayPoints_network.size()-1);
        marker_end.position(pos_end);
        marker_end.title("●종료\n");//마커 타이틀
        //마커에 아이콘 표시
        marker_end.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_spot));
        //지도에 마커 등록 및 마커 title/snippet 표시
        map.addMarker(marker_end).showInfoWindow();

    }

    private void showCurrentLocation(LatLng curPoint) {
        // 현재 위치를 이용해 LatLon 객체 생성
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));

        // 지도 유형 설정. 지형도인 경우에는 GoogleMap.MAP_TYPE_TERRAIN,
        // 위성 지도인 경우에는 GoogleMap.MAP_TYPE_SATELLITE
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);


    }
	
}
