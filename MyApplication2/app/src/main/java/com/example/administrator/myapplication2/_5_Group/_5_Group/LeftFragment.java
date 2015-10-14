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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.administrator.myapplication2.Main;
import com.example.administrator.myapplication2.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class LeftFragment extends Fragment  {
    TextView tvGroupName,tvGroupMember;

    String id="";
    String groupName="";

    String names="";

    ImageButton goutBtn;
	public LeftFragment(Context c, IFragmentListener l, Handler h) {

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout._5_community_left, container, false);

        SharedPreferences myPrefs=this.getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        if((myPrefs != null) && (myPrefs.contains("id"))){
            id=myPrefs.getString("id","");
            Log.i("logintest", id.toString());
        }


        tvGroupName=(TextView)rootView.findViewById(R.id.tvGroupName);
        tvGroupMember=(TextView)rootView.findViewById(R.id.tvGroupMember);

        goutBtn=(ImageButton)rootView.findViewById(R.id.goutBtn);
        goutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("그룹탈퇴")
                        .setMessage("정말 탈퇴하시겠습니까?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                outGroup();
                            }
                        }).create().show();
            }
        });

        getGroupName();

		return rootView;

	}


    public void outGroup(){

        AsyncHttpClient client1 = new AsyncHttpClient();
        client1.get("http://14.63.219.140:8080/han5/webresources/han5.grouping/deleteGrouping/" + id, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.i("boogil1", "outGroup receive json data start! ");}
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("boogil1", "outGroup success! ");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("boogil1", "outGroup fail!");
            }
        });

        AsyncHttpClient client2 = new AsyncHttpClient();
        client2.get("http://14.63.219.140:8080/han5/webresources/han5.nowgps/deleteNowgps/" + id, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.i("boogil1", "outGroup2 receive json data start! ");}
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("boogil1", "outGroup2 success! ");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("boogil1", "outGroup2 fail!");
            }
        });

            //Set Class to Top of App and no history
            Intent launchNextActivity;
            launchNextActivity = new Intent(getActivity(),Main.class);
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(launchNextActivity);

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

                    tvGroupName.setText(groupName);

                    getGroupMembers();

                } catch (JSONException e) {}
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("boogil1", "getGroupName fail!");
            }
        });

    }


    public void getGroupMembers(){
        AsyncHttpClient client1 = new AsyncHttpClient();
        client1.get("http://14.63.219.140:8080/han5/webresources/han5.grouping/getNamelist/" + groupName, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.i("boogil1", "getGroupName receive json data start! ");}
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.i("_4_", "getRecentData(right) success! ");
                Log.i("_4_", response.toString());

                try {

                    JSONArray jArray = response;


                    for(int i=0; i<jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);

                        String name=json_data.getString("name");
                        String kor_name= URLDecoder.decode(name.toString(),"UTF-8");
                        names+=kor_name+"\n";

                    }

                    tvGroupMember.setText(names);

                } catch (JSONException e) {} catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i("boogil1", "getGroupMembers fail! ");}


        });


    }

}
