package org.shokai.gpstracker;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

public class GpsTracker extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	
	private Button buttonPostLocation;
	private TextView textViewMessage;

    private final int MENU_ZOOM_IN = 1;
    private final int MENU_ZOOM_OUT = 2;
    private final int MENU_SAVE_LOCATION = 3;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.textViewMessage = (TextView)findViewById(R.id.textViewMessage);
        this.buttonPostLocation = (Button)findViewById(R.id.buttonPostLocation);
        buttonPostLocation.setOnClickListener(this);
    }

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.buttonPostLocation:
			LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
			Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            
            double lat = location.getLatitude() * 1E6;
            double lon = location.getLongitude() * 1E6;
			message(Double.toString(lat)+", "+Double.toString(lon));
			break;
		}
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      boolean supRetVal = super.onCreateOptionsMenu(menu);
      menu.add(0, this.MENU_ZOOM_IN, 0, "zoom in");
      menu.add(0, this.MENU_ZOOM_OUT, 1, "zoom out");
      menu.add(0, this.MENU_SAVE_LOCATION, 2, "save");
      return supRetVal;
    }
	
	public void message(String mes){
		this.textViewMessage.setText(mes + "\r\n" + this.textViewMessage.getText());
	}
}