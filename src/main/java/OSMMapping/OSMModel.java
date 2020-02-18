package OSMMapping;

import java.util.ArrayList;

public class OSMModel {

    private ArrayList<Drawable> buildings;
    private ArrayList<Drawable> coastlines;
    private ArrayList<Drawable> highways;

    private ArrayList<Runnable> observers = new ArrayList<>();
    private Bound mapBound;

    public OSMModel(String mapFileLocation){
        System.out.println("***" + mapFileLocation);
        long time = -System.nanoTime();
        OSMReader reader = new OSMReader(mapFileLocation);
        time += System.nanoTime();
        System.out.println("Read time: " + ((time)/1000000) + "ms");

        OSMHandler handler = reader.getHandler();
        buildings = handler.getBuildings();
        coastlines = handler.getCoastlines();
        highways = handler.getHighways();
        mapBound = handler.getTempBound();
    }

    public ArrayList<Drawable> getBuildings(){return buildings;}
    public ArrayList<Drawable> getCoastlines(){return coastlines;}
    public ArrayList<Drawable> getHighways(){return highways;}
    public Bound getMapBound(){return mapBound;}

    public void addObserver(Runnable observer){
        observers.add(observer);
    }

    public void notifyObservers(){
        for(Runnable observer : observers){
            observer.run();
        }
    }

}

