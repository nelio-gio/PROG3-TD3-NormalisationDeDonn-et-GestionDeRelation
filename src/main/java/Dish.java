import java.util.List;
import java.util.Objects;

// Le plat est comme un jeu avec une liste de cartes (DishIngredient) pour ses jouets (ingrédients).
public class Dish {
    private Integer id;
    private Double sellingPrice;  // Renommé pour matcher l'exercice, comme le prix de vente de la maison.
    private String name;
    private DishTypeEnum dishType;
    private List<DishIngredient> dishIngredients;  // Nouvelle liste de liens, au lieu des ingrédients directs.

    public Dish() {}

    public Dish(Integer id, String name, DishTypeEnum dishType, List<DishIngredient> dishIngredients) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.dishIngredients = dishIngredients;
    }

    public Double getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(Double sellingPrice) { this.sellingPrice = sellingPrice; }

    public Double getDishCost() {
        if (dishIngredients == null || dishIngredients.isEmpty()) return 0.0;
        double total = 0.0;
        for (DishIngredient di : dishIngredients) {
            if (di.getQuantityRequired() == null) {
                throw new RuntimeException("Quantity is null for ingredient in dish " + name);
            }
            total += di.getIngredient().getPrice() * di.getQuantityRequired();  // Calcule comme compter des bonbons : prix * combien.
        }
        return total;
    }

    public Double getGrossMargin() {
        if (sellingPrice == null) {
            throw new RuntimeException("Selling price is null for dish " + name);
        }
        return sellingPrice - getDishCost();  // Soustraction simple, comme argent gagné moins dépensé.
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public DishTypeEnum getDishType() { return dishType; }
    public void setDishType(DishTypeEnum dishType) { this.dishType = dishType; }
    public List<DishIngredient> getDishIngredients() { return dishIngredients; }
    public void setDishIngredients(List<DishIngredient> dishIngredients) { this.dishIngredients = dishIngredients; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.id) && Objects.equals(sellingPrice, dish.sellingPrice) && Objects.equals(name, dish.name) && dishType == dish.dishType && Objects.equals(dishIngredients, dish.dishIngredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sellingPrice, name, dishType, dishIngredients);
    }

    @Override
    public String toString() {
        return "Dish{id=" + id + ", sellingPrice=" + sellingPrice + ", name='" + name + '\'' + ", dishType=" + dishType + ", dishIngredients=" + dishIngredients + '}';
    }
}