package dev.wearkit.core.rendering.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import org.dyn4j.geometry.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dev.wearkit.core.common.Paintable;
import dev.wearkit.core.common.Renderable;
import dev.wearkit.core.common.Scalable;

public class Polygon extends org.dyn4j.geometry.Polygon implements Paintable, Renderable, Scalable {

    private Paint paint;

    public Polygon(Vector2... vertices) {
        super(vertices);
    }

    @Override
    public void render(Canvas canvas) {
        Iterator<Vector2> iterator = this.getVertexIterator();
        if (!iterator.hasNext()) throw new NullPointerException("vertices cannot be empty");
        Path path = new Path();
        Vector2 firstPoint = iterator.next();
        path.moveTo((float) firstPoint.x, (float) firstPoint.y);
        while (iterator.hasNext()){
            Vector2 point = iterator.next();
            path.lineTo((float) point.x, (float) point.y);
        }
        path.close();
        canvas.drawPath(path, this.paint);
    }

    @Override
    public void paint(Paint paint) {
        if(paint == null) throw new NullPointerException("paint cannot be null");
        this.paint = paint;
    }

    @Override
    public Scalable scale(double rate) {
        Vector2[] vertexes = this.getVertices().clone();
        for(Vector2 vertex: vertexes){
            vertex.multiply(rate);
        }
        return new Polygon(vertexes);
    }
}
