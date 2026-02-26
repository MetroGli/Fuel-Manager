package co.edu.unipiloto.fuelmanager.data.model;

public class Station {

    private int id;
    private String name;
    private String address;
    private String zone;
    private double priceCorriente;
    private double priceExtra;
    private double priceAcpm;
    private double distanceKm;

    public Station() {}

    public Station(int id, String name, String address, String zone,
                   double priceCorriente, double priceExtra, double priceAcpm) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.zone = zone;
        this.priceCorriente = priceCorriente;
        this.priceExtra = priceExtra;
        this.priceAcpm = priceAcpm;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public double getPriceCorriente() { return priceCorriente; }
    public void setPriceCorriente(double priceCorriente) { this.priceCorriente = priceCorriente; }

    public double getPriceExtra() { return priceExtra; }
    public void setPriceExtra(double priceExtra) { this.priceExtra = priceExtra; }

    public double getPriceAcpm() { return priceAcpm; }
    public void setPriceAcpm(double priceAcpm) { this.priceAcpm = priceAcpm; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
}