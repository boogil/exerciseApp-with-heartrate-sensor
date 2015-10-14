package com.example.administrator.myapplication2.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.administrator.myapplication2.Main;
import com.example.administrator.myapplication2.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.net.URLEncoder;


public class Register extends FragmentActivity implements View.OnClickListener{

    EditText etId,etPassword,etName,
            etAge,etEmail,etWeight;
    RadioGroup rdSex;
    ImageButton bRegister;

    private RadioButton radioTmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etId=(EditText)findViewById(R.id.etId);
        etPassword=(EditText)findViewById(R.id.etPassword);
        etName=(EditText)findViewById(R.id.etName);
        rdSex=(RadioGroup)findViewById(R.id.rdSex);
        etAge=(EditText)findViewById(R.id.etAge);
        etEmail=(EditText)findViewById(R.id.etEmail);
        etWeight=(EditText)findViewById(R.id.etWeight);

        bRegister=(ImageButton)findViewById(R.id.bRegister);
        bRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bRegister:
                String id=etId.getText().toString();
                String pwd=etPassword.getText().toString();
                String name=etName.getText().toString();
               /* 라디오버튼 텍스트 가져오는 과정*/
                int selectedId=rdSex.getCheckedRadioButtonId();
                radioTmp=(RadioButton)findViewById(selectedId);
                String sex=radioTmp.getText().toString();
                /*끝*/
                String age=etAge.getText().toString();
                String email=etEmail.getText().toString();
                String weight=etWeight.getText().toString();


                try {

                    RequestParams a = new RequestParams();
                    a.put("id",id);
                    a.put("pwd",pwd);
                    //a.put("name",name);
                    a.put("name", URLEncoder.encode(name.toString(), "UTF-8"));
                    a.put("gen",sex);
                    a.put("age",age);
                    a.put("email",email);
                    a.put("weight", weight);
                    a.setContentEncoding(HTTP.UTF_8);
                    a.setUseJsonStreamer(true);

                    Log.i("boogil", a.toString());

                    HttpPostDateTest(a);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    public void HttpPostDateTest(RequestParams rp){
        AsyncHttpClient client = new AsyncHttpClient();


        client.post(this.getApplicationContext(), "http://14.63.219.140:8080/han5/webresources/han5.member1", null, rp, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
                // TODO Auto-generated method stub
                Log.i("boogil", "onStart! ");

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("boogil", "success ");
                Toast.makeText(Register.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Register.this, Main.class));
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("boogil", "fail ");
                Toast.makeText(Register.this, "동일한 ID가 존재합니다.", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
