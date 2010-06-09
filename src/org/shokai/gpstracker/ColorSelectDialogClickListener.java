package org.shokai.gpstracker;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class ColorSelectDialogClickListener implements OnClickListener {
    
    private GpsLog log;
    public static String[] colors = {"Red", "Green", "Blue"};
    
    public ColorSelectDialogClickListener(GpsLog log){
        this.log = log;
    }
    
    public void onClick(DialogInterface dialog, int which) {
        // TODO Auto-generated method stub
        switch(which){
        case 0:
            log.setR(255);
            log.setG(0);
            log.setB(0);
            break;
        case 1:
            log.setR(0);
            log.setG(255);
            log.setB(0);
            break;
        case 2:
            log.setR(0);
            log.setG(0);
            log.setB(255);
            break;
        }
    }

}
