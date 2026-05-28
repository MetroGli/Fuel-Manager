package co.edu.unipiloto.fuelmanager.data.model;

public class Subsidy {

    private int    id;
    private String targetType;
    private String targetValue;
    private String fuelType;
    private double discountPct;
    private String startDate;
    private String endDate;
    private String notes;
    private boolean active;
    private int    authorityId;

    public Subsidy() {}

    public Subsidy(String targetType, String targetValue, String fuelType,
                   double discountPct, String startDate, String endDate,
                   String notes, int authorityId) {
        this.targetType   = targetType;
        this.targetValue  = targetValue;
        this.fuelType     = fuelType;
        this.discountPct  = discountPct;
        this.startDate    = startDate;
        this.endDate      = endDate;
        this.notes        = notes;
        this.authorityId  = authorityId;
        this.active       = true;
    }

    public int     getId()                        { return id; }
    public void    setId(int id)                  { this.id = id; }

    public String  getTargetType()                { return targetType; }
    public void    setTargetType(String v)        { this.targetType = v; }

    public String  getTargetValue()               { return targetValue; }
    public void    setTargetValue(String v)       { this.targetValue = v; }

    public String  getFuelType()                  { return fuelType; }
    public void    setFuelType(String v)          { this.fuelType = v; }

    public double  getDiscountPct()               { return discountPct; }
    public void    setDiscountPct(double v)       { this.discountPct = v; }

    public String  getStartDate()                 { return startDate; }
    public void    setStartDate(String v)         { this.startDate = v; }

    public String  getEndDate()                   { return endDate; }
    public void    setEndDate(String v)           { this.endDate = v; }

    public String  getNotes()                     { return notes; }
    public void    setNotes(String v)             { this.notes = v; }

    public boolean isActive()                     { return active; }
    public void    setActive(boolean v)           { this.active = v; }

    public int     getAuthorityId()               { return authorityId; }
    public void    setAuthorityId(int v)          { this.authorityId = v; }
}
