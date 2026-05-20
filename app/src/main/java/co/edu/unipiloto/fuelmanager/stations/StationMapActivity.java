package co.edu.unipiloto.fuelmanager.stations;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.local.DatabaseHelper;
import co.edu.unipiloto.fuelmanager.data.model.Station;


public class StationMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG                     = "StationMapActivity";
    private static final int    LOCATION_PERMISSION_REQ = 1001;
    private static final LatLng BOGOTA_CENTER           = new LatLng(4.6097, -74.0817);
    private static final float  DEFAULT_ZOOM            = 12f;
    private static final String CIUDAD                  = ", Bogotá, Colombia";


    private FusedLocationProviderClient fusedClient;
    private Geocoder                    geocoder;

    private GoogleMap      map;
    private DatabaseHelper db;
    private List<Station>  stations;
    private Station        cheapestStation;

    private LatLng userLatLng = null;


    private final List<StationLatLng> resolvedMarkers = new ArrayList<>();

    private static final NumberFormat COP =
            NumberFormat.getInstance(new Locale("es", "CO"));

    private static class StationLatLng {
        final Station station;
        final LatLng  latLng;
        StationLatLng(Station s, LatLng l) { station = s; latLng = l; }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_map);

        db          = DatabaseHelper.getInstance(this);
        geocoder    = new Geocoder(this, new Locale("es", "CO"));
        fusedClient = LocationServices.getFusedLocationProviderClient(this);

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());


        if (findViewById(R.id.btnPlanRoute) != null)
            findViewById(R.id.btnPlanRoute).setOnClickListener(v -> onPlanRoute());

        new Thread(() -> {
            stations        = db.getAllStations();
            cheapestStation = findCheapest(stations);
            resolveCoordinatesWithGeocoder();
            runOnUiThread(this::onCoordinatesReady);
        }).start();

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) mapFragment.getMapAsync(this);
    }


    private void resolveCoordinatesWithGeocoder() {
        if (stations == null) return;
        for (Station s : stations) {
            LatLng coords = geocodeAddress(s.getAddress() + CIUDAD);
            resolvedMarkers.add(new StationLatLng(s, coords));
        }
    }

    private LatLng geocodeAddress(String address) {
        try {
            List<Address> results = geocoder.getFromLocationName(address, 1);
            if (results != null && !results.isEmpty()) {
                Address found = results.get(0);
                return new LatLng(found.getLatitude(), found.getLongitude());
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder error: " + e.getMessage());
        }
        return BOGOTA_CENTER;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        try {
            map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark));
        } catch (Exception ignored) {}

        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);

        map.setInfoWindowAdapter(new StationInfoWindowAdapter(this));

        map.setOnInfoWindowClickListener(marker -> {
            Object tag = marker.getTag();
            if (tag instanceof Station) {
                onNavigateToStation((Station) tag, marker.getPosition());
            }
        });

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(BOGOTA_CENTER, DEFAULT_ZOOM));
        if (!resolvedMarkers.isEmpty()) addMarkers();

        requestLocationAndCenter();
    }


    private void onCoordinatesReady() {
        TextView tvCount = findViewById(R.id.tvStationCount);
        if (tvCount != null && stations != null)
            tvCount.setText(stations.size()
                    + " estaciones · 🟢 más barata · toca info para navegar");
        if (map != null) addMarkers();
    }

    private void addMarkers() {
        if (map == null || resolvedMarkers.isEmpty()) return;
        for (StationLatLng item : resolvedMarkers) {
            Station s = item.station;
            boolean isCheapest = cheapestStation != null
                    && s.getId() == cheapestStation.getId();
            float hue = isCheapest
                    ? BitmapDescriptorFactory.HUE_GREEN
                    : BitmapDescriptorFactory.HUE_ORANGE;

            MarkerOptions opts = new MarkerOptions()
                    .position(item.latLng)
                    .title(s.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(hue));

            Marker marker = map.addMarker(opts);
            if (marker != null) marker.setTag(s);
        }
    }



    private void onNavigateToStation(Station station, LatLng destination) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.google.com")
                .appendPath("maps")
                .appendPath("dir")
                .appendPath("")
                .appendQueryParameter("api", "1")
                .appendQueryParameter("destination",
                        destination.latitude + "," + destination.longitude);


        if (userLatLng != null) {
            builder.appendQueryParameter("origin",
                    userLatLng.latitude + "," + userLatLng.longitude);
        }

        builder.appendQueryParameter("travelmode", "driving");

        String url = builder.build().toString();
        Log.d(TAG, "Directions URL → " + url);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.setPackage("com.google.android.apps.maps");


        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this,
                    "Google Maps no está instalado en este dispositivo.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void onPlanRoute() {
        if (resolvedMarkers.isEmpty()) {
            Toast.makeText(this, "Cargando estaciones, espera un momento.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (resolvedMarkers.size() < 2) {
            Toast.makeText(this, "Se necesitan al menos 2 estaciones para planear ruta.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String origin;
        if (userLatLng != null) {
            origin = userLatLng.latitude + "," + userLatLng.longitude;
        } else {
            LatLng first = resolvedMarkers.get(0).latLng;
            origin = first.latitude + "," + first.longitude;
        }

        StationLatLng lastItem = resolvedMarkers.get(resolvedMarkers.size() - 1);
        String destination = lastItem.latLng.latitude + "," + lastItem.latLng.longitude;


        StringBuilder waypointsBuilder = new StringBuilder();
        int limit = Math.min(resolvedMarkers.size() - 1, 8);
        for (int i = 0; i < limit; i++) {
            if (i > 0) waypointsBuilder.append("|");
            LatLng wl = resolvedMarkers.get(i).latLng;
            waypointsBuilder.append(wl.latitude).append(",").append(wl.longitude);
        }

        Uri gmmIntentUri = Uri.parse(
                "https://www.google.com/maps/dir/?api=1"
                        + "&origin="      + Uri.encode(origin)
                        + "&destination=" + Uri.encode(destination)
                        + "&waypoints="   + Uri.encode(waypointsBuilder.toString())
                        + "&travelmode=driving");

        Log.d(TAG, "Route URL → " + gmmIntentUri.toString());

        Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        intent.setPackage("com.google.android.apps.maps");

        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            try {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                startActivity(unrestrictedIntent);
            } catch (android.content.ActivityNotFoundException innerEx) {
                Toast.makeText(this,
                        "Por favor instala Google Maps para usar esta función.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void requestLocationAndCenter() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQ);
        }
    }

    private void enableMyLocation() {
        if (map == null) return;
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        map.setMyLocationEnabled(true);

        fusedClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14f));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQ
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        } else {
            Toast.makeText(this,
                    "Permiso de ubicación no concedido. Mostrando Bogotá.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private Station findCheapest(List<Station> list) {
        if (list == null || list.isEmpty()) return null;
        Station best = list.get(0);
        for (Station s : list) {
            if (s.getPriceCorriente() < best.getPriceCorriente()) best = s;
        }
        return best;
    }
}