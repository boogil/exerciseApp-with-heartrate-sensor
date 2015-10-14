package com.example.administrator.myapplication2;

import android.app.Fragment;
import android.app.FragmentManager;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.administrator.myapplication2.login.Login;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class _7_SetupFragment extends Fragment implements View.OnClickListener{
    EditText etAge,etWeight;
    ImageButton btSave,btCancle,btLogout;

    RadioGroup rdSex,rdAlert;
    Boolean radio=false;
    Boolean alert=false;
    //  2131624088 = on /  2131624089=off
    String origin_age="";
    String origin_weight="";
    String id="";
    RadioButton on,off,alertOn,alertOff;
    public _7_SetupFragment(){}
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout._7_setup, container, false);

        etAge=(EditText)rootView.findViewById(R.id.etAge);
        etWeight=(EditText)rootView.findViewById(R.id.etWeight);
        btSave=(ImageButton)rootView.findViewById(R.id.btSave);
        btCancle=(ImageButton)rootView.findViewById(R.id.btCancle);
        btLogout=(ImageButton)rootView.findViewById(R.id.btLogout);
        rdSex=(RadioGroup)rootView.findViewById(R.id.rdSex);
        rdAlert=(RadioGroup)rootView.findViewById(R.id.rdAlert);

        on=(RadioButton)rootView.findViewById(R.id.on);
        off=(RadioButton)rootView.findViewById(R.id.off);
        alertOn=(RadioButton)rootView.findViewById(R.id.alertOn);
        alertOff=(RadioButton)rootView.findViewById(R.id.alertOff);

        SharedPreferences myPrefs=this.getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        if((myPrefs != null) && (myPrefs.contains("id"))){
            id=myPrefs.getString("id","");
            origin_age=myPrefs.getString("age","");
            origin_weight=myPrefs.getString("weight", "");
            etAge.setText(origin_age);
            etWeight.setText(origin_weight);
        }

        SharedPreferences myPrefs2=this.getActivity().getSharedPreferences("radio", Context.MODE_PRIVATE);
        if((myPrefs2 != null) && (myPrefs2.contains("radio"))){
            radio=myPrefs2.getBoolean("radio",false);
        }

        SharedPreferences myPrefs3=this.getActivity().getSharedPreferences("alert", Context.MODE_PRIVATE);
        if((myPrefs3 != null) && (myPrefs3.contains("alert"))){
            alert=myPrefs3.getBoolean("alert",false);
        }

        if(radio==true){
            rdSex.check(on.getId());
        }else{
            rdSex.check(off.getId());
        }

        if(alert==true){
            rdAlert.check(alertOn.getId());
        }else{
            rdAlert.check(alertOff.getId());
        }

        rdSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences myPrefs = getActivity().getSharedPreferences("radio", Context.MODE_PRIVATE);
                SharedPreferences.Editor myEditor = myPrefs.edit();

                Log.i("boogil", String.valueOf(checkedId));
                if(checkedId==on.getId()){  //on
                     /*radio 정보 등록*/
                    myEditor.putBoolean("radio", true);
                    myEditor.commit();
                                  /*끝*/
                }else if(checkedId==off.getId()){  //off
                    myEditor.putBoolean("radio", false);
                    myEditor.commit();
                }
            }
        });

        rdAlert.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences myPrefs = getActivity().getSharedPreferences("alert", Context.MODE_PRIVATE);
                SharedPreferences.Editor myEditor = myPrefs.edit();

                Log.i("boogil", String.valueOf(checkedId));
                if(checkedId==alertOn.getId()){  //on
                     /*radio 정보 등록*/
                    myEditor.putBoolean("alert", true);
                    myEditor.commit();
                                  /*끝*/
                }else if(checkedId==alertOff.getId()){  //off
                    myEditor.putBoolean("alert", false);
                    myEditor.commit();
                }
            }
        });

        btSave.setOnClickListener(this);
        btCancle.setOnClickListener(this);
        btLogout.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btSave:
                updateSetup();
                Toast.makeText(getActivity().getApplicationContext(), "정보가 변경되었습니다.", Toast.LENGTH_SHORT).show();

                break;

            case R.id.btCancle:
                etAge.setText(origin_age);
                etWeight.setText(origin_weight);
                break;

            case R.id.btLogout:
                /*로그인 정보 삭제*/
                SharedPreferences myPrefs=this.getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor myEditor=myPrefs.edit();
                myEditor.clear();
                myEditor.commit();
                /*끝*/
                //Set Class to Top of App and no history
                Intent launchNextActivity;
                launchNextActivity = new Intent(getActivity(),Login.class);
                launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(launchNextActivity);
                break;
        }
    }

    public void updateSetup(){
        String age= String.valueOf(etAge.getText());
        String weight= String.valueOf(etWeight.getText());
            try {
                AsyncHttpClient client = new AsyncHttpClient();
                //http://14.63.219.140:8080/han5/webresources/han5.member1/updateNowgps/{id}/{age}/{weight}



                client.get("http://14.63.219.140:8080/han5/webresources/han5.member1/updateNowgps/" + id + "/" +age + "/" +weight

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

        /*로그인 정보 변경*/
        SharedPreferences myPrefs = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = myPrefs.edit();
        myEditor.putString("age", age);
        myEditor.putString("weight", weight);
        myEditor.commit();


        Fragment fragment =new _7_SetupFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment).commit();
    }


                                  /*끝*/
}
