package org.shokai.gpstracker;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

public class GpsTracker extends Activity implements OnClickListener, LocationListener{
    /** Called when the activity is first created. */
	
	private Button buttonPostLocation;
	private TextView textViewMessage;

    private final int MENU_ZOOM_IN = 1;
    private final int MENU_ZOOM_OUT = 2;
    private final int MENU_UPDATE_LOCATION = 3;
    private final int MENU_SHOW_LAST_LOCATION = 4;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.textViewMessage = (TextView)findViewById(R.id.textViewMessage);
        this.buttonPostLocation = (Button)findViewById(R.id.buttonPostLocation);
        buttonPostLocation.setOnClickListener(this);
    }

	public void onClick(View v) {
		switch(v.getId()){
		case R.id.buttonPostLocation:
			LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
			lm.removeUpdates(this);
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, this);
			
			Criteria crit = new Criteria();
			crit.setAccuracy(Criteria.ACCURACY_FINE);
			String provider = lm.getBestProvider(crit, true);
			Location loc = lm.getLastKnownLocation(provider);

	        double lat = loc.getLatitude();
	        double lon = loc.getLongitude();
			message("start: "+Double.toString(lat)+", "+Double.toString(lon));
			break;
		}
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      boolean supRetVal = super.onCreateOptionsMenu(menu);
      menu.add(0, this.MENU_ZOOM_IN, 0, "zoom in");
      menu.add(0, this.MENU_ZOOM_OUT, 1, "zoom out");
      menu.add(0, this.MENU_UPDATE_LOCATION, 2, "save");
      menu.add(0, this.MENU_SHOW_LAST_LOCATION, 3, "show last location");
      return supRetVal;
    }
	
	public void message(String mes){
		this.textViewMessage.setText(mes + "\r\n" + this.textViewMessage.getText());
	}

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
        double lat = location.getLatitude();
        double lon = location.getLongitude();
		message("update: "+Double.toString(lat)+", "+Double.toString(lon));
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
}