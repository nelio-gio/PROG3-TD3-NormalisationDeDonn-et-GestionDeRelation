
public class DishOrder {
    private Integer id;
    private Integer idOrder;
    private Integer idDish;
    private Integer quantity;
    private Dish dish;  // pour affichage facile (optionnel)

    public DishOrder() {}

    public DishOrder(Integer idDish, Integer quantity) {
        this.idDish = idDish;
        this.quantity = quantity;
    }

   
    public Integer getId() { 
        return id; 
    }
    public void setId(Integer id) { 
        this.id = id; }
    public Integer getIdOrder() { 
        return idOrder; 
    }
    public void setIdOrder(Integer idOrder) {
        this.idOrder = idOrder; 
    }
    public Integer getIdDish() { 
        return idDish;
    }
    public void setIdDish(Integer idDish) { 
        this.idDish = idDish; 
    }
    public Integer getQuantity() { 
        return quantity; 
    }
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity; 
    }
    public Dish getDish() { 
        return dish; 
    }
    public void setDish(Dish dish) {
        this.dish = dish;
    }

    @Override
    public String toString() {
        return "DishOrder{id=" + id + ", idOrder=" + idOrder +
                ", idDish=" + idDish + ", quantity=" + quantity +
                ", dish=" + (dish != null ? dish.getName() : "null") + '}';
    }
}

