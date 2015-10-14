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

package com.example.administrator.myapplication2._4_Detail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.myapplication2.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class LeftFragment extends Fragment  {
    TextView tvHeart,tvDistance,tvCalorie,tvWeight,tvRuntime;

    String seq="";
    String id="";

	public LeftFragment(Context c, IFragmentListener l, Handler h) {

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout._2_fragment_main_dummy, container, false);

        Intent myIntent = getActivity().getIntent();
        if(myIntent != null){seq=myIntent.getStringExtra("seq");}

        tvHeart=(TextView)rootView.findViewById(R.id.tvHeart);
        tvDistance=(TextView)rootView.findViewById(R.id.tvDistance);
        tvCalorie=(TextView)rootView.findViewById(R.id.tvCalorie);
        tvWeight=(TextView)rootView.findViewById(R.id.tvWeight);;
        tvRuntime=(TextView)rootView.findViewById(R.id.tvRuntime);

        SharedPreferences myPrefs=this.getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        if((myPrefs != null) && (myPrefs.contains("id"))){
            id=myPrefs.getString("id","");
            Log.i("logintest",id.toString());
        }

        getRecentData();

		return rootView;
	}



    public void getRecentData(){

        AsyncHttpClient client1 = new AsyncHttpClient();
        client1.get("http://14.63.219.140:8080/han5/webresources/han5.log/findone/" + seq, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.i("boogil1", "receive json data start! ");}
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("boogil1", "success! ");
                Log.i("boogil1", response.toString());

                try {
                    String hb = response.getString("hb");
                    String ldistance = response.getString("ldistance");
                    String kal = response.getString("kal");
                    String weight = response.getString("weight");
                    String runtime = response.getString("runtime");

                    tvHeart.setText(hb);
                    tvDistance.setText(ldistance);
                    tvCalorie.setText(kal);
                    tvWeight.setText(weight);
                    tvRuntime.setText(runtime);

                    Log.i("boogil1", "seq=" + seq);
                } catch (JSONException e) {}
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("boogil1", "getRecentData(left) seq값: "+seq);
                Log.i("boogil1", "getRecentData(left) fail! ");
            }
        });
    }

}
