
import java.sql.Timestamp;
import java.util.Objects;

public class StockMovement {
    private Integer id;
    private Integer idIngredient;
    private Double quantity;
    private String unit;
    private String movementType;  // "IN" ou "OUT"
    private Timestamp creationDatetime;

    public StockMovement() {}

    public StockMovement(Integer idIngredient, Double quantity, String unit, String movementType) {
        this.idIngredient = idIngredient;
        this.quantity = quantity;
        this.unit = unit;
        this.movementType = movementType;
    }


    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getIdIngredient() {
        return idIngredient;
    }
    public void setIdIngredient(Integer idIngredient) {
        this.idIngredient = idIngredient;
    }
    public Double getQuantity() {
        return quantity;
    }
    public void setQuantity(Double quantity) {

        this.quantity = quantity;
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public String getMovementType() {
        return movementType;
    }
    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }
    public Timestamp getCreationDatetime() {
        return creationDatetime;
    }
    public void setCreationDatetime(Timestamp creationDatetime) {
        this.creationDatetime = creationDatetime;
    }

    @Override
    public String toString() {
        return "StockMovement{" +
                "id=" + id +
                ", idIngredient=" + idIngredient +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                ", movementType='" + movementType + '\'' +
                ", creationDatetime=" + creationDatetime +
                '}';
    }
}

