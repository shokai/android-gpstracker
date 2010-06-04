package org.shokai.gpstracker;

import java.util.*;
import android.graphics.*;
import android.graphics.Paint.*;
import android.util.Log;
import com.google.android.maps.*;

public class LogOverlay extends Overlay {

    private Paint linePaint;
    private GpsLog log;
    
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

    @Override
    public void draw(Canvas canvas, MapView view, boolean shadow) {
        List<GeoPoint> points = log.getPoints();
        if (points.size() < 2) return;
        Point pa = new Point();
        Point pb = new Point();

        GeoPoint center = view.getMapCenter();
        int top = center.getLatitudeE6() - view.getLatitudeSpan()/2;
        int bottom = center.getLatitudeE6() + view.getLatitudeSpan()/2;
        int left = center.getLongitudeE6() - view.getLongitudeSpan()/2;
        int right = center.getLongitudeE6() + view.getLongitudeSpan()/2;

        GeoPoint ga, gb;
        for (int i = 0; i < points.size() - 1; i++) {
            ga = points.get(i);
            gb = points.get(i+1);
            if( !(ga.getLatitudeE6() < top && gb.getLatitudeE6() < top ||
                  ga.getLatitudeE6() > bottom && gb.getLatitudeE6() > bottom || 
                  ga.getLongitudeE6() < left && gb.getLongitudeE6() < left ||
                  ga.getLongitudeE6() > right && gb.getLongitudeE6() > right) ){ // 画面外は描画しない
            
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
