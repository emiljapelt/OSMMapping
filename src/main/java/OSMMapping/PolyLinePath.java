package OSMMapping;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public class PolyLinePath implements Drawable{
    private ArrayList<LinePath> ways;

    public PolyLinePath(Relation relation){
        ways = new ArrayList<>();

        for(Way way : relation.getWays()){
            ways.add(new LinePath(way));
        }
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.beginPath();
        for(LinePath linePath : ways){
            linePath.trace(gc);
        }
        gc.stroke();
    }
}
