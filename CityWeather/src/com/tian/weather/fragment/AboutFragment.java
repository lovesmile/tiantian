package com.tian.weather.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tian.weather.R;

/**
 */
public class AboutFragment extends Fragment {
	public AboutFragment() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AboutFragment(Context context) {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.about, container, false);
		
		return view;
	}

	

}
