package com.nilsen.jim.mapstest;

import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements
        OnMapReadyCallback {
    GoogleMap mMap;
    ArrayList<LatLng> points = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_run)));
                points.add(latLng);
                DrawLines drawLines = new DrawLines();
                drawLines.execute(points);
            }
        });
    }

    private class DrawLines extends AsyncTask<ArrayList<LatLng>, Integer, Integer>{
        ArrayList<LatLng> points = new ArrayList<>();
        PolylineOptions polylineOptions = null;
        @Override
        protected Integer doInBackground(ArrayList<LatLng>... params) {
            points = params[0];
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            polylineOptions = new PolylineOptions();
            polylineOptions.addAll(points);
            polylineOptions.width(5);
            polylineOptions.color(Color.BLUE);
            mMap.addPolyline(polylineOptions);

        }
    }
}
