package org.shokai.gpstracker;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.TextView;

public class GpsTracker extends Activity implements LocationListener{
	
	private TextView textViewMessage;
	private LocationManager lm;
	
	private static class MenuId{
    	private static final int START_GPS = 1;
    	private static final int STOP_GPS = 2;
    	private static final int LAST_LOCATION = 3;
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.textViewMessage = (TextView)findViewById(R.id.textViewMessage);
        lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      boolean supRetVal = super.onCreateOptionsMenu(menu);
      menu.add(0, MenuId.START_GPS, 0, "Start GPS");
      menu.add(0, MenuId.STOP_GPS, 0, "Stop GPS");
      menu.add(0, MenuId.LAST_LOCATION, 0, "Last Location");
      return supRetVal;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case MenuId.START_GPS:
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this); // 5(sec), 10(meter)
			message("Start GPS");
			break;
		case MenuId.STOP_GPS:
			lm.removeUpdates(this);
			message("Stop GPS");
			
			break;
		case MenuId.LAST_LOCATION:
			Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			double lat = loc.getLatitude();
			double lon = loc.getLongitude();
			message("last: "+Double.toString(lat) + ", " + Double.toString(lon));
			break;
    	}
    	return true;
    }
	
	public void message(String mes){
		this.textViewMessage.setText(mes + "\n" + this.textViewMessage.getText());
	}

	public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
		message("update: "+Double.toString(lat)+", "+Double.toString(lon));
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}