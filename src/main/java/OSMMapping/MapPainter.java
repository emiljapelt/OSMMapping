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

    public MapPainter(Main main, OSMModel model, GraphicsContext gc, Affine transform){
        this.main = main;
        this.model = model;
        this.gc = gc;
        this.transform = transform;
    }

    public void paintMap(){

        ArrayList<Drawable> coastlines = model.getCoastlines();

        gc.setTransform(new Affine());
        gc.setFill(Color.AQUA);
        gc.fillRect(0, 0, main.getMapCanvas().getWidth(), main.getMapCanvas().getHeight());
        gc.setTransform(transform);

        model.getMapBound().draw(gc, transform.getMxx());
        gc.setFill(Type.getColor(Type.COASTLINE));
        for(Drawable coastline : coastlines){
            coastline.draw(gc, transform.getMxx());
            gc.fill();
        }

        double pixelWidth = 1/Math.sqrt(Math.abs(transform.determinant()));
        gc.setLineWidth(pixelWidth);
        gc.setFillRule(FillRule.EVEN_ODD);

        paintDrawables(model.getDrawablesOfType(Type.BEACH), true, pixelWidth);
        paintDrawables(model.getDrawablesOfType(Type.FOREST), true, pixelWidth);
        paintDrawables(model.getDrawablesOfType(Type.FARMFIELD), true, pixelWidth);

        paintDrawables(model.getDrawablesOfType(Type.WATERWAY), false, pixelWidth);
        paintDrawables(model.getDrawablesOfType(Type.WATER), true, pixelWidth);

        paintDrawables(model.getDrawablesOfType(Type.HIGHWAY), false, 2* pixelWidth);

        paintDrawables(model.getDrawablesOfType(Type.BUILDING), true, pixelWidth);
    }

    private void paintDrawables(List<Drawable> drawables, boolean fill, double lineWidth){
        gc.setLineWidth(lineWidth);
        if (drawables.size() != 0) {
            Type type = drawables.get(0).getType();
            gc.setStroke(Type.getColor(type));
            if (fill) gc.setFill(Type.getColor(type));
            for (Drawable drawable : drawables) {
                drawable.draw(gc, transform.getMxx());
                if (fill) gc.fill();
            }
        }
    }
}

//TODO Make MapPainter only paint elements that are within the viewBound.