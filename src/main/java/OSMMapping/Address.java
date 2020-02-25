package OSMMapping;

public class Address implements Comparable<Address>{
    private Node location;

    private String city;
    private String postcode;
    private String street;
    private String house;

    public Address(Node location, String[] info){
        this.location = location;
        city = info[0];
        postcode = info[1];
        street = info[2];
        house = info[3];
    }

    @Override
    public String toString(){
        return street + " " + house + ", " + postcode + " " + city;
    }

    public Node getLocation(){
        return location;
    }

    public int compareTo(Address that){
        String comparator = that.toString();
        return compareToString(comparator);
    }

    public int compareToString(String comparator){
        return this.toString().toLowerCase().compareTo(comparator.toLowerCase());
    }
}
