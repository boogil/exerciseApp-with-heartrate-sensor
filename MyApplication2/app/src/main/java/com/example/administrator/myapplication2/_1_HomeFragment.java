package com.example.administrator.myapplication2;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.myapplication2.login.Login;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class _1_HomeFragment extends Fragment {
    TextView tvName,tvLdistance,tvKal,tvRuntime;
    String seq;
    String id;
    public _1_HomeFragment(){}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout._1_activity_main, container, false);

        tvName=(TextView)rootView.findViewById(R.id.tvName);
        tvRuntime=(TextView) rootView.findViewById(R.id.tvRuntime);
        tvLdistance=(TextView)rootView.findViewById(R.id.tvLdistance);
        tvKal=(TextView)rootView.findViewById(R.id.tvKal);

        SharedPreferences myPrefs=this.getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        if((myPrefs != null) && (myPrefs.contains("id"))){
            id=myPrefs.getString("id","");
            tvName.setText(id);
        }else{
            //Set Class to Top of App and no history
            Intent launchNextActivity;
            launchNextActivity = new Intent(getActivity(),Login.class);
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(launchNextActivity);
        }

        getRecentSeq();

        Log.i("boogil1", myPrefs.getString("id", ""));


        return rootView;
    }
    public void getRecentSeq(){

        AsyncHttpClient client1 = new AsyncHttpClient();
        client1.get("http://14.63.219.140:8080/han5/webresources/han5.log/returnseq/" + id, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.i("boogil1", "getRecentSeq receive json data start! ");}
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("boogil1", "getRecentSeq(left) success! ");
                Log.i("boogil1", response.toString());

                try {
                    seq = response.getString("seq");

                    Log.i("boogil1", "seq=" + seq);
                } catch (JSONException e) {}

                getRecentData();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("boogil1", "getRecentSeq(left) fail! ");
            }
        });
    }
    public void getRecentData(){

        AsyncHttpClient client1 = new AsyncHttpClient();
        client1.get("http://14.63.219.140:8080/han5/webresources/han5.log/findone/" + seq, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.i("boogil1", "receive json data start! seq:"+seq);}
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("boogil1", "success! ");
                Log.i("boogil1", response.toString());

                try {
                    String runtime = response.getString("runtime");
                    String ldistance = response.getString("ldistance");
                    String kal = response.getString("kal");


                    tvRuntime.setText(runtime);
                    tvLdistance.setText(ldistance);
                    tvKal.setText(kal);

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
