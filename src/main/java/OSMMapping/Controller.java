package OSMMapping;

import javafx.geometry.Point2D;
import javafx.scene.control.Label;

public class Controller {
    private Main main;
    private OSMModel model;
    Point2D lastMouse;

    public Controller(Main main, OSMModel model){
        this.main = main;
        this.model = model;

        main.getMapCanvas().setOnMousePressed(e -> {
            lastMouse = new Point2D(e.getX(), e.getY());
        });

        main.getMapCanvas().setOnMouseDragged(e -> {
            main.move(e.getX() - lastMouse.getX(), e.getY() - lastMouse.getY());
            main.paintMap();
            lastMouse = new Point2D(e.getX(), e.getY());
        });

        main.getMapCanvas().setOnScroll(e -> {
            double factor = Math.pow(1.001, e.getDeltaY());
            main.zoom(factor, e.getX(), e.getY());
            main.paintMap();
        });

        main.getInput().setOnKeyReleased(e -> {
            main.getSuggestions().getChildren().clear();
            if(main.getInput().getText() == "") main.getSuggestions().getChildren().clear();
            else {
                for (Address address : model.getAddresses()) {
                    String addressText = address.toString();
                    if(addressText.toLowerCase().startsWith(main.getInput().getText().toLowerCase())){
                        Label label = new Label(addressText);
                        label.setOnMouseClicked(a -> {
                            main.getInput().setText(addressText);
                            main.getSuggestions().getChildren().clear();
                        });
                        main.getSuggestions().getChildren().add(label);
                    }
                }
            }
        });
    }
}
