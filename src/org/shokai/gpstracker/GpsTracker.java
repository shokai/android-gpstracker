package org.shokai.gpstracker;

import android.os.Bundle;
import com.google.android.maps.*;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.location.*;
import android.util.Log;
import android.view.*;
import android.widget.*;

public class GpsTracker extends MapActivity implements LocationListener, DialogInterface.OnClickListener {

    private MapView map;
    private TextView textViewMessage;
    private LocationManager lm;
    private MyLocationOverlay myOverlay;
    private final int zoom_default = 18;
    private boolean location_enalbed, log_enabled; // GPSとコンパスを動かしているかどうか、logを表示しているかどうか
    private LogOverlay logOverlay;
    private GpsLog log;
    private boolean paused;

    private static class MenuId {
        private static final int START_GPS = 1;
        private static final int LAST_LOCATION = 2;
        private static final int SET_ZOOM = 3;
        private static final int SATELLITE_TOGGLE = 4;
        private static final int LOG_TOGGLE = 5;
        private static final int SELECT_LOGFILE = 6;
        private static final int SELECT_COLOR = 7;
    }

    public GpsTracker() {
        this.location_enalbed = false;
        this.log_enabled = true;
        this.paused = false;
    }
    
    @Override
    public void onPause(){
        super.onPause();
        trace("onPause");
        this.paused = true;
        if(this.location_enalbed && this.myOverlay != null){
            myOverlay.disableCompass();
            myOverlay.disableMyLocation();
        }
    }
    
    @Override
    public void onResume(){
        super.onResume();
        trace("onResume");
        this.paused = false;
        if(this.location_enalbed && this.myOverlay != null){
            myOverlay.enableCompass();
            myOverlay.enableMyLocation();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.log = new GpsLog(this);
        this.logOverlay = new LogOverlay(log);
        this.logOverlay.setMaxLines(1000);
        
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
        menu.add(0, MenuId.START_GPS, 0, "Start GPS").setIcon(android.R.drawable.ic_menu_mylocation);
        menu.add(0, MenuId.LAST_LOCATION, 0, "Last Location").setIcon(android.R.drawable.ic_menu_revert);
        menu.add(0, MenuId.SET_ZOOM, 0, "Zoom").setIcon(android.R.drawable.ic_menu_zoom);
        menu.add(0, MenuId.SATELLITE_TOGGLE, 0, "Satellite/Map").setIcon(android.R.drawable.ic_menu_mapmode);
        menu.add(0, MenuId.LOG_TOGGLE, 0, "Hide Log").setIcon(android.R.drawable.ic_menu_view);
        menu.add(0, MenuId.SELECT_LOGFILE, 0, "Select Log").setIcon(android.R.drawable.ic_menu_recent_history);
        menu.add(0, MenuId.SELECT_COLOR, 0, "Line Color").setIcon(android.R.drawable.ic_menu_edit);
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
                item.setTitle("Hide Log");
                message("logs: " + Integer.toString(log.size()));
                log_enabled = true;
            } else {
                map.getOverlays().remove(logOverlay);
                item.setTitle("Show Log");
                log_enabled = false;
            }
            map.invalidate(); // すぐ再描画
            break;
        case MenuId.SELECT_LOGFILE:
            trace("Menu - Select Log");
            if(log.fileNames().length < 1){
                new AlertDialog.Builder(this).setMessage("No Logs").setPositiveButton("OK", null).show();
            }
            else{
                new AlertDialog.Builder(this).setTitle("Select Log").setItems(log.fileNames(), this).show();
            }
            break;
        case MenuId.SELECT_COLOR:
            trace("Menu - Select Color");
            new AlertDialog.Builder(this).setTitle("Select Log Color")
                .setItems(ColorSelectDialogClickListener.colors, new ColorSelectDialogClickListener(log))
                .show();
            break;
        }
        return true;
    }

    public void onClick(DialogInterface dialog, int which) {
        try{
            String name = log.fileNames()[which];
            log.loadLog(name);
            this.message("load : " + name + " - " + log.size() + "logs");
            GeoPoint p = log.getPoint(log.size()-1);
            double lat = ((double)p.getLatitudeE6()) / 1E6;
            double lon = ((double)p.getLongitudeE6()) / 1E6;
            this.setPosition(lat, lon);
        }
        catch(Exception e){
            Log.e("GpsTracker", e.getMessage());
        }
    }

    public void setPosition(double lat, double lon, int zoom) {
        MapController mc = map.getController();
        GeoPoint p = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
        mc.animateTo(p);
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
        log.add(lat, lon);
        if(!paused){
            message("lat:" + Double.toString(lat) + ", lon:" + Double.toString(lon));
            this.setPosition(lat, lon);
        }
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