package com.dso30bt.project2019.engineerdashboard.activities;

import android.os.Bundle;

import com.dso30bt.project2019.engineerdashboard.R;
import com.dso30bt.project2019.engineerdashboard.models.Coordinates;
import com.dso30bt.project2019.engineerdashboard.models.Report;
import com.dso30bt.project2019.engineerdashboard.repository.UserImpl;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentActivity;

public class ReportsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        showReportsOnMap();
    }

    public void showReportsOnMap() {
        List<Marker> markers = new ArrayList<>();

        new UserImpl(this)
                .getReports(reportList -> {
                    if (reportList.size() > 0) {
                        for (Report report : reportList) {
                            Coordinates coordinates = report.getPothole().getCoordinates();
                            final double lat = coordinates.getLatitude();
                            final double lng = coordinates.getLongitude();
                            final String reportBy = report.getReportedBy();

                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(lat, lng))
                                    .title(reportBy)
                                    .snippet("Reported on: " + report.getReportDate()));
                            markers.add(marker);

                        }

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (Marker marker : markers) {
                            builder.include(marker.getPosition());
                        }

                        LatLngBounds bounds = builder.build();
                        int padding = 0; // offset from edges of the map in pixels
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.animateCamera(cu);
                    }
                });
    }
}
