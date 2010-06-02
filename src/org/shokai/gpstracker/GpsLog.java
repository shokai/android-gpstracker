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
            new AlertDialog.Builder(context).setMessage("this app uses SD Card").setPositiveButton("OK", null).show();
        }
        try{
            this.loadLog(); 
        }
        catch(Exception e){
            Log.e("GpsTracker", "log load error");
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
    
    public boolean loadLog() throws Exception{
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        try{
            return this.loadLog(year, month, day);
        }
        catch(Exception e){
            throw e;
        }
    }
    
    public boolean loadLog(int year, int month, int day) throws Exception{
        File f = new File(this.dataDir, year+"-"+month+"-"+day+".txt");
        try{
            return this.loadLog(f);
        }
        catch(Exception e){
            throw e;
        }
    }
    
    public boolean loadLog(File f) throws Exception{
        if(!f.exists()) return false;
        context.trace("load Log - " + f.getPath());
        try{
            InputStream is = new FileInputStream(f);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            is.close();
            String[] lines = new String(bytes).split("\n");
            List<GeoPoint> points = new ArrayList<GeoPoint>();
            for(String line : lines){
                String[] items = line.split("[    ]*,[    ]*");
                if(items.length > 1){
                    double lat = Double.parseDouble(items[0]);
                    double lon = Double.parseDouble(items[1]);
                    context.trace("load Log - "+lat + ", " + lon);
                    points.add( new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6)) );
                }
            }
            if (points.size() > 0){
                this.points = points;
                return true;
            }
            return false;
        }
        catch(Exception e){
            throw e;
        }
    }
    
}
