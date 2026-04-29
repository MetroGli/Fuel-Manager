package co.edu.unipiloto.fuelmanager.data.model;

/**
 * Recibo generado automáticamente al registrar cada venta.
 * Vinculado a FuelSale por sale_id.
 */
public class Receipt {

    private int    id;
    private long   saleId;
    private String fuelType;
    private double volumeGal;
    private double pricePerGal;
    private double total;
    private String clientPlate;
    private String date;
    private int    stationId;

    public Receipt() {}

    public Receipt(long saleId, String fuelType, double volumeGal,
                   double pricePerGal, double total, String clientPlate,
                   String date, int stationId) {
        this.saleId      = saleId;
        this.fuelType    = fuelType;
        this.volumeGal   = volumeGal;
        this.pricePerGal = pricePerGal;
        this.total       = total;
        this.clientPlate = clientPlate;
        this.date        = date;
        this.stationId   = stationId;
    }

    public int    getId()                       { return id; }
    public void   setId(int id)                 { this.id = id; }

    public long   getSaleId()                   { return saleId; }
    public void   setSaleId(long v)             { this.saleId = v; }

    public String getFuelType()                 { return fuelType; }
    public void   setFuelType(String v)         { this.fuelType = v; }

    public double getVolumeGal()                { return volumeGal; }
    public void   setVolumeGal(double v)        { this.volumeGal = v; }

    public double getPricePerGal()              { return pricePerGal; }
    public void   setPricePerGal(double v)      { this.pricePerGal = v; }

    public double getTotal()                    { return total; }
    public void   setTotal(double v)            { this.total = v; }

    public String getClientPlate()              { return clientPlate; }
    public void   setClientPlate(String v)      { this.clientPlate = v; }

    public String getDate()                     { return date; }
    public void   setDate(String v)             { this.date = v; }

    public int    getStationId()                { return stationId; }
    public void   setStationId(int v)           { this.stationId = v; }
}
