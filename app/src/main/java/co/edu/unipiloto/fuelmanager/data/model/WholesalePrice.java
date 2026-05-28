package co.edu.unipiloto.fuelmanager.data.model;

public class WholesalePrice {

    private int    id;
    private int    stationId;
    private String stationName;
    private String fuelType;
    private double pricePerGallon;
    private String effectiveDate;
    private int    distributorId;

    public WholesalePrice() {}

    public WholesalePrice(int stationId, String stationName, String fuelType,
                          double pricePerGallon, String effectiveDate, int distributorId) {
        this.stationId      = stationId;
        this.stationName    = stationName;
        this.fuelType       = fuelType;
        this.pricePerGallon = pricePerGallon;
        this.effectiveDate  = effectiveDate;
        this.distributorId  = distributorId;
    }

    public int    getId()                         { return id; }
    public void   setId(int id)                   { this.id = id; }

    public int    getStationId()                  { return stationId; }
    public void   setStationId(int v)             { this.stationId = v; }

    public String getStationName()                { return stationName; }
    public void   setStationName(String v)        { this.stationName = v; }

    public String getFuelType()                   { return fuelType; }
    public void   setFuelType(String v)           { this.fuelType = v; }

    public double getPricePerGallon()             { return pricePerGallon; }
    public void   setPricePerGallon(double v)     { this.pricePerGallon = v; }

    public String getEffectiveDate()              { return effectiveDate; }
    public void   setEffectiveDate(String v)      { this.effectiveDate = v; }

    public int    getDistributorId()              { return distributorId; }
    public void   setDistributorId(int v)         { this.distributorId = v; }
}
