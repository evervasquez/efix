package com.emergentes.efix;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ConfigDialogFragment extends DialogFragment implements
		OnItemSelectedListener {


	public static ConfigDialogFragment newInstance() {
		ConfigDialogFragment frag = new ConfigDialogFragment();
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());

		View view = getActivity().getLayoutInflater().inflate(
				R.layout.config_fragment, null);
		
		
		alertDialogBuilder.setView(view);
		alertDialogBuilder.setTitle(getString(R.string.dialog_title))
		.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                	
                	Intent settingsIntent = new Intent(
           				 android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS); 
           		 settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED); 
           		 getActivity().startActivityForResult(settingsIntent, 0);
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    getActivity().finish();
                }
            });
		
		return alertDialogBuilder.create();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// outState.putFloat(KEY_SAVE_RATING_BAR_VALUE, mRatingBar.getRating());
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}


}