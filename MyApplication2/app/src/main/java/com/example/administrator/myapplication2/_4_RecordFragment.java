package com.example.administrator.myapplication2;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplication2._4_Detail.graphActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class _4_RecordFragment extends ListFragment {

    private SimpleAdapter mAdapter;
    private SimpleAdapter mAdapter2;

    private ArrayList<HashMap<String,String>> listItem;
    private HashMap<String,String> map;

    String id="";
    private Menu mMenu;

    ArrayList<String> seq;

    public _4_RecordFragment(){}
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        mMenu=menu;
        inflater.inflate(R.menu._4_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.graph_btn:
                Intent a = new Intent(getActivity(),graphActivity.class);
                a.putExtra("listItem",listItem);
                startActivity(a);
                break;

            case R.id.delete_btn :
                viewing2();

                MenuInflater inflater = getActivity().getMenuInflater();
                mMenu.clear();
                inflater.inflate(R.menu._4_main_delete, mMenu);
                break;

            case R.id.ok_btn:
                new AlertDialog.Builder(this.getActivity())
                        .setTitle("운동정보 삭제")
                        .setMessage("정말 삭제하시겠습니까?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                deleteRecord();
                                Fragment fragment =new _4_RecordFragment();
                                FragmentManager fragmentManager = getFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.frame_container, fragment).commit();
                            }
                        }).create().show();
                break;
            case R.id.cancel_btn:
                viewing();
                MenuInflater inflater2 = getActivity().getMenuInflater();
                mMenu.clear();
                inflater2.inflate(R.menu._4_main, mMenu);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // if this is set true,
        // Activity.onCreateOptionsMenu will call Fragment.onCreateOptionsMenu
        // Activity.onOptionsItemSelected will call Fragment.onOptionsItemSelected
        setHasOptionsMenu(true);


        seq=new ArrayList<String>();

        SharedPreferences myPrefs=this.getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        if((myPrefs != null) && (myPrefs.contains("id"))){
            id=myPrefs.getString("id","");
        }



        listItem = new ArrayList<HashMap<String, String>>();

        getRecentData();



        Log.i("boogilll",listItem.toString());

    }


    public void deleteRecord(){
        AsyncHttpClient client1 = new AsyncHttpClient();

        if(seq.size()>0) {
            for (int i = 0; i < seq.size(); i++) {
                client1.get("http://14.63.219.140:8080/han5/webresources/han5.map/deleteMap/" + seq.get(i), new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        Log.i("boogil1", "receive json data start! ");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.i("boogil1", "success! ");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.i("boogil1", "getRecentData(left) fail! ");
                    }
                });

                client1.get("http://14.63.219.140:8080/han5/webresources/han5.log/deletelog/" + seq.get(i), new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        Log.i("boogil1", "receive json data start! ");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.i("boogil1", "success! ");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.i("boogil1", "getRecentData(left) fail! ");
                    }
                });
            }
        }
    }
    public void noviewing(){

        //Création d'un SimpleAdapter qui se chargera de mettre les items présent dans notre list (listItem) dans la vue fragment_add_objet
        mAdapter = null;

        Toast.makeText(getActivity(),"운동기록이 없습니다.",Toast.LENGTH_SHORT).show();
        setListAdapter(mAdapter);
    }


    public void viewing(){

        //Création d'un SimpleAdapter qui se chargera de mettre les items présent dans notre list (listItem) dans la vue fragment_add_objet
        mAdapter = new SimpleAdapter(getActivity(), listItem, R.layout._4_main_design,
                new String[]{"ldate", "ldistance","kal","seq"}, new int[]{R.id.ldate_entry, R.id.ldistance_entry,R.id.kal_entry,R.id.seq_entry});

        setListAdapter(mAdapter);
    }

    public void viewing2(){

        //Création d'un SimpleAdapter qui se chargera de mettre les items présent dans notre list (listItem) dans la vue fragment_add_objet
        mAdapter2 = new SimpleAdapter(getActivity(), listItem, R.layout._4_main_design2,
                new String[]{"ldate", "ldistance","kal","seq"}, new int[]{R.id.ldate_entry, R.id.ldistance_entry,R.id.kal_entry,R.id.seq_entry}){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);

                final CheckBox b=(CheckBox)v.findViewById(R.id.checkbox);
                final TextView tvseq=(TextView)v.findViewById(R.id.seq_entry);
                b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        seq.add(tvseq.getText().toString());
                    }
                });

                return v;
            }
        };

        setListAdapter(mAdapter2);
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO implement some logic

        HashMap<String,String> item = (HashMap<String, String>) mAdapter.getItem(position);  //item을 cursor타입으로 변형

        String seq = item.get("seq");

        Intent intent = new Intent(getActivity(),com.example.administrator.myapplication2._4_Detail._4_DetailMain.class);
        intent.putExtra("seq",seq);
        startActivity(intent);
    }

    public void getRecentData(){

        AsyncHttpClient client1 = new AsyncHttpClient();
        client1.get("http://14.63.219.140:8080/han5/webresources/han5.log/getloglist/"+id, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.i("_4_", "receive json data start! ");}

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.i("_4_", "getRecentData(right) success! ");
                Log.i("_4_", response.toString());

                try {
                    JSONArray jArray = response;

                    for(int i=0; i<jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);

                        map = new HashMap<String, String>();
                        map.put("ldate", json_data.getString("ldate"));
                        map.put("ldistance", json_data.getString("ldistance"));
                        map.put("kal", json_data.getString("kal"));
                        map.put("seq", json_data.getString("seq"));

                        Log.i("boogilll",json_data.getString("id"));
                        Log.i("boogilll","map데이터:"+map.toString());

                        listItem.add(map);
                        Log.i("boogilll","map데이터:"+listItem.toString());
                    }

                    listLintUp(listItem); //seq순서대로 정렬하기

                    viewing();
                } catch (JSONException e) {}
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i("_4_", "3");
                noviewing();
            }


        });
    }

    public void listLintUp(ArrayList<HashMap<String,String>> listItem){
        for(int i=0;i<listItem.size()-1;i++){
            for(int j=i+1;j<listItem.size();j++){
                int a= Integer.parseInt(listItem.get(i).get("seq"));
                int b= Integer.parseInt(listItem.get(j).get("seq"));
                HashMap tmp=new HashMap<String,String>();
                if(a<b){
                    tmp=listItem.get(i);
                    listItem.set(i,listItem.get(j));
                    listItem.set(j,tmp);
                }
            }
        }
    }
}
