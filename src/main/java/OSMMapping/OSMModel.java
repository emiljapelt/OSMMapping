package OSMMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OSMModel {

    private ArrayList<Drawable> buildings;
    private ArrayList<Drawable> coastlines;
    private ArrayList<Drawable> highways;

    private Map<Type, List<Drawable>> enumMap;

    private ArrayList<Runnable> observers = new ArrayList<>();
    private Bound mapBound;

    public OSMModel(String mapFileLocation){
        System.out.println("***" + mapFileLocation);
        long time = -System.nanoTime();
        OSMReader reader = new OSMReader(mapFileLocation);
        time += System.nanoTime();
        System.out.println("Read time: " + ((time)/1000000) + "ms");

        OSMHandler handler = reader.getHandler();
        coastlines = handler.getCoastlines();
        mapBound = handler.getTempBound();
        enumMap = handler.getEnumMap();
    }

    public ArrayList<Drawable> getBuildings(){return buildings;}
    public ArrayList<Drawable> getCoastlines(){return coastlines;}
    public ArrayList<Drawable> getHighways(){return highways;}
    public Bound getMapBound(){return mapBound;}

    public List<Drawable> getDrawablesOfType(Type type) {
        if (enumMap.containsKey(type)) return enumMap.get(type);
        return new ArrayList<>();
    }

    public void addObserver(Runnable observer){
        observers.add(observer);
    }

    public void notifyObservers(){
        for(Runnable observer : observers){
            observer.run();
        }
    }

}

