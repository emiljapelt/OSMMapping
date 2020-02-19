package OSMMapping;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.transform.Affine;

import java.util.ArrayList;
import java.util.List;

public class MapPainter {

    private Main main;
    private OSMModel model;
    private GraphicsContext gc;
    private Affine transform;
    private float lineWidth;

    public MapPainter(Main main, OSMModel model, GraphicsContext gc, Affine transform){
        this.main = main;
        this.model = model;
        this.gc = gc;
        this.transform = transform;
    }

    public void paintMap(float lineWidth){

        this.lineWidth = lineWidth;
        ArrayList<Drawable> coastlines = model.getCoastlines();

        gc.setTransform(new Affine());
        gc.setLineWidth(lineWidth);
        gc.setFill(Color.AQUA);
        gc.fillRect(0, 0, main.getMapCanvas().getWidth(), main.getMapCanvas().getHeight());
        gc.setTransform(transform);

        model.getMapBound().draw(gc);
        gc.setFill(Type.getColor(Type.COASTLINE));
        for(Drawable coastline : coastlines){
            coastline.draw(gc);
            gc.fill();
        }

        paintDrawables(model.getDrawablesOfType(Type.BEACH), true, lineWidth);
        paintDrawables(model.getDrawablesOfType(Type.FOREST), true, lineWidth);
        paintDrawables(model.getDrawablesOfType(Type.FARMFIELD), true, lineWidth);

        paintDrawables(model.getDrawablesOfType(Type.WATERWAY), false, lineWidth);
        paintDrawables(model.getDrawablesOfType(Type.WATER), true, lineWidth);

        paintDrawables(model.getDrawablesOfType(Type.HIGHWAY), false, lineWidth);

        paintDrawables(model.getDrawablesOfType(Type.BUILDING), true, lineWidth);
    }

    public void paintDrawables(List<Drawable> drawables, boolean fill, double linewidth){
        if (drawables.size() != 0) {
            Type type = drawables.get(0).getType();
            gc.setStroke(Type.getColor(type));
            if (fill) gc.setFill(Type.getColor(type));
            for (Drawable drawable : drawables) {
                drawable.draw(gc);
                if (fill) gc.fill();
            }
        }
    }
}

//TODO Make MapPainter only paint elements that are within the viewBound.
//TODO fix linewidth consistensy