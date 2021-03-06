package OSMMapping;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ScaleBar implements Drawable {
    //0.0089993 is an estimate for the length of 1km in model coordinates.
    private final double kilometerLength = 0.0089993;
    private double placementX;
    private double placementY;
    private double barLength;
    private String barShowing;

    public ScaleBar(){}

    public void updateScaleBar(Main main) {
        Point2D placement = main.getModelCoordinates(main.getMapCanvas().getWidth() - 50, main.getMapCanvas().getHeight() - 50);
        placementX = placement.getX();
        placementY = placement.getY();
        System.out.println(main.getMapCanvas().getGraphicsContext2D().getTransform().getMxx());
        double scale = main.getMapCanvas().getGraphicsContext2D().getTransform().getMxx();
        if (scale < 1500) {
            barLength = kilometerLength * 10;
            barShowing = "10km";
        } else if (scale < 3000) {
            barLength = kilometerLength * 5;
            barShowing = "5km";
        } else if(scale < 6000){
            barLength = kilometerLength * 2;
            barShowing = "2km";
        } else if(scale < 12000){
            barLength = kilometerLength;
            barShowing = "1km";
        } else if(scale < 24000){
            barLength = kilometerLength/2;
            barShowing = "500m";
        } else if(scale < 48000){
            barLength = kilometerLength/4;
            barShowing = "250m";
        } else if(scale < 96000){
            barLength = kilometerLength/10;
            barShowing = "100m";
        } else if(scale < 192000){
            barLength = kilometerLength/20;
            barShowing = "50m";
        }
    }


    @Override
    public void draw(GraphicsContext gc, double scale) {
        gc.beginPath();
        gc.moveTo(placementX, placementY - (5*scale));
        gc.lineTo(placementX, placementY);
        gc.lineTo(placementX-barLength, placementY);
        gc.lineTo(placementX-barLength, placementY - (5*scale));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2*scale);
        gc.stroke();
        gc.setFont(new Font(Font.getDefault().toString(), 12 * scale));
        gc.strokeText(barShowing, placementX - 10*scale, placementY + 13*scale);
    }

    @Override
    public Type getType() {
        return Type.UNKNOWN;
    }
}
