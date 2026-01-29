
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();

        // =============================================================================
        // PARTIE 1 : TESTS TD3 (Normalisation ManyToMany, coût, marge, ingrédients)
        // =============================================================================
        System.out.println("=== PARTIE 1 : TESTS TD3 ===");

        // Test TD3.1 : Chargement d'un plat avec ses ingrédients (ManyToMany)
        System.out.println("Test TD3.1 : Chargement plat id=4 (Gâteau au chocolat)");
        Dish plat = dr.findDishById(4);
        if (plat != null) {
            System.out.println("Plat chargé : " + plat);
            System.out.println("Coût calculé des ingrédients : " + plat.getDishCost());
            try {
                System.out.println("Marge brute : " + plat.getGrossMargin());
            } catch (RuntimeException e) {
                System.out.println("Erreur marge (attendue si sellingPrice null) : " + e.getMessage());
            }
        } else {
            System.out.println("Plat non trouvé (id=4)");
        }

        // Test TD3.2 : Création d'un nouvel ingrédient
        System.out.println("\nTest TD3.2 : Création d'un nouvel ingrédient");
        List<Ingredient> nouveaux = dr.createIngredients(
                List.of(
                        new Ingredient(null, "Farine", CategoryEnum.OTHER, 400.0)
                )
        );
        System.out.println("Ingrédients créés : " + nouveaux);

        System.out.println("\n=== FIN DES TESTS TD3 ===\n");

        // =============================================================================
        // PARTIE 2 : TESTS TD4 (Gestion des stocks - mouvements IN/OUT)
        // =============================================================================
        System.out.println("=== PARTIE 2 : TESTS TD4 ===");

        if (!nouveaux.isEmpty()) {
            Integer idFarine = nouveaux.get(0).getId();

            // Test TD4.1 : Mouvement d'achat (IN)
            System.out.println("Test TD4.1 : Achat 10 KG de Farine");
            StockMovement achat = new StockMovement(idFarine, 10.0, "KG", "IN");
            dr.addStockMovement(achat);
            System.out.println("Mouvement ajouté : " + achat);

            Ingredient farineApresAchat = dr.findIngredientById(idFarine);
            System.out.println("Stock après achat : " + farineApresAchat.getStock());

            // Test TD4.2 : Mouvement de vente (OUT)
            System.out.println("\nTest TD4.2 : Vente 2.5 KG de Farine");
            StockMovement vente = new StockMovement(idFarine, 2.5, "KG", "OUT");
            dr.addStockMovement(vente);
            System.out.println("Mouvement ajouté : " + vente);

            Ingredient farineApresVente = dr.findIngredientById(idFarine);
            System.out.println("Stock après vente : " + farineApresVente.getStock());

            // Test TD4.3 : Historique des mouvements
            System.out.println("\nTest TD4.3 : Historique des mouvements pour la Farine");
            List<StockMovement> historique = dr.findStockMovementsByIngredientId(idFarine);
            System.out.println("Historique (" + historique.size() + " mouvements) :");
            for (StockMovement m : historique) {
                System.out.println(" - " + m);
            }
        } else {
            System.out.println("Aucun nouvel ingrédient créé → impossible de tester TD4");
        }

        System.out.println("\n=== FIN DES TESTS TD4 ===\n");

        // =============================================================================
        // PARTIE 3 : TESTS ANNEXE TD4 (Gestion des commandes)
        // =============================================================================
        System.out.println("=== PARTIE 3 : TESTS ANNEXE TD4 (Commandes) ===");

        // Test Annexe 1 : Commande valide
        System.out.println("Test Annexe 1 : Commande valide (2 plats)");
        Order commandeValide = new Order();
        commandeValide.setReference("ORD00001");
        commandeValide.setTotalHT(11500.0);
        commandeValide.setTotalTTC(12650.0);

        DishOrder l1 = new DishOrder();
        l1.setIdDish(1);  // Salade fraîche
        l1.setQuantity(2);

        DishOrder l2 = new DishOrder();
        l2.setIdDish(4);  // Gâteau au chocolat
        l2.setQuantity(1);

        commandeValide.addDishOrder(l1);
        commandeValide.addDishOrder(l2);

        try {
            Order saved = dr.saveOrder(commandeValide);
            System.out.println("Commande sauvegardée avec succès :");
            System.out.println(saved);
            System.out.println("Lignes : " + saved.getDishOrders());
        } catch (Exception e) {
            System.out.println("Erreur sauvegarde commande valide : " + e.getMessage());
        }

        // Test Annexe 2 : Recherche par référence
        System.out.println("\nTest Annexe 2 : Recherche par référence");
        try {
            Order found = dr.findOrderByReference("ORD00001");
            System.out.println("Commande retrouvée :");
            System.out.println(found);
        } catch (Exception e) {
            System.out.println("Erreur recherche : " + e.getMessage());
        }

        // Test Annexe 3 : Référence introuvable
        System.out.println("\nTest Annexe 3 : Référence introuvable");
        try {
            dr.findOrderByReference("ORD99999");
        } catch (Exception e) {
            System.out.println("Erreur attendue : " + e.getMessage());
        }

        // Test Annexe 4 : Commande vide
        System.out.println("\nTest Annexe 4 : Commande vide");
        Order commandeVide = new Order();
        commandeVide.setReference("ORD00002");
        commandeVide.setTotalHT(0.0);
        commandeVide.setTotalTTC(0.0);
        try {
            dr.saveOrder(commandeVide);
        } catch (Exception e) {
            System.out.println("Erreur attendue : " + e.getMessage());
        }

        // Test Annexe 5 : Stock insuffisant
        System.out.println("\nTest Annexe 5 : Stock insuffisant");
        Order commandeStock = new Order();
        commandeStock.setReference("ORD00003");
        commandeStock.setTotalHT(50000.0);
        commandeStock.setTotalTTC(55000.0);

        DishOrder ligneCritique = new DishOrder();
        ligneCritique.setIdDish(2);  // Poulet grillé
        ligneCritique.setQuantity(100);  // Quantité énorme

        commandeStock.addDishOrder(ligneCritique);

        try {
            dr.saveOrder(commandeStock);
        } catch (Exception e) {
            System.out.println("Erreur attendue (stock insuffisant) : " + e.getMessage());
        }

        System.out.println("\n=== FIN DE TOUS LES TESTS (TD3 + TD4 + ANNEXE) ===");
    }
}

