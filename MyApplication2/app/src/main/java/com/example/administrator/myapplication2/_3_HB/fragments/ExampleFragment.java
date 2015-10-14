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

package com.example.administrator.myapplication2._3_HB.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplication2.R;


public class ExampleFragment extends Fragment implements View.OnClickListener {

	private Context mContext = null;
	private IFragmentListener mFragmentListener = null;
	private Handler mActivityHandler = null;
	
	TextView mTextChat;
	EditText mEditChat;
	Button mBtnSend;
    SeekBar seekbar;
    //심박수 프로그래스바
    ProgressBar progressBar;                                              //
    int progress=0;                                                       //
    MyHandler handler;
    Boolean threadBool=true;

    String ss="";

    int a=0;
    int first=0;
    public ExampleFragment(Context c, IFragmentListener l, Handler h) {
		mContext = c;
		mFragmentListener = l;
		mActivityHandler = h;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout._3_fragment_main_dummy, container, false);
		
		mTextChat = (TextView) rootView.findViewById(R.id.text_chat);
		mTextChat.setMaxLines(1000);
		mTextChat.setVerticalScrollBarEnabled(true);
		mTextChat.setMovementMethod(new ScrollingMovementMethod());
		
		mEditChat = (EditText) rootView.findViewById(R.id.edit_chat);
		mEditChat.setOnEditorActionListener(mWriteListener);
		
		mBtnSend = (Button) rootView.findViewById(R.id.button_send);
		mBtnSend.setOnClickListener(this);

        handler = new MyHandler();
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar2);
        seekbar = (SeekBar) rootView.findViewById(R.id.seekBar);

        new Thread() {

            @Override

            public void run() {
                try {
                    while (true){
                        sleep(1000);
                        handler.sendMessage(new Message());

                        if(threadBool==false){
                            break;
                        }

                    }
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }
        }.start();
		return rootView;
	}

    public void stopThread(){
        threadBool=false;
    }

    class MyHandler extends Handler {

        @Override

        public void handleMessage(Message msg) {

            //progressBar.incrementProgressBy(1);
            if(!ss.equals("")) {
                a = Math.round(Integer.parseInt(ss) / 2);
                if (a >= 100) {
                    a = 100;
                }
                if (a <= 0) {
                    a = 1;
                }


                if(a>=first){

                    ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, first, a);
                    anim.setDuration(500);
                    SeekBarAnimation animS= new SeekBarAnimation(seekbar,first,a);
                    animS.setDuration(500);
                    progressBar.startAnimation(anim);
                    seekbar.startAnimation(animS);

                }else{

                    ProgressBarAnimation2 anim = new ProgressBarAnimation2(progressBar, first, a);
                    anim.setDuration(500);
                    SeekBarAnimation2 animS= new SeekBarAnimation2(seekbar,first,a);
                    animS.setDuration(500);
                    progressBar.startAnimation(anim);
                    seekbar.startAnimation(animS);
                }

                first=a;

                Log.i("boogil", String.valueOf(a));
            }
            super.handleMessage(msg);

        }

    }

    public class ProgressBarAnimation extends Animation {
        private ProgressBar progressBar;

        private float from;
        private float  to;

        public ProgressBarAnimation(ProgressBar progressBar, float from, float to) {
            super();
            this.progressBar = progressBar;

            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = from + (to - from) * interpolatedTime;
            progressBar.setProgress((int) value);
        }
    }

    public class SeekBarAnimation extends Animation {

        private SeekBar seekbar;
        private float from;
        private float  to;

        public SeekBarAnimation(SeekBar seekbar, float from, float to) {
            super();

            this.seekbar=seekbar;
            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = from + (to - from) * interpolatedTime;
            seekbar.setProgress((int) value);
        }
    }
    public class ProgressBarAnimation2 extends Animation {
        private ProgressBar progressBar;

        private float from;
        private float  to;

        public ProgressBarAnimation2(ProgressBar progressBar, float from, float to) {
            super();
            this.progressBar = progressBar;

            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = from + (to - from) * interpolatedTime;
            progressBar.setProgress((int) value);
        }
    }

    public class SeekBarAnimation2 extends Animation {

        private SeekBar seekbar;
        private float from;
        private float  to;

        public SeekBarAnimation2(SeekBar seekbar, float from, float to) {
            super();

            this.seekbar=seekbar;
            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = from + (to - from) * interpolatedTime;
            seekbar.setProgress((int) value);
        }
    }
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button_send:
            //String message = mEditChat.getText().toString();
            Toast.makeText(getActivity(), "다시 연결을 시도합니다.", Toast.LENGTH_SHORT).show();
            String message = "start!";
            if(message != null && message.length() > 0)
            	sendMessage(message);
			break;
		}
	}
	
	
    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
        new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                if(message != null && message.length() > 0)
                	sendMessage(message);
            }
            return true;
        }
    };
	
    // Sends user message to remote
    private void sendMessage(String message) {
    	if(message == null || message.length() < 1)
    		return;
    	// send to remote
    	if(mFragmentListener != null)
    		mFragmentListener.OnFragmentCallback(IFragmentListener.CALLBACK_SEND_MESSAGE, 0, 0, message, null,null);
    	else
    		return;
    	// show on text view
    	if(mTextChat != null) {
            /*
    		mTextChat.append("\nSend: ");
    		mTextChat.append(message);
        	int scrollamout = mTextChat.getLayout().getLineTop(mTextChat.getLineCount()) - mTextChat.getHeight();
        	if (scrollamout > mTextChat.getHeight())
        		mTextChat.scrollTo(0, scrollamout);*/
    	}
    	mEditChat.setText("");
    }
    
    private static final int NEW_LINE_INTERVAL = 1000;
    private long mLastReceivedTime = 0L;
    
    // Show messages from remote
    public void showMessage(String message) {
    	if(message != null && message.length() > 0) {
    		long current = System.currentTimeMillis();

    		if(current - mLastReceivedTime > NEW_LINE_INTERVAL) {
                //mTextChat.append("receive:");
                mTextChat.setText("");
                Log.i("boogil", "ss:" +String.valueOf(ss));
                ss="";
            }
            //mTextChat.append(message);
            ss+=message;
            if(Integer.parseInt(ss)>=200){
                mTextChat.setText("200");
            }else {
                mTextChat.append(message);
            }

            /*
        	int scrollamout = mTextChat.getLayout().getLineTop(mTextChat.getLineCount()) - mTextChat.getHeight();
        	if (scrollamout > mTextChat.getHeight())
        		mTextChat.scrollTo(0, scrollamout);*/

        	mLastReceivedTime = current;


    	}


    }
    
}
