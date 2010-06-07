package org.shokai.gpstracker;

import java.util.*;
import android.graphics.*;
import android.util.Log;
import com.google.android.maps.*;

public class LogOverlay extends Overlay {

    private GpsLog log;
    private int maxLines = 1000;
    
    public LogOverlay(GpsLog log) {
        this.log = log;
    }
    
    public void setMaxLines(int num){
        if (num < 1) return;
        this.maxLines = num;
    }

    @Override
    public void draw(Canvas canvas, MapView view, boolean shadow) {
        List<LogPoint> points = log.getPoints();
        int size = points.size();
        if (size < 2) return;

        GeoPoint center = view.getMapCenter();
        int top = center.getLatitudeE6() - view.getLatitudeSpan()*2; // 実際の画面範囲より大きめに描画する
        int bottom = center.getLatitudeE6() + view.getLatitudeSpan()*2;
        int left = center.getLongitudeE6() - view.getLongitudeSpan()*2;
        int right = center.getLongitudeE6() + view.getLongitudeSpan()*2;

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
        
        Point pa = new Point();
        Point pb = new Point();
        LogPoint la, lb;
        int count = 0;
        int visible_ratio = visibles_num / this.maxLines + 1;
        if(visible_ratio > 1) Log.v("GpsTracker", "visibles : " + visibles_num + ", max : " + this.maxLines);
        for (int i = 0; i < size - 1; i++) {
            if(visibles[i] && visibles[i+1]){
                if(count++ % visible_ratio == 0){ // 線が増えすぎないように間引く
                    la = points.get(i);
                    lb = points.get(i+1);
                    view.getProjection().toPixels(la, pa);
                    view.getProjection().toPixels(lb, pb);
                    canvas.drawLine(pa.x, pa.y, pb.x, pb.y, la.getPaint());
                }
            }
        }
    }

    public int size() {
        return log.getPoints().size();
    }

}
