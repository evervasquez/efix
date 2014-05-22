package com.emergentes.efix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity{
	private FragmentTabHost mTabHost;
	private static final int 	RESULT_SETTING	= 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.addTab(mTabHost.newTabSpec("tag1").setIndicator(("Imagen"),
                getResources().getDrawable(R.drawable.camara)),
                ImagenFragment.class, null);
        
        
        /*mTabHost.addTab(mTabHost.newTabSpec("tag2").setIndicator(("Información"),
                getResources().getDrawable(android.R.drawable.ic_popup_sync)),
                InformacionFragment.class, null);*/
        
       mTabHost.addTab(mTabHost.newTabSpec("tag3").setIndicator(("Ubicación"),
                getResources().getDrawable(R.drawable.mapa)),
                MapFragment.class, null);
       
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//acomodando codigo
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch(item.getItemId()){
				case R.id.action_settings:
					Intent intent = new Intent(MainActivity.this,
							ConfigActivity.class);
					startActivityForResult(intent, RESULT_SETTING);
					break;

				default:
					break;
			}

			return super.onOptionsItemSelected(item);
		}

		@Override
		// metodo para recuperar la url de preferences
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			switch(requestCode){
				case RESULT_SETTING:
					showSettings();
					break;

				default:
					break;
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
		
		private void showSettings() {
			SharedPreferences mSharePreferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			String url = mSharePreferences.getString("prefUrl", "NULL");

			mSharePreferences = getSharedPreferences("EmergentesPreferences",
					MODE_PRIVATE);

			// guardamos las preferencias en un xml
			SharedPreferences.Editor editor = mSharePreferences.edit();
			editor.putString("url", url);
			editor.commit();
		}
		
		
}
