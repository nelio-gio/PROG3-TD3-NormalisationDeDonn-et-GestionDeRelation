
public class DishIngredient {
    private Integer id;                // ID de la relation (optionnel, SERIAL dans la base)
    private Integer idDish;            // ID du plat
    private Integer idIngredient;      // ID de l'ingrédient
    private Double quantityRequired;   // Quantité requise (ex: 0.20)
    private UnitEnum unit;             // Unité (KG, L, PCS, etc.)
    private Ingredient ingredient;     // L'ingrédient lié (pour calcul du coût)

    // Constructeur avec tous les paramètres (corrige l'erreur de "constructor cannot be applied")
    public DishIngredient(Integer idDish, Integer idIngredient, Double quantityRequired, UnitEnum unit) {
        this.idDish = idDish;
        this.idIngredient = idIngredient;
        this.quantityRequired = quantityRequired;
        this.unit = unit;
    }

    // Constructeur vide (utile pour les frameworks ou quand on construit l'objet étape par étape)
    public DishIngredient() {
    }

    // Getters et setters (tous les manquants sont ajoutés ici)
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
        this.quantityRequired = quantityRequired;
    }

    public UnitEnum getUnit() {
        return unit;
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
        return "DishIngredient{" +
                "id=" + id +
                ", idDish=" + idDish +
                ", idIngredient=" + idIngredient +
                ", quantityRequired=" + quantityRequired +
                ", unit=" + unit +
                ", ingredient=" + ingredient +
                '}';
    }
}


