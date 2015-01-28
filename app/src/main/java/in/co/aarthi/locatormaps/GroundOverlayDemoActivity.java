package in.co.aarthi.locatormaps;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class GroundOverlayDemoActivity extends FragmentActivity
        implements OnSeekBarChangeListener, OnMapReadyCallback {
    private static final int TRANSPARENCY_MAX = 100;

    private final List<BitmapDescriptor> mImages = new ArrayList<BitmapDescriptor>();
    LatLngBounds newarkBounds = new LatLngBounds(
            new LatLng(12.982853, 79.967206),       // South west corner
            new LatLng(12.989023, 79.976680));
    private GroundOverlay mGroundOverlay;
    private SeekBar mTransparencyBar;

    private int mCurrentEntry = 0;
    Button b1;
    GoogleMap googleMap;
    MarkerOptions markerOptions;
    LatLng latLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ground_overlay_demo);

        mTransparencyBar = (SeekBar) findViewById(R.id.transparencySeekBar);
        mTransparencyBar.setMax(TRANSPARENCY_MAX);
        mTransparencyBar.setProgress(0);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
         googleMap = mapFragment.getMap();
        b1 = (Button) findViewById(R.id.btn_find);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etLocation = (EditText) findViewById(R.id.et_location);

                // Getting user input location
                String location = etLocation.getText().toString();

                if (location != null && !location.equals("")) {
                    new GeocoderTask().execute(location);

                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.987501, 79.971542), 13));
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        map.addMarker(new MarkerOptions().position(new LatLng(12.988248, 79.969976)).title("Hostel"));
        map.addMarker(new MarkerOptions().position(new LatLng(12.986340, 79.972411)).title("canteen"));
        map.addMarker(new MarkerOptions().position(new LatLng(12.986293, 79.973200)).title("cricketground"));
        map.addMarker(new MarkerOptions().position(new LatLng(12.990135, 79.971104)).title("swimming pool"));

        mImages.clear();
        mImages.add(BitmapDescriptorFactory.fromResource(R.drawable.map));
        map.setMyLocationEnabled(true);
        mCurrentEntry = 0;
        mGroundOverlay = map.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.map))
                .positionFromBounds(newarkBounds));
        map.getUiSettings().setZoomControlsEnabled(true);
        mTransparencyBar.setOnSeekBarChangeListener(this);

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mGroundOverlay != null) {
            mGroundOverlay.setTransparency((float) progress / (float) TRANSPARENCY_MAX);
        }
    }

    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if (addresses == null || addresses.size() == 0) {
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }

            // Clears all the existing markers on the map
           // googleMap.clear();

            // Adding Markers on Google Map for each matching address
           // for (int i = 0; i < addresses.size(); i++) {

                Address address = (Address) addresses.get(0);

                // Creating an instance of GeoPoint, to display in Google Map
                latLng = new LatLng(address.getLatitude(), address.getLongitude());

                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());

                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressText);

                googleMap.addMarker(markerOptions);

                // Locate the first location
              //  if (i == 0)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            //}
        }
    }

}