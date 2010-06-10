package org.shokai.gpstracker;

import com.google.android.maps.GeoPoint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;

public class LoadLogFileSelectDialogClickListener implements OnClickListener {
    
    private GpsLog log;
    private GpsTracker gpsTracker;
    
    public LoadLogFileSelectDialogClickListener(GpsTracker gpsTracker, GpsLog log){
        this.log = log;
        this.gpsTracker = gpsTracker;
    }
    

    public void onClick(DialogInterface dialog, int which) {
        try{
            String name = log.fileNames()[which];
            log.loadLog(name);
            gpsTracker.message("load : " + name + " - " + log.size() + "logs");
            GeoPoint p = log.getPoint(log.size()-1);
            double lat = ((double)p.getLatitudeE6()) / 1E6;
            double lon = ((double)p.getLongitudeE6()) / 1E6;
            gpsTracker.setPosition(lat, lon);
        }
        catch(Exception e){
            Log.e("GpsTracker", e.getMessage());
        }
    }
    

}
