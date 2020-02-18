package OSMMapping;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public enum Type {

    UNKNOWN,
    BUILDING,
    HIGHWAY,
    COASTLINE,
    WATER;


    public static Paint getColor(Type type) {
        switch (type) {
            case WATER:
                return Color.BLUE;
            case BUILDING:
                return Color.BROWN;
            case HIGHWAY:
                return Color.RED;
            default:
                return Color.BLACK;
        }
    }
}
