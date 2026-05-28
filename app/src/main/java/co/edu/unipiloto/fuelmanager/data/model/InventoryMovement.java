package co.edu.unipiloto.fuelmanager.data.model;

public class InventoryMovement {

    public static final String TYPE_ENTRADA = "ENTRADA";
    public static final String TYPE_SALIDA  = "SALIDA";

    public static final String FUEL_CORRIENTE = "Corriente";
    public static final String FUEL_EXTRA     = "Extra";
    public static final String FUEL_ACPM      = "ACPM";

    private int    id;
    private String fuelType;
    private String movType;
    private double volumeGal;
    private String note;
    private String date;
    private int    stationId;

    public InventoryMovement() {}

    public InventoryMovement(String fuelType, String movType,
                             double volumeGal, String note,
                             String date, int stationId) {
        this.fuelType  = fuelType;
        this.movType   = movType;
        this.volumeGal = volumeGal;
        this.note      = note;
        this.date      = date;
        this.stationId = stationId;
    }

    public int    getId()        { return id; }
    public void   setId(int id)  { this.id = id; }

    public String getFuelType()              { return fuelType; }
    public void   setFuelType(String t)      { this.fuelType = t; }

    public String getMovType()               { return movType; }
    public void   setMovType(String t)       { this.movType = t; }

    public double getVolumeGal()             { return volumeGal; }
    public void   setVolumeGal(double v)     { this.volumeGal = v; }

    public String getNote()                  { return note; }
    public void   setNote(String n)          { this.note = n; }

    public String getDate()                  { return date; }
    public void   setDate(String d)          { this.date = d; }

    public int    getStationId()             { return stationId; }
    public void   setStationId(int s)        { this.stationId = s; }
}