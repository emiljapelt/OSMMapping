package OSMMapping;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ScaleBar implements Drawable {
    private double length;
    private float placementX;
    private float placementY;

    //0.0089993 is an estimate for the length of 1km in model coordinates.
    public ScaleBar(){
        length = 0.0089993;
    }

    public void updateScaleBar(Main main){
        Point2D placement = main.getModelCoordinates(main.getMapCanvas().getWidth() -50,main.getMapCanvas().getHeight() -50);
        placementX = (float) placement.getX();
        placementY = (float) placement.getY();
    }


    @Override
    public void draw(GraphicsContext gc, double scale) {
        gc.beginPath();
        gc.moveTo(placementX, placementY - (5*scale));
        gc.lineTo(placementX, placementY);
        gc.lineTo(placementX-length, placementY);
        gc.lineTo(placementX-length, placementY - (5*scale));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2*scale);
        gc.stroke();
    }

    @Override
    public Type getType() {
        return Type.UNKNOWN;
    }
}
