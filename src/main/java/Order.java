
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Order {
    private Integer id;
    private String reference;
    private Timestamp creationDatetime;
    private Double totalHT;
    private Double totalTTC;
    private List<DishOrder> dishOrders = new ArrayList<>();

    public Order() {}

    public Order(String reference, Double totalHT, Double totalTTC) {
        this.reference = reference;
        this.totalHT = totalHT;
        this.totalTTC = totalTTC;
    }


    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getReference() {
        return reference;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }
    public Timestamp getCreationDatetime() {
        return creationDatetime;
    }
    public void setCreationDatetime(Timestamp creationDatetime) {
        this.creationDatetime = creationDatetime;
    }
    public Double getTotalHT() {
        return totalHT;
    }
    public void setTotalHT(Double totalHT) {
        this.totalHT = totalHT;
    }
    public Double getTotalTTC() {
        return totalTTC;
    }
    public void setTotalTTC(Double totalTTC) {
        this.totalTTC = totalTTC;
    }
    public List<DishOrder> getDishOrders() {
        return dishOrders;
    }
    public void setDishOrders(List<DishOrder> dishOrders) {
        this.dishOrders = dishOrders;
    }

    public void addDishOrder(DishOrder dishOrder) {
        dishOrders.add(dishOrder);
    }

    @Override
    public String toString() {
        return "Order{id=" + id + ", reference='" + reference + '\'' +
                ", creationDatetime=" + creationDatetime +
                ", totalHT=" + totalHT + ", totalTTC=" + totalTTC +
                ", dishOrders=" + dishOrders + '}';
    }
}


