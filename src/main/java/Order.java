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
    private OrderTypeEnum orderType;          // EAT_IN ou TAKE_AWAY
    private OrderStatusEnum orderStatus;      // CREATED, READY, DELIVERED
    private String customerName;              // Optionnel
    private List<DishOrder> dishOrders = new ArrayList<>();

    public Order() {
        this.orderStatus = OrderStatusEnum.CREATED; // Par défaut : créée
    }


    public Order(String reference, Double totalHT, Double totalTTC, OrderTypeEnum orderType) {
        this.reference = reference;
        this.totalHT = totalHT;
        this.totalTTC = totalTTC;
        this.orderType = orderType;
        this.orderStatus = OrderStatusEnum.CREATED;
    }


    public OrderTypeEnum getOrderType() { return orderType; }
    public void setOrderType(OrderTypeEnum orderType) { this.orderType = orderType; }

    public OrderStatusEnum getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatusEnum orderStatus) { this.orderStatus = orderStatus; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public Timestamp getCreationDatetime() { return creationDatetime; }
    public void setCreationDatetime(Timestamp creationDatetime) { this.creationDatetime = creationDatetime; }
    public Double getTotalHT() { return totalHT; }
    public void setTotalHT(Double totalHT) { this.totalHT = totalHT; }
    public Double getTotalTTC() { return totalTTC; }
    public void setTotalTTC(Double totalTTC) { this.totalTTC = totalTTC; }
    public List<DishOrder> getDishOrders() { return dishOrders; }
    public void setDishOrders(List<DishOrder> dishOrders) { this.dishOrders = dishOrders; }

    public void addDishOrder(DishOrder dishOrder) {
        dishOrders.add(dishOrder);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", creationDatetime=" + creationDatetime +
                ", totalHT=" + totalHT +
                ", totalTTC=" + totalTTC +
                ", orderType=" + orderType +
                ", orderStatus=" + orderStatus +
                ", customerName='" + customerName + '\'' +
                ", dishOrders=" + dishOrders +
                '}';
    }
}