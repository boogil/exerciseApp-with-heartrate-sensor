package com.example.administrator.myapplication2;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class _6_WeatherFragment extends Fragment {

    public _6_WeatherFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout._6_activity_main, container, false);

        return rootView;


    }
    private String mInputUrl = "http://m.weather.naver.com/";

    private EditText mEditText;
    private WebView mWebView;
    private WebSettings mWebSettings;
    private ProgressBar mProgressBar;
    private InputMethodManager mInputMethodManager;

    @Override
    public void onStart() {
        super.onStart();


//        mEditText = (EditText) getView().findViewById(R.id.edit_Url);
        mWebView = (WebView)getView().findViewById(R.id.webview);
        mProgressBar = (ProgressBar)getView().findViewById(R.id.progressBar);
//        getView().findViewById(R.id.btn_go).setOnClickListener(onClickListener);
// 웹뷰에서 자바스크립트실행가능
        mWebView.getSettings().setJavaScriptEnabled(true);
        mInputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        mWebView.setWebChromeClient(new webViewChrome());
        mWebView.setWebViewClient(new webViewClient());
        mWebSettings = mWebView.getSettings();
        mWebSettings.setBuiltInZoomControls(true);

        mWebView.loadUrl(mInputUrl);
//        mEditText.setHint(mInputUrl);
    }

    //Button Event를 처리
//    View.OnClickListener onClickListener = new View.OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            switch(v.getId()) {
//                case R.id.btn_go:
//                    //InputMethodManager를 이용하여 키보드를 숨김
//                    mInputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
//                    mInputUrl = httpInputCheck(mEditText.getText().toString());
//
//                    if(mInputUrl == null) break;
//
//                    //페이지를 불러온다
//                    mWebView.loadUrl(mInputUrl);
//                    mEditText.setText("");
//                    mEditText.setHint(mInputUrl);
//                    break;
//            }
//        }
//    };

    class webViewChrome extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //현제 페이지 진행사항을 ProgressBar를 통해 알린다.
            if(newProgress < 100) {
                mProgressBar.setProgress(newProgress);
            } else {
                mProgressBar.setVisibility(View.INVISIBLE);
                mProgressBar.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            }
        }
    }

    class webViewClient extends WebViewClient {

        //Loading이 시작되면 ProgressBar처리를 한다.
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 15));
            view.loadUrl(url);
            return true;
        }

//        @Override
//        public void onPageFinished(WebView view, String url) {
//            mWebSettings.setJavaScriptEnabled(true);
//            mEditText.setHint(url);
//            super.onPageFinished(view, url);
//        }
    }

    //http://를 체크하여 추가한다.
//    private String httpInputCheck(String url) {
//        if(url.isEmpty()) return null;
//
//        if(url.indexOf("http://") == ("http://").length()) return url;
//        else return "http://" + url;
//    }

}