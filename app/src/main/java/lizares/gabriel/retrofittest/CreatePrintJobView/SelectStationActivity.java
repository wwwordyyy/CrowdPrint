package lizares.gabriel.retrofittest.CreatePrintJobView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import lizares.gabriel.retrofittest.R;
import lizares.gabriel.retrofittest.Retrofit.CrowdPrintAPI;
import lizares.gabriel.retrofittest.Retrofit.ServiceGenerator;
import lizares.gabriel.retrofittest.UserAuthentication.UserInformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectStationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private static final String TAG = "SelectStationActivity";
    private GoogleMap mMap;
    private Marker marker;
    LocationManager locationManager;
    CrowdPrintAPI client;

    ArrayList<PrintStationInfo> printStationList = new ArrayList<>();

    UserInformation userInformation;
    PrintJobSettings printJobSettings;
    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(lizares.gabriel.retrofittest.R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        userInformation = (UserInformation) getIntent().getSerializableExtra("userInformation");
        printJobSettings = (PrintJobSettings) getIntent().getSerializableExtra("printJobSettings");
        client = ServiceGenerator.CreateService(CrowdPrintAPI.class, userInformation.getAuthToken());
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(this,SelectPrinterActivity.class);
        PrintStationInfo selectedStation = (PrintStationInfo) marker.getTag();
        printJobSettings.setStationName(selectedStation.getStationName());
        intent.putExtra("station",selectedStation);
        intent.putExtra("userInformation",userInformation);
        intent.putExtra("printJobSettings",printJobSettings);
        startActivity(intent);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(LOCATION_PERMS, 41);
            return;
        }

        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
        mMap.setMinZoomPreference(18);

        mMap.setInfoWindowAdapter(new StationInfoWindowAdapter(this, userInformation));

        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastLocation != null) {
            LatLng currentLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            // marker = mMap.addMarker(new MarkerOptions().position(currentLocation));
        }

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                if (marker != null) {
                    marker.remove();
                }
                // marker = mMap.addMarker(new MarkerOptions().position(currentLocation));

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        Call<String> call = client.getStationList(userInformation.getUsername());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(TAG, response.body());
                try {
                    JSONArray stationList = new JSONArray(response.body());
                    for (int index = 0; index < stationList.length(); index++) {
                        PrintStationInfo printStationInfo = new PrintStationInfo();
                        printStationInfo.buildStationInfo(stationList.getString(index));
                        MarkerOptions markerOption = new MarkerOptions();
                        markerOption.position(new LatLng(printStationInfo.getLatitude(),printStationInfo.getLongitude()));
                        markerOption.title(printStationInfo.getStationName());
                        mMap.addMarker(markerOption).setTag(printStationInfo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
            }
        });



    }
}
