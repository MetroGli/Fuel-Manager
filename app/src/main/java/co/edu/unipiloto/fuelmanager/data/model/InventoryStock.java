package co.edu.unipiloto.fuelmanager.data.model;

/** Resumen del stock actual por tipo de combustible para una estación. */
public class InventoryStock {

    private double corrienteGal;
    private double extraGal;
    private double acpmGal;

    public InventoryStock() {}

    public InventoryStock(double corrienteGal, double extraGal, double acpmGal) {
        this.corrienteGal = corrienteGal;
        this.extraGal     = extraGal;
        this.acpmGal      = acpmGal;
    }

    public double getCorrienteGal()              { return corrienteGal; }
    public void   setCorrienteGal(double v)      { this.corrienteGal = v; }

    public double getExtraGal()                  { return extraGal; }
    public void   setExtraGal(double v)          { this.extraGal = v; }

    public double getAcpmGal()                   { return acpmGal; }
    public void   setAcpmGal(double v)           { this.acpmGal = v; }

    /** Retorna el stock del combustible indicado. */
    public double getStock(String fuelType) {
        switch (fuelType) {
            case InventoryMovement.FUEL_EXTRA:     return extraGal;
            case InventoryMovement.FUEL_ACPM:      return acpmGal;
            default:                               return corrienteGal;
        }
    }
}