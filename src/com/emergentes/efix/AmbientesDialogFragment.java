package com.emergentes.efix;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.emergentes.efix.model.Ambientes;
import com.emergentes.efix.model.Aulas;
import com.emergentes.efix.model.Incidencias;
import com.emergentes.efix.utils.Connection;
import com.emergentes.efix.utils.SubirImagen;

public class AmbientesDialogFragment extends DialogFragment implements
		OnItemSelectedListener {

	private static final String KEY_SAVE_RATING_BAR_VALUE = "KEY_SAVE_RATING_BAR_VALUE";
	private Spinner sp_averias, sp_ambientes, sp_aulas, sp_incidencia;
	private static JSONArray json_content;
	public ArrayList<Ambientes> datosambiente;
	public ArrayList<Aulas> datosaulas;
	public ArrayList<Incidencias> datosIncidencia;
	private Button btn_enviar;
	private EditText txt_descripcion;
	AdapterView<?> adapview = null;
	public ProgressDialog pd;
	public String id_aula,id_incidencia;
	public String id_averia;
	public static String nombreImagen;
	private String archphp = "upload.php";
	private int count = 0;

	public static AmbientesDialogFragment newInstance(JSONArray jsonMainNode,
			String imagen) {
		AmbientesDialogFragment frag = new AmbientesDialogFragment();
		AmbientesDialogFragment.json_content = jsonMainNode;
		AmbientesDialogFragment.nombreImagen = imagen;
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());

		View view = getActivity().getLayoutInflater().inflate(
				R.layout.ambientes_fragment, null);

		sp_averias = (Spinner) view.findViewById(R.id.sp_averia);
		sp_ambientes = (Spinner) view.findViewById(R.id.sp_ambiente);
		sp_incidencia = (Spinner) view.findViewById(R.id.sp_incidencia);
		txt_descripcion = (EditText) view.findViewById(R.id.txt_info);

		sp_aulas = (Spinner) view.findViewById(R.id.sp_aula);

		btn_enviar = (Button) view.findViewById(R.id.btn_enviar);

		ArrayAdapter sp_adpter = ArrayAdapter.createFromResource(getActivity()
				.getApplicationContext(), R.array.sp_averias,
				R.layout.spinner_item);
		sp_adpter.setDropDownViewResource(R.layout.spiner_lista);
		sp_averias.setAdapter(sp_adpter);

		alertDialogBuilder.setView(view);
		alertDialogBuilder.setTitle(getString(R.string.dialog_title));
		String[] datosaa = Json_Array(AmbientesDialogFragment.json_content);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity()
				.getApplicationContext(), R.layout.spinner_item, datosaa);
		adapter.setDropDownViewResource(R.layout.spiner_lista);
		sp_ambientes.setAdapter(adapter);

		sp_ambientes.setOnItemSelectedListener(this);

		// adapter de sp_incidencia
		ArrayAdapter adapter_incidencia = ArrayAdapter.createFromResource(
				getActivity().getApplicationContext(), R.array.sp_incidencias,
				R.layout.spinner_item);

		adapter_incidencia.setDropDownViewResource(R.layout.spiner_lista);
		sp_incidencia.setAdapter(adapter_incidencia);

		sp_aulas.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				adapview = arg0;
				id_aula = datosaulas.get(arg2).getId();
				sp_averias.setOnItemSelectedListener(new CustomOnItemSelectedListener());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		
		/*sp_averias.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				id_averia = arg2 + 1 + "";
				new IncidenciasAsyncTask(id_averia, sp_incidencia).execute();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});*/

		btn_enviar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.v("datosAenviar",
						"id_aula=" + id_aula + "- id_averia = " + id_averia
								+ " - descripcion" + txt_descripcion.getText());
				String info = txt_descripcion.getText().toString();

				if (ImagenFragment.file.exists()) {

					SubirImagen nuevaTarea = new SubirImagen(
							getUrl() + archphp, ImagenFragment.file,
							getActivity());
					nuevaTarea.execute();

					new infoAsyncTask(id_aula, id_averia, info,
							AmbientesDialogFragment.nombreImagen).execute();
				} else {
					Toast.makeText(getActivity().getApplicationContext(),
							"No se ha realizado la foto", Toast.LENGTH_SHORT)
							.show();

				}
			}
		});

		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(KEY_SAVE_RATING_BAR_VALUE)) {
				// mRatingBar.setRating(savedInstanceState.getFloat(KEY_SAVE_RATING_BAR_VALUE));
			}
		}

		return alertDialogBuilder.create();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// outState.putFloat(KEY_SAVE_RATING_BAR_VALUE, mRatingBar.getRating());
		super.onSaveInstanceState(outState);
	}

	// parse de la data
	public String[] Json_Array(JSONArray jsonMainNode) {

		int lengthJsonArr = jsonMainNode.length();
		String[] ambi = new String[lengthJsonArr];

		if (lengthJsonArr > 0) {

			datosambiente = new ArrayList<Ambientes>();

			for (int i = 0; i < lengthJsonArr; i++) {
				JSONObject jsonChildNode;

				try {
					jsonChildNode = jsonMainNode.getJSONObject(i);
					datosambiente.add(new Ambientes(jsonChildNode.optString(
							"idambientes").toString(), jsonChildNode.optString(
							"piso").toString()));
					ambi[i] = jsonChildNode.optString("piso").toString();

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			return ambi;
		} else {
			Toast.makeText(getActivity().getApplicationContext(),
					"hubo un error al descargar los datos", Toast.LENGTH_SHORT)
					.show();
		}
		return ambi;
	}

	public String getUrl() {
		SharedPreferences pref = getActivity().getSharedPreferences(
				"EmergentesPreferences", getActivity().MODE_PRIVATE);
		return pref.getString("url", "nada");
	}

	// peticion aulas
	private class datosAsyncTask extends AsyncTask<String, Void, Void> {
		private String datos;
		private Connection emergentesUtil;
		private String content;
		private String archivophp = "getAulas.php";
		private String id;
		private Spinner s_aula;
		private String[] aulas;
		ArrayAdapter<String> adapter_aulas = null;

		public datosAsyncTask(String id, Spinner aula) {
			this.id = id;
			this.s_aula = aula;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(getActivity(), "", "Obteniendo Aulas...",
					true);
			try {
				String param = URLEncoder.encode("ambiente", "UTF-8") + "="
						+ URLEncoder.encode(this.id, "UTF-8");
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
				JSONArray jsonMainNode = jsonResponse.optJSONArray("aulas");

				// Toast.makeText(getActivity().getApplicationContext(),
				// jsonMainNode+"", Toast.LENGTH_SHORT).show();

				int lengthJsonArr = jsonMainNode.length();
				aulas = new String[lengthJsonArr];

				if (lengthJsonArr > 0) {

					datosaulas = new ArrayList<Aulas>();

					for (int i = 0; i < lengthJsonArr; i++) {
						JSONObject jsonChildNode;

						try {
							jsonChildNode = jsonMainNode.getJSONObject(i);
							datosaulas.add(new Aulas(jsonChildNode.optString(
									"idambientes").toString(), jsonChildNode
									.optString("aulas").toString()));
							aulas[i] = jsonChildNode.optString("aulas")
									.toString();

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					adapter_aulas = new ArrayAdapter<String>(getActivity()
							.getApplicationContext(), R.layout.spinner_item,
							aulas);
					adapter_aulas
							.setDropDownViewResource(R.layout.spiner_lista);
					s_aula.setAdapter(adapter_aulas);

				} else {
					Toast.makeText(getActivity().getApplicationContext(),
							"hubo un error al descargar los datos",
							Toast.LENGTH_SHORT).show();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private class infoAsyncTask extends AsyncTask<String, Void, Void> {
		private String datos;
		private Connection emergentesUtil;
		private String content;
		private String id_aula, id_averia, descripcion, nameimagen;
		private String archiphpq = "setAmbiente.php";

		public infoAsyncTask(String id_aula, String id_averia,
				String txt_descripcion, String nameImagen) {
			this.id_aula = id_aula;
			this.id_averia = id_averia;
			this.descripcion = txt_descripcion;
			this.nameimagen = nameImagen;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(getActivity(), "",
					"Enviando datos al servidor...", true);
			try {
				String param = URLEncoder.encode("id_aula", "UTF-8") + "="
						+ URLEncoder.encode(id_aula, "UTF-8");
				param += "&" + URLEncoder.encode("id_averia", "UTF-8") + "="
						+ URLEncoder.encode(id_averia, "UTF-8");
				param += "&" + URLEncoder.encode("id_incidencia", "UTF-8") + "="
						+ URLEncoder.encode(id_incidencia, "UTF-8");
				param += "&" + URLEncoder.encode("descripcion", "UTF-8") + "="
						+ URLEncoder.encode(descripcion, "UTF-8");
				param += "&" + URLEncoder.encode("nameimagen", "UTF-8") + "="
						+ URLEncoder.encode(nameimagen, "UTF-8");
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
					.getApplicationContext(), getUrl() + archiphpq, datos);
			this.content = emergentesUtil.getResponse();
			Log.v("URL", getUrl() + archiphpq);
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			JSONObject jsonResponse = null;

			pd.dismiss();
			try {

				jsonResponse = new JSONObject(this.content);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("respuesta");

				try {
					int lengthJsonArr = jsonMainNode.length();

					if (lengthJsonArr > 0) {

						String respuesta = null;
						for (int i = 0; i < lengthJsonArr; i++) {
							JSONObject jsonChildNode;

							jsonChildNode = jsonMainNode.getJSONObject(i);
							respuesta = jsonChildNode.optString("id")
									.toString();

						}
						Toast.makeText(getActivity().getApplicationContext(),
								"datos ingresados correctamente",
								Toast.LENGTH_SHORT).show();
						getDialog().dismiss();

					} else {
						Toast.makeText(getActivity().getApplicationContext(),
								"hubo un error al descargar los datos",
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

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

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {

		String id = datosambiente.get(arg2).getId();

		if (adapview != null) {
			adapview.setAdapter(null);
		}

		new datosAsyncTask(id, sp_aulas).execute();

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	// getIncidencias
	private class IncidenciasAsyncTask extends AsyncTask<String, Void, Void> {
		private String datos;
		private Connection emergentesUtil;
		private String content;
		private String archivophp = "getIncidencia.php";
		private String id;
		private Spinner s_incidencia;
		private String[] indicencias;
		ArrayAdapter<String> adapter_incidencias = null;

		public IncidenciasAsyncTask(String id, Spinner incidencia) {
			this.id = id;
			this.s_incidencia = incidencia;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(getActivity(), "",
					"Obteniendo Incidencias...", true);
			try {
				String param = URLEncoder.encode("tipo_averia", "UTF-8") + "="
						+ URLEncoder.encode(this.id, "UTF-8");
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

			Log.v("getIncidencia", content);

			pd.dismiss();

			try {

				jsonResponse = new JSONObject(this.content);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("incidencia");

				// Toast.makeText(getActivity().getApplicationContext(),
				// jsonMainNode+"", Toast.LENGTH_SHORT).show();

				int lengthJsonArr = jsonMainNode.length();
				indicencias = new String[lengthJsonArr];

				if (lengthJsonArr > 0) {

					datosIncidencia = new ArrayList<Incidencias>();

					for (int i = 0; i < lengthJsonArr; i++) {
						JSONObject jsonChildNode;

						try {
							jsonChildNode = jsonMainNode.getJSONObject(i);

							datosambiente.add(new Ambientes(jsonChildNode
									.optString("id_incidencia").toString(),
									jsonChildNode.optString("incidencia")
											.toString()));

							indicencias[i] = jsonChildNode.optString(
									"incidencia").toString();

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					adapter_incidencias = new ArrayAdapter<String>(
							getActivity().getApplicationContext(),
							R.layout.spinner_item, indicencias);
					adapter_incidencias
							.setDropDownViewResource(R.layout.spiner_lista);
					s_incidencia.setAdapter(adapter_incidencias);

				} else {
					Toast.makeText(getActivity().getApplicationContext(),
							"hubo un error al descargar los datos",
							Toast.LENGTH_SHORT).show();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	class CustomOnItemSelectedListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			
			//if(arg2 != 0){
			id_averia = arg2 + 1 + "";
			id_incidencia = datosambiente.get(arg2).getId();
			new IncidenciasAsyncTask(id_averia, sp_incidencia).execute();
			//}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
}