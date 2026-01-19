import java.util.List;

// Point de départ pour tester, comme allumer la lumière pour voir si la maison marche.
public class Main {
    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();

        // Test find et cost/margin.
        Dish salade = dr.findDishById(1);
        System.out.println("Salade fraîche: " + salade);
        System.out.println("Coût: " + salade.getDishCost());  // Devrait être 250.00
        try {
            System.out.println("Marge: " + salade.getGrossMargin());  // 3250.00
        } catch (RuntimeException e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        Dish riz = dr.findDishById(3);
        System.out.println("Riz: " + riz);
        System.out.println("Coût: " + riz.getDishCost());  // 0.00
        try {
            System.out.println("Marge: " + riz.getGrossMargin());  // Exception
        } catch (RuntimeException e) {
            System.out.println("Erreur attendue: " + e.getMessage());
        }

        // Test createIngredients (exemple).
        List<Ingredient> created = dr.createIngredients(List.of(new Ingredient(null, "Fromage", CategoryEnum.DAIRY, 1200.0)));
        System.out.println("Ingrédients créés: " + created);
    }
}