package co.edu.unipiloto.fuelmanager.data.model;

public class PriceAlert {

    private int    id;
    private int    stationId;
    private String stationName;
    private String fuelType;       // Corriente / Extra / ACPM
    private double lastKnownPrice; // Precio cuando se activó la alerta
    private boolean active;        // Si la alerta está activa
    private int    userId;         // Usuario que activó la alerta

    public PriceAlert() {}

    public PriceAlert(int stationId, String stationName, String fuelType,
                      double lastKnownPrice, int userId) {
        this.stationId       = stationId;
        this.stationName     = stationName;
        this.fuelType        = fuelType;
        this.lastKnownPrice  = lastKnownPrice;
        this.active          = true;
        this.userId          = userId;
    }

    public int     getId()                       { return id; }
    public void    setId(int id)                 { this.id = id; }

    public int     getStationId()                { return stationId; }
    public void    setStationId(int s)           { this.stationId = s; }

    public String  getStationName()              { return stationName; }
    public void    setStationName(String s)      { this.stationName = s; }

    public String  getFuelType()                 { return fuelType; }
    public void    setFuelType(String f)         { this.fuelType = f; }

    public double  getLastKnownPrice()           { return lastKnownPrice; }
    public void    setLastKnownPrice(double p)   { this.lastKnownPrice = p; }

    public boolean isActive()                    { return active; }
    public void    setActive(boolean a)          { this.active = a; }

    public int     getUserId()                   { return userId; }
    public void    setUserId(int u)              { this.userId = u; }
}