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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.administrator.myapplication2._5_Group._5_Group._5_GroupMain;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class _5_GroupFragment extends Fragment implements View.OnClickListener {
    EditText etGroup;
    ImageButton createBtn,joinBtn;
    String id,name;

    boolean isGroup = false;

	public _5_GroupFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout._5_community_main, container, false);

        SharedPreferences myPrefs=this.getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        if((myPrefs != null) && (myPrefs.contains("id"))){
            id=myPrefs.getString("id","");
            name=myPrefs.getString("name","");
        }

        etGroup = (EditText) rootView.findViewById(R.id.etGroup);
        createBtn = (ImageButton)rootView.findViewById(R.id.createBtn);
        joinBtn = (ImageButton)rootView.findViewById(R.id.joinBtn);

        createBtn.setOnClickListener(this);
        joinBtn.setOnClickListener(this);

        checkGroup();

        if(isGroup==true) {
            Intent myIntent = new Intent(getActivity(), _5_GroupMain.class);
            startActivity(myIntent);

            return null;
        }



            return rootView;


    }


    public void checkGroup(){
        AsyncHttpClient client1 = new AsyncHttpClient();
        client1.get("http://14.63.219.140:8080/han5/webresources/han5.grouping/getGroupname/" + id, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.i("boogil1", "checkGroup receive json data start! ");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("boogil1", "checkGroup success! ");
                Log.i("boogil1", response.toString());

                isGroup=true;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("boogil1", "checkGroup fail! ");
                isGroup=false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.createBtn:
                final String str=etGroup.getText().toString();

                AsyncHttpClient client1 = new AsyncHttpClient();
                client1.get("http://14.63.219.140:8080/han5/webresources/han5.grouping/gnamecheck/" + str, new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        Log.i("boogil1", "createBtn receive json data start! ");
                        if (str.equals("")) {
                            Toast.makeText(getActivity(), "그룹명을 입력하세요.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.i("boogil1", "createBtn success! ");
                        Log.i("boogil1", response.toString());
                        Toast.makeText(getActivity(), "이미 생성된 그룹입니다.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.i("boogil1", "createBtn fail! ");
                        if(!str.equals("")) {
                            makeNowgps();
                            makeGrouping(str);
                        }

                    }
                });
                break;

            case R.id.joinBtn:
                final String str2=etGroup.getText().toString();

                AsyncHttpClient client2 = new AsyncHttpClient();
                client2.get("http://14.63.219.140:8080/han5/webresources/han5.grouping/gnamecheck/" + str2, new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        Log.i("boogil1", "joinBtn receive json data start! ");
                        if (str2.equals("")) {
                            Toast.makeText(getActivity(), "그룹명을 입력하세요.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.i("boogil1", "joinBtn success! ");
                        Log.i("boogil1", response.toString());

                        makeNowgps();
                        makeGrouping(str2);


                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.i("boogil1", "joinBtn fail! ");
                        if(!str2.equals(""))
                        Toast.makeText(getActivity(), "존재하지 않는 그룹입니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

    public void makeNowgps(){



        RequestParams a = new RequestParams();
        a.put("id", id);
        try {
            a.put("name", URLEncoder.encode(name, "UTF-8"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        a.put("gpslat", "null");
        a.put("gpslng", "null");
        a.setContentEncoding("UTF-8");
        a.setUseJsonStreamer(true);
        AsyncHttpClient client = new AsyncHttpClient();


        client.post(this.getActivity(), "http://14.63.219.140:8080/han5/webresources/han5.nowgps", null, a, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
                // TODO Auto-generated method stub
                Log.i("boogil", "makeNowgps onStart! ");

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("boogil", "makeNowgps success ");

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("boogil", "makeNowgps fail ");

            }
        });
    }
    public void makeGrouping(String str){
        RequestParams a = new RequestParams();
        a.put("id", id);
        a.put("gname", str);
        a.setUseJsonStreamer(true);

        Log.i("boogil", a.toString());

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(this.getActivity(), "http://14.63.219.140:8080/han5/webresources/han5.grouping", null, a, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
                // TODO Auto-generated method stub
                Log.i("boogil", "makeGrouping onStart! ");

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("boogil", "makeGrouping success ");

                startActivity(new Intent(getActivity(),com.example.administrator.myapplication2._5_Group._5_Group._5_GroupMain.class));

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("boogil", "makeGrouping fail ");

            }
        });
    }
}
