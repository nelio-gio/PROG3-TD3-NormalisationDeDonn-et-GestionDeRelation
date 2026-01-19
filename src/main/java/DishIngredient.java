
public class DishIngredient {
    private Integer id;
    private Integer idDish;
    private Integer idIngredient;
    private Double quantityRequired;
    private UnitEnum unit;
    private Ingredient ingredient;

    public DishIngredient() {}

    public DishIngredient(Integer idDish, Integer idIngredient, Double quantityRequired, UnitEnum unit) {
        this.idDish = idDish;
        this.idIngredient = idIngredient;
        this.quantityRequired = quantityRequired;
        this.unit = unit;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getIdDish() {
        return idDish;
    }
    public void setIdDish(Integer idDish) {
        this.idDish = idDish;
    }
    public Integer getIdIngredient() {
        return idIngredient;
    }
    public void setIdIngredient(Integer idIngredient) {
        this.idIngredient = idIngredient;
    }
    public Double getQuantityRequired() {
        return quantityRequired;
    }
    public void setQuantityRequired(Double quantityRequired) {
        this.quantityRequired = quantityRequired; }
    public UnitEnum getUnit() { return unit;
    }
    public void setUnit(UnitEnum unit) {
        this.unit = unit;
    }
    public Ingredient getIngredient() {
        return ingredient;
    }
    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public String toString() {
        return "DishIngredient{id=" + id + ", idDish=" + idDish + ", idIngredient=" + idIngredient + ", quantityRequired=" + quantityRequired + ", unit=" + unit + ", ingredient=" + ingredient + '}';
    }
}
