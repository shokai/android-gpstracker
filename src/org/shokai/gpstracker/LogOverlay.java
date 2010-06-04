package org.shokai.gpstracker;

import java.util.*;
import android.graphics.*;
import android.graphics.Paint.*;
import android.util.Log;
import com.google.android.maps.*;

public class LogOverlay extends Overlay {

    private Paint linePaint;
    private GpsLog log;
    private int maxLines = 1000;
    
    public LogOverlay(GpsLog log) {
        this.log = log;
        this.linePaint = new Paint();
        linePaint.setARGB(255, 255, 0, 0);
        linePaint.setStrokeWidth(2);
        linePaint.setDither(true);
        linePaint.setStyle(Style.FILL);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
    }
    
    public void setMaxLines(int num){
        if (num < 1) return;
        this.maxLines = num;
    }

    @Override
    public void draw(Canvas canvas, MapView view, boolean shadow) {
        List<GeoPoint> points = log.getPoints();
        int size = points.size();
        if (size < 2) return;

        GeoPoint center = view.getMapCenter();
        int top = center.getLatitudeE6() - view.getLatitudeSpan()/2;
        int bottom = center.getLatitudeE6() + view.getLatitudeSpan()/2;
        int left = center.getLongitudeE6() - view.getLongitudeSpan()/2;
        int right = center.getLongitudeE6() + view.getLongitudeSpan()/2;

        // 画面内にあるGeoPointをリストアップ
        boolean[] visibles = new boolean[size];
        int visibles_num = 0;
        for(int i = 0; i < size; i++){
            GeoPoint p = points.get(i);
            if( top < p.getLatitudeE6() &&  p.getLatitudeE6() < bottom &&
                left < p.getLongitudeE6() && p.getLongitudeE6() < right ){
                visibles[i] = true;
                visibles_num++;
            }
            else{
                visibles[i] = false;
            }
        }
        
        // 画面外への線を描画するためのGeoPointをリストアップ
        boolean[] borders = new boolean[size];
        for(int i = 0; i < size; i++){
            if(!visibles[i]){
                if( (i > 0 && visibles[i-1]) ||
                    (i < size-1 && visibles[i+1])){
                    borders[i] = true;
                }
            }
        }

        Point pa = new Point();
        Point pb = new Point();
        int count = 0;
        int visible_ratio = visibles_num / this.maxLines + 1;
        if(visible_ratio > 1) Log.v("GpsTracker", "visibles : " + visibles_num + ", max : " + this.maxLines);
        for (int i = 0; i < size - 1; i++) {
            if(visibles[i] && visibles[i+1]){
                if(count++ % visible_ratio == 0){ // 線が増えすぎないように間引く
                    view.getProjection().toPixels(points.get(i), pa);
                    view.getProjection().toPixels(points.get(i + 1), pb);
                    canvas.drawLine(pa.x, pa.y, pb.x, pb.y, linePaint);
                }
            }
            if((visibles[i] && borders[i+1]) || (borders[i] && visibles[i+1])){
                view.getProjection().toPixels(points.get(i), pa);
                view.getProjection().toPixels(points.get(i + 1), pb);
                canvas.drawLine(pa.x, pa.y, pb.x, pb.y, linePaint);
            }
        }
    }

    public int size() {
        return log.getPoints().size();
    }

}
