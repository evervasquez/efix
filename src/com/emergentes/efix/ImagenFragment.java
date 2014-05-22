package com.emergentes.efix;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import com.emergentes.efix.utils.Connection;

public class ImagenFragment extends Fragment implements LocationListener {
	private static int TAKE_PICTURE = 1;
	private static int SELECT_PICTURE = 2;
	public static File file;
	public static String nameImagen = null;
	private String name = "";
	private View root;
	private RadioButton radio1;
	/*private RadioButton radio2;*/
	private Button btnAction;
	private LocationManager locationManager;
	public double latitud;
	public double longitud;
	public ProgressDialog pd;
	public String nombreimagen;
	public String archivophp = "getLocalizacion.php";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, this);
		this.root = inflater
				.inflate(R.layout.imagen_fragment, container, false);

		radio1 = (RadioButton) root.findViewById(R.id.radbtnFull);
/*		radio2 = (RadioButton) root.findViewById(R.id.radbtnGall);*/


		btnAction = (Button) root.findViewById(R.id.btnPic);

		btnAction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				nombreimagen = getNombreImagen();
				name = Environment.getExternalStorageDirectory() + nombreimagen;
				ImagenFragment.nameImagen = nombreimagen;
				RadioButton rbtnFull = (RadioButton) root
						.findViewById(R.id.radbtnFull);
				/*RadioButton rbtnGallery = (RadioButton) root
						.findViewById(R.id.radbtnGall);*/

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				int code = TAKE_PICTURE;
				if (rbtnFull.isChecked()) {
					Uri output = Uri.fromFile(new File(name));
					intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
				} /*else if (rbtnGallery.isChecked()) {
					intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
					code = SELECT_PICTURE;
				}*/
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

		/*radio2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				btnAction.setText("Tomar Foto");
			}
		});*/

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

				ImagenFragment.file = new File(name);

				ImagenFragment.nameImagen = getNombreImagen();

				if (ImagenFragment.file.exists()) {
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

					new datosAsyncTask().execute();
				} else {
					Toast.makeText(getActivity().getApplicationContext(),
							"Tiene que capturar una imagen", Toast.LENGTH_SHORT)
							.show();
				}
			}
		} else if (requestCode == SELECT_PICTURE) {

			if (data!= null) {
				Uri selectedImage = data.getData();
				InputStream is;
				try {
					is = getActivity().getContentResolver().openInputStream(
							selectedImage);
					BufferedInputStream bis = new BufferedInputStream(is);
					Bitmap bitmap = BitmapFactory.decodeStream(bis);
					ImageView iv = (ImageView) root.findViewById(R.id.imgView);
					iv.setImageBitmap(bitmap);
					
					new datosAsyncTask().execute();
				} catch (FileNotFoundException e) {
				}
			}else{
				Toast.makeText(getActivity().getApplicationContext(),
						"Tiene que escoger una imagen", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	private class datosAsyncTask extends AsyncTask<String, Void, Void> {
		private String datos;
		private Connection emergentesUtil;
		private String content;

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(getActivity(), "",
					"Recuperando Ambientes...", true);
			try {
				String param = URLEncoder.encode("latitud", "UTF-8") + "="
						+ URLEncoder.encode(latitud + "", "UTF-8");
				param += "&" + URLEncoder.encode("logitud", "UTF-8") + "="
						+ URLEncoder.encode(longitud + "", "UTF-8");
				datos = param;
				Log.v("URL", datos);
			} catch (java.io.UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		protected Void doInBackground(String... arg0) {
			emergentesUtil = new Connection(getActivity()
					.getApplicationContext(), getUrl() + archivophp, datos);
			this.content = emergentesUtil.getResponse();
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			String OutputData = "";
			JSONObject jsonResponse = null;
			ArrayList<String> datosUsusario;

			pd.dismiss();
			try {

				jsonResponse = new JSONObject(this.content);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("ambientes");

				showDialog(jsonMainNode);
				// Toast.makeText(getActivity().getApplicationContext(),
				// jsonMainNode+"", Toast.LENGTH_SHORT).show();

			} catch (JSONException e) {
				Toast.makeText(getActivity().getApplicationContext(),
						"Hubo un error al recuperar los datos",
						Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
			// Log.v("ImagenFragment", content);

			// Toast.makeText(getActivity().getApplicationContext(), content,
			// Toast.LENGTH_SHORT).show();

		}

	}

	// location gps
	@Override
	public void onLocationChanged(Location location) {
		this.latitud = location.getLatitude();
		this.longitud = location.getLongitude();
	}

	@Override
	public void onProviderDisabled(String provider) {
		ConfigDialogFragment newFragment = ConfigDialogFragment
				.newInstance();
		newFragment.show(getFragmentManager(), "dialog");
		
		 
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(getActivity().getApplicationContext(), "Enabled",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Toast.makeText(getActivity().getApplicationContext(), "status",
				Toast.LENGTH_SHORT).show();
	}

	public String getUrl() {
		SharedPreferences pref = getActivity().getSharedPreferences(
				"EmergentesPreferences", getActivity().MODE_PRIVATE);
		return pref.getString("url", "nada");
	}

	private void showDialog(JSONArray jsonMainNode) {
		DialogFragment newFragment = AmbientesDialogFragment
				.newInstance(jsonMainNode,nombreimagen);
		newFragment.show(getFragmentManager(), "dialog");
	}

	public String getNombreImagen() {
		Calendar ahoraCal = Calendar.getInstance();
		String imagenNombre = "/" + ahoraCal.get(Calendar.YEAR) + ""
				+ ahoraCal.get(Calendar.MONTH) + ""
				+ ahoraCal.get(Calendar.DATE) + ""
				+ ahoraCal.get(Calendar.HOUR) + ""
				+ ahoraCal.get(Calendar.MINUTE) + ""
				+ ahoraCal.get(Calendar.MILLISECOND) + ".png";

		return imagenNombre;
	}
}
