package co.edu.unipiloto.fuelmanager.data.model;

public class Delivery {

    private int    id;
    private int    stationId;
    private String stationName;
    private String fuelType;       // Corriente / Extra / ACPM
    private double volumeGal;      // Galones entregados
    private String date;           // Fecha de entrega
    private String notes;          // Observaciones opcionales
    private int    distributorId;  // userId del distribuidor

    public Delivery() {}

    public Delivery(int stationId, String stationName, String fuelType,
                    double volumeGal, String date, String notes, int distributorId) {
        this.stationId     = stationId;
        this.stationName   = stationName;
        this.fuelType      = fuelType;
        this.volumeGal     = volumeGal;
        this.date          = date;
        this.notes         = notes;
        this.distributorId = distributorId;
    }

    public int    getId()                        { return id; }
    public void   setId(int id)                  { this.id = id; }

    public int    getStationId()                 { return stationId; }
    public void   setStationId(int s)            { this.stationId = s; }

    public String getStationName()               { return stationName; }
    public void   setStationName(String s)       { this.stationName = s; }

    public String getFuelType()                  { return fuelType; }
    public void   setFuelType(String f)          { this.fuelType = f; }

    public double getVolumeGal()                 { return volumeGal; }
    public void   setVolumeGal(double v)         { this.volumeGal = v; }

    public String getDate()                      { return date; }
    public void   setDate(String d)              { this.date = d; }

    public String getNotes()                     { return notes; }
    public void   setNotes(String n)             { this.notes = n; }

    public int    getDistributorId()             { return distributorId; }
    public void   setDistributorId(int d)        { this.distributorId = d; }
}