package com.example.administrator.myapplication2;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class _3_HeartrateFragment extends Fragment {

    public _3_HeartrateFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       /* View rootView = inflater.inflate(R.layout._3_heartrateMain, container, false);

        return rootView;*/

        startActivity(new Intent(getActivity(),com.example.administrator.myapplication2._3_HB._3_MainActivity.class));

        return null;
    }
}
