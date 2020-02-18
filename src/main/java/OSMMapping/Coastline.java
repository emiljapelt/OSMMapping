package OSMMapping;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Coastline implements Drawable{
    Drawable shape;

    public Coastline(Way way){
        shape = new LinePath(way);
    }
    public Coastline(Relation relation){
        shape = new PolyLinePath(relation);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.WHITESMOKE);
        shape.draw(gc);
        gc.fill();
    }
}
