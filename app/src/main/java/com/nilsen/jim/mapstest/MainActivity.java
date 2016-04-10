package com.nilsen.jim.mapstest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nilsen.jim.mapstest.sqlite.LatLngDataSource;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback {
    GoogleMap mMap;
    ArrayList<LatLng> points = new ArrayList<>();
    ArrayList<Polyline> polylines = new ArrayList<>();
    ArrayList<Marker> markers = new ArrayList<>();
    LocationManager locationManager;
    LocationListener onLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) throws SecurityException{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        onLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) throws SecurityException {
            }

            @Override
            public void onProviderEnabled(String provider) throws SecurityException {
                mMap.setMyLocationEnabled(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
            }

            @Override
            public void onProviderDisabled(String provider) throws SecurityException{
                mMap.setMyLocationEnabled(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_localdata:
                new LatLngDataSource(this).deleteAllStoredData();
                points.clear();
                for(Polyline polyline : polylines) {
                    polyline.remove();
                }
                polylines.clear();
                for(Marker marker : markers) {
                    marker.remove();
                }
                markers.clear();
                return true;
            case R.id.change_maptype:
                AlertDialog.Builder settingsBuilder = new AlertDialog.Builder(this);
                settingsBuilder.setTitle(R.string.change_maptype);

                CharSequence[] maptypes = new CharSequence[] {getString(R.string.normal), getString(R.string.satellite), getString(R.string.terrain), getString(R.string.hybrid)};
                settingsBuilder.setItems(maptypes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                        break;
                                    case 1:
                                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                        break;
                                    case 2:
                                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                        break;
                                    case 3:
                                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                        break;
                                }
                            }
                        });
                settingsBuilder.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onResume() throws SecurityException {
        super.onResume();
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            DialogInterface.OnClickListener dialog = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) throws SecurityException{
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setMessage(getString(R.string.query_location));
            alertBuilder.setPositiveButton(getString(R.string.accept), dialog);
            alertBuilder.setNegativeButton(getString(R.string.decline), dialog);
            alertBuilder.show();

        }

        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(provider, (long) 1000, 5.0f, onLocationListener);

        if(mMap!= null) mMap.setMyLocationEnabled(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
    }

    @Override
    protected void onPause() throws SecurityException{
        super.onPause();
        locationManager.removeUpdates(onLocationListener);
    }

    private void addStoredPoints() {
        points = new LatLngDataSource(this).getAllLatLngPositions();
        for(LatLng latLng : points){
            markers.add(mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_run))));
        }
        DrawLines drawLines = new DrawLines();
        drawLines.execute(points);
        if(points.size() > 0){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(points.get(0), 7));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) throws SecurityException{
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        addStoredPoints();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                markers.add(mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_run))));
                points.add(latLng);
                DrawLines drawLines = new DrawLines();
                drawLines.execute(points);
                new LatLngDataSource(getApplicationContext()).createLatLngData(latLng.latitude, latLng.longitude);
            }
        });
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(provider, (long) 1000, 5.0f, onLocationListener);
        mMap.setMyLocationEnabled(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
    }

    private class DrawLines extends AsyncTask<ArrayList<LatLng>, Integer, Integer>{
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
            polylines.add(mMap.addPolyline(polylineOptions));
        }
    }
}