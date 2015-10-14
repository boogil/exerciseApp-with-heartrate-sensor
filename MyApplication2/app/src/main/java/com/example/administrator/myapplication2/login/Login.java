package com.example.administrator.myapplication2.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.administrator.myapplication2.Main;
import com.example.administrator.myapplication2.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class Login extends FragmentActivity implements View.OnClickListener{

    ImageButton bLogin;
    ImageButton bUserAdd;
    EditText etId,etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // super.onSaveInstanceState(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        etId=(EditText)findViewById(R.id.etId);
        etPassword=(EditText)findViewById(R.id.etPassword);
        bLogin=(ImageButton)findViewById(R.id.bLogin);
        bUserAdd=(ImageButton)findViewById(R.id.bUserAdd);

        bLogin.setOnClickListener(this);
        bUserAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bLogin:
                final String id=etId.getText().toString();
                final String pwd=etPassword.getText().toString();

                AsyncHttpClient client1 = new AsyncHttpClient();
                client1.get("http://14.63.219.140:8080/han5/webresources/han5.member1/" + id, new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        Log.i("boogil1", "receive json data start! ");
                        if (id.equals("")) {
                            Toast.makeText(Login.this, "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.i("boogil1", "success! ");
                        Log.i("boogil1", response.toString());


                        String id, rpwd, name, email,age,weight = "";
                        try {
                            id = response.getString("id");
                            rpwd = response.getString("pwd");
                            name = response.getString("name");
                            email = response.getString("email");
                            age = response.getString("age");
                            weight = response.getString("weight");

                            Log.i("boogil1", "id=" + id + ", pwd=" + rpwd);
                            if (rpwd.equals(pwd)) {
                                  /*로그인 정보 등록*/
                                SharedPreferences myPrefs = getSharedPreferences("login", MODE_PRIVATE);
                                SharedPreferences.Editor myEditor = myPrefs.edit();
                                myEditor.putString("id", id);
                                myEditor.putString("name", name);
                                myEditor.putString("email", email);
                                myEditor.putString("age", age);
                                myEditor.putString("weight", weight);
                                myEditor.commit();
                                  /*끝*/
                                Toast.makeText(Login.this, id + "님 환영합니다.", Toast.LENGTH_SHORT).show();
                                //Set Class to Top of App and no history
                                Intent launchNextActivity;
                                launchNextActivity = new Intent(Login.this,Main.class);
                                launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(launchNextActivity);
                            } else {
                                Toast.makeText(Login.this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(Login.this, "없는 ID입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }


                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.i("boogil1", "fail! ");
                    }


                });
                break;

            case R.id.bUserAdd:
                startActivity(new Intent(this,Register.class));
                break;
        }
    }
}
