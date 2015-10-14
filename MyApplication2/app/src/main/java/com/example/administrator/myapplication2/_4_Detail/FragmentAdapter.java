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

package com.example.administrator.myapplication2._4_Detail;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;


/**
 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class FragmentAdapter extends FragmentPagerAdapter {
	
	public static final String TAG = "FragmentAdapter";
	
	// TODO: Total count
	public static final int FRAGMENT_COUNT = 2;
	
    // TODO: Fragment position
    public static final int FRAGMENT_POS_EXAMPLE = 0;
    public static final int FRAGMENT_POS_SETTINGS = 1;
    
    // System
    private Context mContext = null;
    private Handler mHandler = null;
    private IFragmentListener mFragmentListener = null;

    private Fragment mExampleFragment = null;
    private Fragment mLLSettingsFragment = null;

    public FragmentAdapter(FragmentManager fm, Context c, IFragmentListener l, Handler h) {
		super(fm);
		mContext = c;
		mFragmentListener = l;
		mHandler = h;
	}
    
	@Override
	public Fragment getItem(int position) {
		// getItem is called to instantiate the fragment for the given page.
		Fragment fragment;
		//boolean needToSetArguments = false;
		
		if(position == FRAGMENT_POS_EXAMPLE) {
			if(mExampleFragment == null) {
				mExampleFragment = new LeftFragment(mContext, mFragmentListener, mHandler);
				//needToSetArguments = true;
			}
			fragment = mExampleFragment;
			
		} else if(position == FRAGMENT_POS_SETTINGS) {
			if(mLLSettingsFragment == null) {
				mLLSettingsFragment = new RightFragment(mContext, mFragmentListener);
				//needToSetArguments = true;
			}
			fragment = mLLSettingsFragment;
			
		} else {
			fragment = null;
		}
		
		// TODO: If you have something to notify to the fragment.
		/*
		if(needToSetArguments) {
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
		}
		*/
		
		return fragment;
	}

	@Override
	public int getCount() {
		return FRAGMENT_COUNT;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case FRAGMENT_POS_EXAMPLE:
			return "결과".toUpperCase(l);
		case FRAGMENT_POS_SETTINGS:
			return "지도보기".toUpperCase(l);
		}
		return null;
	}
    
    
}
