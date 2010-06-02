package org.shokai.gpstracker;

import java.util.*;
import java.io.*;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.android.maps.GeoPoint;


public class GpsLog {
    
    private List<GeoPoint> points;
    private GpsTracker context;
    private File dataDir;
    
    public GpsLog(GpsTracker context){
        this.context = context;
        this.points = new ArrayList<GeoPoint>();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dataDir = new File(Environment.getExternalStorageDirectory(), context.getPackageName()+"/log");
            dataDir.mkdirs();
            context.trace("data dir : "+ dataDir.getPath());
        }
        else{
            context.trace("SD Card not Found");
            new AlertDialog.Builder(context).setMessage("SDカードが必要です").setPositiveButton("OK", null).show();
        }
    }
    
    public List<GeoPoint> load(int year, int month, int day){
        return new ArrayList<GeoPoint>();
    }
    
    public List<GeoPoint> getPoints(){
        return this.points;
    }
    
    public int size(){
        return this.points.size();
    }
    
    public void add(double lat, double lon){
        this.points.add(new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6)));
        context.trace("add points : " + points.size());
        try{
            saveLog(lat, lon);
        }
        catch(Exception e){
            Log.e("GpsTracker", e.toString());
        }
    }
    
    private void saveLog(double lat, double lon) throws Exception{
        if (dataDir == null) return;
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        File f = new File(this.dataDir, year+"-"+month+"-"+day+".txt");
        String str = new String(lat+", " +
                lon+", " +
                hour+"-"+min+"-"+sec +
                "\n");
        try{
            OutputStream os = new FileOutputStream(f, true); // append mode
            os.write(str.getBytes());
            os.close();
        }
        catch(Exception e){
            throw e;
        }
        context.trace("saveLog : " + f.getPath());
    }
    
}
