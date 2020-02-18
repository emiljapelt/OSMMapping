package OSMMapping;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.transform.Affine;

import java.util.ArrayList;

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
        ArrayList<Drawable> buildings = model.getBuildings();
        ArrayList<Drawable> coastlines = model.getCoastlines();
        ArrayList<Drawable> highways = model.getHighways();

        gc.setTransform(new Affine());
        gc.setLineWidth(lineWidth);
        gc.setFill(Color.AQUA);
        gc.fillRect(0, 0, main.getMapCanvas().getWidth(), main.getMapCanvas().getHeight());
        gc.setTransform(transform);

        model.getMapBound().draw(gc);
        for(Drawable coastline : coastlines){
            coastline.draw(gc);
        }
        for(Drawable highway : highways){
            highway.draw(gc);
        }
        gc.setFillRule(FillRule.EVEN_ODD);
        for(Drawable building : buildings){
            building.draw(gc);
        }
    }
}

//TODO Make MapPainter only paint elements that are within the viewBound.