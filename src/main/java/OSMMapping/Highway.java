package OSMMapping;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Highway implements Drawable{
    Drawable shape;

    public Highway(Way way){
        shape = new LinePath(way);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.DARKGREY);
        shape.draw(gc);
    }
}