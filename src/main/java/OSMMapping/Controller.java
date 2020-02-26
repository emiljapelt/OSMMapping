package OSMMapping;

import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
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

        main.getInput().textProperty().addListener(e -> {
            main.getSuggestions().getChildren().clear();
            if(!(main.getInput().getText().equals(""))){
                int suggestionPoint = model.getAddresses().getSuggestions(main.getInput().getText());
                for (int i = suggestionPoint; i < suggestionPoint+6 && i < model.getAddresses().getAddressCount(); i++) {
                    if(model.getAddresses().getAddressByIndex(i).toString().toLowerCase().startsWith(main.getInput().getText().toLowerCase())) {
                        Label label = new Label(model.getAddresses().getAddressByIndex(i).toString());
                        int finalI = i;
                        label.setOnMouseClicked(a -> {
                            main.getInput().setText(model.getAddresses().getAddressByIndex(finalI).toString());
                            main.getSuggestions().getChildren().clear();
                        });
                        main.getSuggestions().getChildren().add(label);
                    }
                }
            }
        });

        main.getInput().setOnAction(e -> {
            String searchedAddress = main.getInput().getText();
            Address found = model.getAddresses().getAddressByName(searchedAddress);
            if(found != null){
                main.zoomToNode(found.getLocation());
                model.setPin(found.getLocation().getLon(), found.getLocation().getLat());
                main.paintMap();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Address search failure");
                alert.setHeaderText("Address: [" + searchedAddress + "] not found.");
                alert.setContentText("Try another address.");
                alert.showAndWait();
            }
        });
    }
}
