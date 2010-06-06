package org.shokai.gpstracker;

import java.util.Calendar;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class LogPoint extends GeoPoint {

    private Paint paint;
    private int year, month, day, hour, min, sec;
        
    public LogPoint(int latitudeE6, int longitudeE6) {
        super(latitudeE6, longitudeE6);
        this.paint = new Paint();
        this.paint.setARGB(255, 255, 0, 0);
        this.paint.setStrokeWidth(2);
        this.paint.setDither(true);
        this.paint.setStyle(Style.FILL);
        this.paint.setAntiAlias(true);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        
        Calendar cal = Calendar.getInstance();
        this.year = cal.get(Calendar.YEAR);
        this.month = cal.get(Calendar.MONTH) + 1;
        this.day = cal.get(Calendar.DAY_OF_MONTH);
        this.hour = cal.get(Calendar.HOUR_OF_DAY);
        this.min = cal.get(Calendar.MINUTE);
        this.sec = cal.get(Calendar.SECOND);
    }
    
    public String toLog(){
        double lat = ((double)this.getLatitudeE6())/1E6;
        double lon = ((double)this.getLongitudeE6())/1E6;
        String str = new String(lat+", " +
                                lon+", " +
                                hour+":"+min+":"+sec +
                                "\n");
        return str;
    }
    
    public static LogPoint parse(String log) throws Exception{
        String[] items = log.split("[    ]*,[    ]*");
        if(items.length < 2) throw new Exception("log format error");
        else{ // 経緯度
            LogPoint p;
            double lat = Double.parseDouble(items[0]);
            double lon = Double.parseDouble(items[1]);
            Log.v("GpsTracker", "load - lat:"+lat+", lon:"+lon);
            p = new LogPoint((int) (lat * 1E6), (int) (lon * 1E6));
            if(items.length > 2){ // 時刻
                String[] time = items[2].split(":");
                p.setHour(Integer.parseInt(time[0]));
                p.setMin(Integer.parseInt(time[1]));
                p.setSec(Integer.parseInt(time[2]));
            }
            return p;
        }
    }
    
    public Paint getPaint(){
        return paint;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMin() {
        return min;
    }

    public int getSec() {
        return sec;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setSec(int sec) {
        this.sec = sec;
    }
    

}
