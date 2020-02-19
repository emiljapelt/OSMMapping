package OSMMapping;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;

public class LinePath implements Drawable{

    float[] coordinates;
    Type type;

    public LinePath(Way way, Type type){
        ArrayList<Node> nodes = way.getNodes();
        coordinates = new float[nodes.size() * 2];
        for(int i = 0; i < nodes.size(); i++){
            coordinates[i*2] = nodes.get(i).getLon();
            coordinates[i*2+1] = nodes.get(i).getLat();
        }
        this.type = type;
    }

    public Type getType(){ return type; }

    @Override
    public void draw(GraphicsContext gc, double scale) {
        gc.beginPath();
        trace(gc, scale);
        gc.stroke();
    }

    public void trace(GraphicsContext gc, double scale){
        gc.moveTo(coordinates[0], coordinates[1]);
        for (int i = 2 ; i < coordinates.length ; i += 2) {
            //TODO review size check
            if( 1 < scale * Math.sqrt(Math.pow((coordinates[i] - coordinates[i-2]),2) + Math.pow((coordinates[i+1] - coordinates[i-1]),2))) {
                gc.lineTo(coordinates[i], coordinates[i + 1]);
            }

        }
    }
}
