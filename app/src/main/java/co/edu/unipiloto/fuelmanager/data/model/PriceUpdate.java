package co.edu.unipiloto.fuelmanager.data.model;

/** Registro histórico de una actualización de precios hecha por un distribuidor. */
public class PriceUpdate {

    private int    id;
    private int    stationId;
    private String stationName;
    private double oldCorriente;
    private double newCorriente;
    private double oldExtra;
    private double newExtra;
    private double oldAcpm;
    private double newAcpm;
    private String date;
    private int    distributorId;

    public PriceUpdate() {}

    public PriceUpdate(int stationId, String stationName,
                       double oldCorriente, double newCorriente,
                       double oldExtra, double newExtra,
                       double oldAcpm, double newAcpm,
                       String date, int distributorId) {
        this.stationId     = stationId;
        this.stationName   = stationName;
        this.oldCorriente  = oldCorriente;
        this.newCorriente  = newCorriente;
        this.oldExtra      = oldExtra;
        this.newExtra      = newExtra;
        this.oldAcpm       = oldAcpm;
        this.newAcpm       = newAcpm;
        this.date          = date;
        this.distributorId = distributorId;
    }

    public int    getId()              { return id; }
    public void   setId(int id)        { this.id = id; }

    public int    getStationId()                   { return stationId; }
    public void   setStationId(int v)              { this.stationId = v; }

    public String getStationName()                 { return stationName; }
    public void   setStationName(String v)         { this.stationName = v; }

    public double getOldCorriente()                { return oldCorriente; }
    public void   setOldCorriente(double v)        { this.oldCorriente = v; }

    public double getNewCorriente()                { return newCorriente; }
    public void   setNewCorriente(double v)        { this.newCorriente = v; }

    public double getOldExtra()                    { return oldExtra; }
    public void   setOldExtra(double v)            { this.oldExtra = v; }

    public double getNewExtra()                    { return newExtra; }
    public void   setNewExtra(double v)            { this.newExtra = v; }

    public double getOldAcpm()                     { return oldAcpm; }
    public void   setOldAcpm(double v)             { this.oldAcpm = v; }

    public double getNewAcpm()                     { return newAcpm; }
    public void   setNewAcpm(double v)             { this.newAcpm = v; }

    public String getDate()                        { return date; }
    public void   setDate(String v)                { this.date = v; }

    public int    getDistributorId()               { return distributorId; }
    public void   setDistributorId(int v)          { this.distributorId = v; }
}