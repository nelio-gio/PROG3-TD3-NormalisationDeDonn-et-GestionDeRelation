
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever();

        // Log avant changements
        Dish dish = dataRetriever.findDishById(4);
        System.out.println(dish);

        // Log après changements
        // Création d'une liste de DishIngredient (exemple avec 2 ingrédients)
        List<DishIngredient> newLinks = List.of(
                new DishIngredient(dish.getId(), 1, 0.5, UnitEnum.KG),  // Exemple : id du plat actuel + ingrédient 1
                new DishIngredient(dish.getId(), 4, 0.3, UnitEnum.KG)   // Exemple : ingrédient 4
        );
        dish.setDishIngredients(newLinks);  // ← Méthode corrigée (plus setIngredients)

        Dish newDish = dataRetriever.saveDish(dish);
        System.out.println(newDish);

        // Création d'ingrédients (ton code original)
        List<Ingredient> createdIngredients = dataRetriever.createIngredients(
                List.of(new Ingredient(null, "Fromage", CategoryEnum.DAIRY, 1200.0))
        );
        System.out.println(createdIngredients);
    }
}

