package com.emergentes.efix;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

public class ImagenFragment extends Fragment implements LocationListener{
	private static int TAKE_PICTURE = 1;
	private static int SELECT_PICTURE = 2;
	private String name = "";
	private View root;
	private RadioButton radio1;
	private RadioButton radio2;
	private Button btnAction;
	private LocationManager locationManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		this.root = inflater
				.inflate(R.layout.imagen_fragment, container, false);

		radio1 = (RadioButton) root.findViewById(R.id.radbtnFull);
		radio2 = (RadioButton) root.findViewById(R.id.radbtnGall);

		name = Environment.getExternalStorageDirectory() + "/test.jpg";
		btnAction = (Button) root.findViewById(R.id.btnPic);

		btnAction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RadioButton rbtnFull = (RadioButton) root
						.findViewById(R.id.radbtnFull);
				RadioButton rbtnGallery = (RadioButton) root
						.findViewById(R.id.radbtnGall);

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				int code = TAKE_PICTURE;
				if (rbtnFull.isChecked()) {
					Uri output = Uri.fromFile(new File(name));
					intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
				} else if (rbtnGallery.isChecked()) {
					intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
					code = SELECT_PICTURE;
				}
				startActivityForResult(intent, code);
			}
		});
		
		radio1.setChecked(true);
		radio1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				btnAction.setText("Escoger Imagen");
			}
		});

		radio2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				btnAction.setText("Tomar Foto");
			}
		});

		return root;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TAKE_PICTURE) {
			if (data != null) {

				if (data.hasExtra("data")) {
					ImageView iv = (ImageView) root.findViewById(R.id.imgView);
					iv.setImageBitmap((Bitmap) data.getParcelableExtra("data"));
				}
			} else {
				ImageView iv = (ImageView) root.findViewById(R.id.imgView);
				iv.setImageBitmap(BitmapFactory.decodeFile(name));
				new MediaScannerConnectionClient() {
					private MediaScannerConnection msc = null;
					{
						msc = new MediaScannerConnection(getActivity()
								.getApplicationContext(), this);
						msc.connect();
					}

					public void onMediaScannerConnected() {
						msc.scanFile(name, null);
					}

					public void onScanCompleted(String path, Uri uri) {
						msc.disconnect();
					}
				};
			}
		} else if (requestCode == SELECT_PICTURE) {
			Uri selectedImage = data.getData();
			InputStream is;
			try {
				is = getActivity().getContentResolver().openInputStream(
						selectedImage);
				BufferedInputStream bis = new BufferedInputStream(is);
				Bitmap bitmap = BitmapFactory.decodeStream(bis);
				ImageView iv = (ImageView) root.findViewById(R.id.imgView);
				iv.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
			}
		}
	}
	
	//location gps
	@Override
	public void onLocationChanged(Location location) {
		Toast.makeText(getActivity().getApplicationContext(), 
				"latitud: "+location.getLatitude()+" - Longitud:"+location.getLongitude(),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(getActivity().getApplicationContext(),"Disabled",
				Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(getActivity().getApplicationContext(),"Enabled",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Toast.makeText(getActivity().getApplicationContext(),"status",
				Toast.LENGTH_SHORT).show();
	}

}
