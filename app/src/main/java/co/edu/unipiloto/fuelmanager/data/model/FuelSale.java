package co.edu.unipiloto.fuelmanager.data.model;

public class FuelSale {

    private int    id;
    private String fuelType;       // Corriente / Extra / ACPM
    private double volumeGal;      // Galones vendidos
    private double pricePerGal;    // Precio por galón al momento de la venta
    private double totalPrice;     // volumeGal * pricePerGal
    private String clientPlate;    // Placa del vehículo (opcional)
    private String date;           // Fecha de la venta
    private int    stationId;      // FK a la estación

    public FuelSale() {}

    public FuelSale(String fuelType, double volumeGal, double pricePerGal,
                    String clientPlate, String date, int stationId) {
        this.fuelType     = fuelType;
        this.volumeGal    = volumeGal;
        this.pricePerGal  = pricePerGal;
        this.totalPrice   = volumeGal * pricePerGal;
        this.clientPlate  = clientPlate;
        this.date         = date;
        this.stationId    = stationId;
    }

    public int    getId()                      { return id; }
    public void   setId(int id)                { this.id = id; }

    public String getFuelType()                { return fuelType; }
    public void   setFuelType(String t)        { this.fuelType = t; }

    public double getVolumeGal()               { return volumeGal; }
    public void   setVolumeGal(double v)       { this.volumeGal = v; }

    public double getPricePerGal()             { return pricePerGal; }
    public void   setPricePerGal(double p)     { this.pricePerGal = p; }

    public double getTotalPrice()              { return totalPrice; }
    public void   setTotalPrice(double t)      { this.totalPrice = t; }

    public String getClientPlate()             { return clientPlate; }
    public void   setClientPlate(String p)     { this.clientPlate = p; }

    public String getDate()                    { return date; }
    public void   setDate(String d)            { this.date = d; }

    public int    getStationId()               { return stationId; }
    public void   setStationId(int s)          { this.stationId = s; }
}