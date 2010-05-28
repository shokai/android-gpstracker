package org.shokai.gpstracker;

import java.util.*;
import android.graphics.*;
import android.graphics.Paint.*;
import android.graphics.drawable.*;
import com.google.android.maps.*;

public class LogOverlay extends Overlay {

	private Projection projection; private Paint linePaint;
	private List<GeoPoint> points;
	
	public LogOverlay(List<GeoPoint> points) {
		this.points = points;
		
		this.linePaint = new Paint();
	    linePaint.setARGB(255, 255, 0, 0);
	    linePaint.setStrokeWidth(3);
	    linePaint.setDither(true);
	    linePaint.setStyle(Style.FILL);
	    linePaint.setAntiAlias(true);
	    linePaint.setStrokeJoin(Paint.Join.ROUND);
	    linePaint.setStrokeCap(Paint.Cap.ROUND);
	}
	
	@Override
	public void draw(Canvas canvas, MapView view, boolean shadow){
	    int size = points.size();
	    Point lastPoint = new Point();
	    if(size == 0) return;
	    view.getProjection().toPixels(points.get(0), lastPoint);
	    Point point = new Point();
	    for(int i = 1; i<size; i++){
	       view.getProjection().toPixels(points.get(i), point);
     	   canvas.drawLine(lastPoint.x, lastPoint.y, point.x, point.y, linePaint);
	       lastPoint = point;
	    }
	}

}
