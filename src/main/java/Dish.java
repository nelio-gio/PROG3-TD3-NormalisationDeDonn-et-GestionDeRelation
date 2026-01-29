
import java.util.List;
import java.util.Objects;

public class Dish {
    private Integer id;
    private Double sellingPrice;  // ← Remplace l'ancien price (exercice TD3)
    private String name;
    private DishTypeEnum dishType;
    private List<DishIngredient> dishIngredients;  // ← Liste pour ManyToMany

    public Dish() {
    }

    public Dish(Integer id, String name, DishTypeEnum dishType, List<DishIngredient> dishIngredients) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.dishIngredients = dishIngredients;
    }

    // Getter et setter pour sellingPrice
    public Double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    // Calcul du coût des ingrédients
    public Double getDishCost() {
        if (dishIngredients == null || dishIngredients.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (DishIngredient di : dishIngredients) {
            if (di.getIngredient() != null && di.getQuantityRequired() != null) {
                total += di.getIngredient().getPrice() * di.getQuantityRequired();
            }
        }
        return total;
    }

    // Marge brute (lance exception si null)
    public Double getGrossMargin() {
        if (sellingPrice == null) {
            throw new RuntimeException("Selling price is null for dish " + name);
        }
        return sellingPrice - getDishCost();
    }

    // Getters / Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DishTypeEnum getDishType() {
        return dishType;
    }

    public void setDishType(DishTypeEnum dishType) {
        this.dishType = dishType;
    }

    public List<DishIngredient> getDishIngredients() {
        return dishIngredients;
    }

    public void setDishIngredients(List<DishIngredient> dishIngredients) {
        this.dishIngredients = dishIngredients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.id) &&
                Objects.equals(sellingPrice, dish.sellingPrice) &&
                Objects.equals(name, dish.name) &&
                dishType == dish.dishType &&
                Objects.equals(dishIngredients, dish.dishIngredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sellingPrice, name, dishType, dishIngredients);
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", sellingPrice=" + sellingPrice +
                ", name='" + name + '\'' +
                ", dishType=" + dishType +
                ", dishIngredients=" + dishIngredients +
                '}';
    }
}

