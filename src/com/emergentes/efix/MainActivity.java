package com.emergentes.efix;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends ActionBarActivity {
	private FragmentTabHost mTabHost;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.addTab(mTabHost.newTabSpec("tag1").setIndicator(("Imagen"),
                getResources().getDrawable(R.drawable.camera)),
                ImagenFragment.class, null);
        
        mTabHost.addTab(mTabHost.newTabSpec("tag2").setIndicator(("Información"),
                getResources().getDrawable(android.R.drawable.ic_popup_sync)),
                InformacionFragment.class, null);
        
        mTabHost.addTab(mTabHost.newTabSpec("tag3").setIndicator(("Ubicación"),
                getResources().getDrawable(android.R.drawable.ic_popup_sync)),
                MapFragment.class, null);

	}
}
