package org.shokai.gpstracker;

import java.util.*;

import android.content.Context;

import com.google.android.maps.GeoPoint;


public class GpsLog {
    private List<GeoPoint> points;
    private Context context;
    
    public GpsLog(Context context){
        this.context = context;
        this.points = new ArrayList<GeoPoint>();
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
    
    public void add(GeoPoint p){
        this.points.add(p);
        // ファイルにも保存する
    }
    
    
    
}
