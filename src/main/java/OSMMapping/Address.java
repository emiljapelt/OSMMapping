package OSMMapping;

public class Address {
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

    public String toString(){
        return street + " " + house + ", " + postcode + " " + city;
    }

    public Node getLocation(){
        return location;
    }
}
