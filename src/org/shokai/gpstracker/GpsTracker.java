package org.shokai.gpstracker;

import android.os.Bundle;
import com.google.android.maps.*;

import android.app.*;
import android.content.Context;
import android.location.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import java.util.*;

public class GpsTracker extends MapActivity implements LocationListener {

    private MapView map;
    private TextView textViewMessage;
    private LocationManager lm;
    private MyLocationOverlay myOverlay;
    private final int zoom_default = 18;
    private boolean location_enalbed, log_enabled; // GPSとコンパスを動かしているかどうか、logを表示しているかどうか
    private LogOverlay logOverlay;
    private GpsLog log;

    private static class MenuId {
        private static final int START_GPS = 1;
        private static final int LAST_LOCATION = 2;
        private static final int SET_ZOOM = 3;
        private static final int SATELLITE_TOGGLE = 4;
        private static final int LOG_TOGGLE = 5;
    }

    public GpsTracker() {
        this.location_enalbed = false;
        this.log_enabled = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.log = new GpsLog(this);
        this.logOverlay = new LogOverlay(log);
        
        this.textViewMessage = (TextView) findViewById(R.id.textViewMessage);
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        map = (MapView) findViewById(R.id.mapview);
        myOverlay = new MyLocationOverlay(getApplicationContext(), map);
        myOverlay.onProviderEnabled(LocationManager.GPS_PROVIDER);
        map.getOverlays().add(myOverlay);
        map.getOverlays().add(logOverlay);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean supRetVal = super.onCreateOptionsMenu(menu);
        menu.add(0, MenuId.START_GPS, 0, "Start GPS");
        menu.add(0, MenuId.LAST_LOCATION, 0, "Last Location");
        menu.add(0, MenuId.SET_ZOOM, 0, "Zoom");
        menu.add(0, MenuId.SATELLITE_TOGGLE, 0, "Satellite/Map");
        menu.add(0, MenuId.LOG_TOGGLE, 0, "Hide Logs");
        return supRetVal;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MenuId.START_GPS:
            trace("Menu - Start GPS");
            if (!this.location_enalbed) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 10, this); // 20(sec), 10(meter)
                myOverlay.enableMyLocation();
                myOverlay.enableCompass();
                message("Start GPS");
                this.location_enalbed = true;
                item.setTitle("Stop GPS");
            } else {
                lm.removeUpdates(this);
                myOverlay.disableCompass();
                myOverlay.disableMyLocation();
                message("Stop GPS");
                this.location_enalbed = false;
                item.setTitle("Start GPS");
            }
            break;
        case MenuId.LAST_LOCATION:
            trace("Menu - Last Location");
            Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(loc == null){
                new AlertDialog.Builder(this).setMessage("Please Start GPS").setPositiveButton("OK", null).show();
            }
            else{
                double lat = loc.getLatitude();
                double lon = loc.getLongitude();
                message("last lat:" + Double.toString(lat) + ", lon:" + Double.toString(lon));
                this.setPosition(lat, lon, this.zoom_default);
            }
            break;
        case MenuId.SET_ZOOM:
            trace("Menu - Set Zoom");
            MapController mc = map.getController();
            mc.setZoom(this.zoom_default);
            break;
        case MenuId.SATELLITE_TOGGLE:
            trace("Menu - Satellite Toggle");
            if (map.isSatellite()) {
                map.setSatellite(false);
            } else {
                map.setSatellite(true);
            }
            break;
        case MenuId.LOG_TOGGLE:
            trace("Menu - Log Toggle : " + !log_enabled);
            if (this.log_enabled != true) {
                map.getOverlays().add(logOverlay);
                item.setTitle("Hide Logs");
                message("logs: " + Integer.toString(log.size()));
                log_enabled = true;
            } else {
                map.getOverlays().remove(logOverlay);
                item.setTitle("Show Logs");
                log_enabled = false;
            }
            map.invalidate(); // すぐ再描画
            break;
        }
        return true;
    }

    public void setPosition(double lat, double lon, int zoom) {
        MapController mc = map.getController();
        GeoPoint p = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
        mc.setCenter(p);
        mc.setZoom(zoom);
        this.myOverlay.getMyLocation();
    }

    // zoomは変更せずに地図だけ動かす
    public void setPosition(double lat, double lon) {
        this.setPosition(lat, lon, map.getZoomLevel());
    }

    public void message(String mes) {
        trace("message - " + mes);
        this.textViewMessage.setText(mes);
    }

    public void trace(String message){
        Log.v("GpsTracker", message);
    }

    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        message("lat:" + Double.toString(lat) + ", lon:" + Double.toString(lon));
        this.setPosition(lat, lon);
        log.add(lat, lon);
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
}