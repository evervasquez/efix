package com.emergentes.efix.utils;

import java.io.File;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class SubirImagen extends AsyncTask<Object, Object, Object>{
 
        ProgressDialog pDialog;
        File miFoto;
        Context actividad;
        String url;
        
        public SubirImagen(String url,File file, Context activity) {
        		this.miFoto = file;
        		this.actividad = activity;
        		this.url = url;	
		}
        
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(actividad);
            pDialog.setMessage("Subiendo la imagen, espere..." );
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        
        protected void onPostExecute(String result) {	
        	super.onPostExecute(result);
            pDialog.dismiss();
            Toast.makeText(actividad.getApplicationContext(), "Datos Enviados Correctamente", Toast.LENGTH_LONG).show();
        }

		@Override
		protected String doInBackground(Object... arg0) {
			String result = null;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                HttpPost httppost = new HttpPost(url);
                MultipartEntity mpEntity = new MultipartEntity();
                ContentBody foto = new FileBody(miFoto, "image/jpeg");
                mpEntity.addPart("fotoUp", foto);
                httppost.setEntity(mpEntity);
                result = (String) httpclient.execute(httppost, new ResponseImagen());
                onPostExecute(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
		}
}