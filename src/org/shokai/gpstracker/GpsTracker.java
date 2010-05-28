package org.shokai.gpstracker;

import android.os.Bundle;
import com.google.android.maps.*;
import android.content.Context;
import android.location.*;
import android.view.*;
import android.widget.*;

public class GpsTracker extends MapActivity implements LocationListener{
	
	private MapView map;
	private TextView textViewMessage;
	private LocationManager lm;
	private MyLocationOverlay myOverlay;
	private final int zoom_default = 18;
	private boolean location_enalbed = false; // GPSとコンパスを動かしているかどうか
	
	private static class MenuId{
    	private static final int START_GPS = 1;
    	private static final int LAST_LOCATION = 3;
    	private static final int SET_ZOOM = 4;
    	private static final int SATELLITE_TOGGLE = 5;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.textViewMessage = (TextView)findViewById(R.id.textViewMessage);
        lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        map = (MapView)findViewById(R.id.mapview);
        myOverlay = new MyLocationOverlay(getApplicationContext(), map);
        myOverlay.onProviderEnabled(LocationManager.GPS_PROVIDER);
        map.getOverlays().add(myOverlay);
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      boolean supRetVal = super.onCreateOptionsMenu(menu);
      menu.add(0, MenuId.START_GPS, 0, "Start GPS");
      menu.add(0, MenuId.LAST_LOCATION, 0, "Last Location");
      menu.add(0, MenuId.SET_ZOOM, 0, "Zoom");
      menu.add(0, MenuId.SATELLITE_TOGGLE, 0, "Show Satellite");
      return supRetVal;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case MenuId.START_GPS:
			if(!this.location_enalbed){
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this); // 5(sec), 10(meter)
		        myOverlay.enableMyLocation();
		        myOverlay.enableCompass();
		        message("Start GPS");
		        this.location_enalbed = true;
		        item.setTitle("Stop GPS");
			}
			else{
				lm.removeUpdates(this);
				myOverlay.disableCompass();
				myOverlay.disableMyLocation();
				message("Stop GPS");
				this.location_enalbed = false;
				item.setTitle("GPS Start");
			}
			break;
		case MenuId.LAST_LOCATION:
			Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			double lat = loc.getLatitude();
			double lon = loc.getLongitude();
			message("last lat:"+Double.toString(lat) + ", lon:" + Double.toString(lon));
			this.setPosition(lat, lon, this.zoom_default);
			break;
		case MenuId.SET_ZOOM:
			MapController mc = map.getController();
			mc.setZoom(this.zoom_default);
			break;
		case MenuId.SATELLITE_TOGGLE:
			if(map.isSatellite()){
				map.setSatellite(false);
				if(!map.isSatellite()) item.setTitle("Show Satellite");
			}
			else{
				map.setSatellite(true);
				if(map.isSatellite()) item.setTitle("Hide Satellite");
			}
			break;
    	}
    	return true;
    }
    
    public void setPosition(double lat, double lon, int zoom){
    	MapController mc = map.getController();
    	mc.setCenter(new GeoPoint( (int)(lat*1E6), (int)(lon*1E6) ));
    	mc.setZoom(zoom);
    	this.myOverlay.getMyLocation();
    }
	
    // zoomは変更せずに地図だけ動かす
    public void setPosition(double lat, double lon){
    	this.setPosition(lat, lon, map.getZoomLevel());
    	
    }
    
	public void message(String mes){
		this.textViewMessage.setText(mes);
	}

	public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
		message("lat:"+Double.toString(lat)+", lon:"+Double.toString(lon));
		this.setPosition(lat, lon);
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
	
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);        
        bundle.putString("textViewMessage", this.textViewMessage.getText().toString() );
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.textViewMessage.setText(bundle.getString("textViewMessage"));
    }
    
}