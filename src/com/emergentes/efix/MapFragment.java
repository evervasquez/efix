package com.emergentes.efix;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapFragment extends Fragment{
    private View view;
    private static LatLng UPV = new LatLng(-6.4859053724667, -76.3701475006103);;
    private GoogleMap mapa;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
    	if (view != null) {

            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        try {
            view = (ViewGroup) inflater.inflate(R.layout.map_fragment, container,
                    false);
            mapa = ((SupportMapFragment) getActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
        } catch (Exception e) {
        }
        return view;
    }
        
    @Override
    public void onResume() {
    	
    	mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(MapFragment.UPV, 13));
        mapa.setMyLocationEnabled(true);
    	super.onResume();
    }

	@Override
	public void onPause() {
		
		super.onPause();
	}
    
    
}

