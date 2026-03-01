package co.edu.unipiloto.fuelmanager.data.model;

public class NormativePrice {

    private int id;
    private String fuelType;
    private double pricePerGallon;
    private String effectiveDate;
    private String source;

    // 🔹 Constructor completo
    public NormativePrice(int id,
                          String fuelType,
                          double pricePerGallon,
                          String effectiveDate,
                          String source) {
        this.id = id;
        this.fuelType = fuelType;
        this.pricePerGallon = pricePerGallon;
        this.effectiveDate = effectiveDate;
        this.source = source;
    }

    // 🔹 Constructor vacío (útil para DatabaseHelper)
    public NormativePrice() {
    }

    // ======================
    // GETTERS
    // ======================

    public int getId() {
        return id;
    }

    public String getFuelType() {
        return fuelType;
    }

    public double getPricePerGallon() {
        return pricePerGallon;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public String getSource() {
        return source;
    }

    // ======================
    // SETTERS
    // ======================

    public void setId(int id) {
        this.id = id;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public void setPricePerGallon(double pricePerGallon) {
        this.pricePerGallon = pricePerGallon;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public void setSource(String source) {
        this.source = source;
    }

}