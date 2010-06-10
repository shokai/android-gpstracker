package org.shokai.gpstracker;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.util.Log;

public class SendLogFileSelectDialogClickListener implements OnClickListener {
    
    private GpsLog log;
    private GpsTracker gpsTracker;
    
    public SendLogFileSelectDialogClickListener(GpsTracker gpsTracker, GpsLog log){
        this.log = log;
        this.gpsTracker = gpsTracker;
    }
    

    public void onClick(DialogInterface dialog, int which) {
        try{
            String name = log.fileNames()[which];
            if(!log.hasSdCard()) return;
            String fullName = log.getDir() + "/" + name;

            Intent it = new Intent();
            it.putExtra(Intent.EXTRA_SUBJECT, "GpsTracker Log: " + name);
            it.setAction(Intent.ACTION_SEND);
            it.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + fullName));
            it.setType("text/plain");
            gpsTracker.startActivity(it);
        }
        catch(Exception e){
            Log.e("GpsTracker", e.getMessage());
        }
    }
    

}
