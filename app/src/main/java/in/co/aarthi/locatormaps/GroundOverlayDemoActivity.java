package in.co.aarthi.locatormaps;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

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
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.987501, 79.971542),13));
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        map.addMarker(new MarkerOptions().position(new LatLng(12.988248, 79.969976)).title("Hostel"));
        map.addMarker(new MarkerOptions().position(new LatLng(12.986340, 79.972411)).title("canteen"));
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

  }
